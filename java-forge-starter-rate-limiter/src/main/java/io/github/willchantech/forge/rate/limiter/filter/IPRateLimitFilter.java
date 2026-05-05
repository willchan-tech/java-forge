package io.github.willchantech.forge.rate.limiter.filter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import io.github.willchantech.forge.log.masking.design.framework.link.model2.handler.ILogicHandler;
import io.github.willchantech.forge.rate.limiter.types.enums.RateLimiterRuleType;
import io.github.willchantech.forge.rate.limiter.factory.RateLimiterChainFactory;
import io.github.willchantech.forge.rate.limiter.types.annotations.IPLimiterRule;
import io.github.willchantech.forge.rate.limiter.types.enums.RateLimiterTreatmentType;
import io.github.willchantech.forge.rate.limiter.types.exception.RateLimiterException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * @Desc : IP 限流过滤器
 * @Author : Will Chan
 * @Date : 2026/4/30 13:28
 */
public class IPRateLimitFilter implements ILogicHandler<String, RateLimiterChainFactory.DynamicContext, String> {

    private static final Logger logger = LoggerFactory.getLogger(IPRateLimitFilter.class);

    /**
     * 为了避免令牌桶失真，将过期时间从 1 秒钟延长至 30 分钟。
     * 同时为了避免高并发下的内存占用过大（创建了太多 RateLimiter 对象），将最大数量设置为 10000。
     * 当缓存数量超过 10000 时，Guava 会按策略淘汰旧 entry（近似 LRU），将不活跃的缓存剔除，然后被新的取代。
     */
    private final Cache<String, RateLimiter> loginRecord = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterAccess(30, TimeUnit.MINUTES) // expireAfterAccess -- 30 分钟没有任何访问，才会被删除 entry（铲除冷门用户）。
            .build();

    // 个人限频黑名单24h - 使用 google guava 的本地缓存。若是分布式业务场景，可以记录到 Redis 中
    /**
     * Cache<String, Long>:
     *      key:    ip
     *      value:  限流次数
     */
    private final Cache<String, Long> blacklist = CacheBuilder.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS) // 每个<key, value>元素自 put 进去后，就开始计时，在 24 小时后会被删除
            .build();

    @Override
    public String apply(String requestParameter, RateLimiterChainFactory.DynamicContext dynamicContext) throws Exception {
        //无 ip 配置，跳过
        IPLimiterRule ipLimiterRule = dynamicContext.getIpLimiterRule();
        if (ipLimiterRule == null) {
            return next(requestParameter, dynamicContext);
        }
        //获取 ip
        String ip = getClientIp();
        if ("unknown".equals( ip) || StringUtils.isBlank( ip)) {
            logger.warn("java-forge rate-limiter cannot resolve client ip, skip ip limiter: {}", ip);
            //拿不到 ip，则跳过，不作限流处理。不误伤用户。
            return next(requestParameter, dynamicContext);
        }
        // 黑名单拦截
        if (
            ipLimiterRule.blacklistCount() != 0 // 开启了黑名单
            && null != blacklist.getIfPresent(ip) // 在黑名单中
            && blacklist.getIfPresent(ip) >= ipLimiterRule.blacklistCount() // 次数达到/超过黑名单阈值
        )
        {
            logger.info("java-forge 限流-IP黑名单拦截(24h)：{}", ip);
            throw new RateLimiterException(RateLimiterRuleType.IP, RateLimiterTreatmentType.BLACKLIST_LIMIT.getCode());
        }
        /**
         *  Guava 的 RateLimiter 本质是 令牌桶（Token Bucket）算法实现对象。
         *  Guava 提供的限流器：每秒生成多少个令牌（每秒允许多少个该用户的请求通过）
         *  注：
         *       get(K key, java. util. concurrent. Callable<? extends V> loader ) 是 Guava 提供的支持线程安全创建单例对象的方法。
         *       Get 到，返回。当 Get 不到时，同一个 key 的并发加载会协调，避免重复加载，然后 put 到缓存中。
         */
        RateLimiter rateLimiter = loginRecord.get(ip, () -> RateLimiter.create(ipLimiterRule.permitsPerSecond()));

        // 限流拦截
        /**
         * Guava 的令牌桶原理：
         *      1. 系统按固定速率往桶里放令牌（比如：1个/秒）
         *      2. 每次请求必须拿一个令牌
         *      3. 拿到 → 放行
         *      4. 没拿到 → 拒绝（限流）
         */
        if (!rateLimiter.tryAcquire()) {
            // 1 秒钟内尝试获取的频率过高，触发黑名单记账 1 次, 同时限制访问。累积记账 N 次后会被限制访问一段很长的时间，直到解锁。
            if (ipLimiterRule.blacklistCount() != 0) { // 开启了黑名单
                //限流记账次数
                Long count = blacklist.getIfPresent(ip);
                long chargeCount = blacklist.getIfPresent(ip) == null ? 1L : count + 1;
                blacklist.put(ip, chargeCount);
            }
            logger.info("java-forge 限流-IP超频次拦截：{}", ip);
            throw new RateLimiterException(RateLimiterRuleType.IP, RateLimiterTreatmentType.FREQUENCY_LIMIT.getCode());
        }
        return next(requestParameter, dynamicContext);
    }

    /**
     * 获取客户端 ip
     * @return ip
     */
    private static String getClientIp() {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return "unknown";
        }
        HttpServletRequest request = attributes.getRequest();
        String ip = extractIp(request);
        //本地调试会获取到 IPv6 localhost，因此转为可以识别的 IPv4。生产线上运行不会有这种情况，通常能得到 183.x.x.x，45.x.x.x，2408:xxxx:xxxx 这种。
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    private static String extractIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }
}
