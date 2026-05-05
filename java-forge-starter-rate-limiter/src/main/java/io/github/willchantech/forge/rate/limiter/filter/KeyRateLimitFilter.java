package io.github.willchantech.forge.rate.limiter.filter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import io.github.willchantech.forge.log.masking.design.framework.link.model2.handler.ILogicHandler;
import io.github.willchantech.forge.rate.limiter.types.enums.RateLimiterRuleType;
import io.github.willchantech.forge.rate.limiter.factory.RateLimiterChainFactory;
import io.github.willchantech.forge.rate.limiter.types.annotations.KeyLimiterRule;
import io.github.willchantech.forge.rate.limiter.types.enums.RateLimiterTreatmentType;
import io.github.willchantech.forge.rate.limiter.types.exception.RateLimiterException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @Desc : Key 限流过滤器
 * @Author : Will Chan
 * @Date : 2026/4/30 13:28
 */
public class KeyRateLimitFilter implements ILogicHandler<String, RateLimiterChainFactory.DynamicContext, String> {

    private static final Logger logger = LoggerFactory.getLogger(KeyRateLimitFilter.class);

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
     *      key:    valueOfKey
     *      value:  限流次数
     */
    private final Cache<String, Long> blacklist = CacheBuilder.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS) // 每个<key, value>元素自 put 进去后，就开始计时，在 24 小时后会被删除
            .build();

    @Override
    public String apply(String requestParameter, RateLimiterChainFactory.DynamicContext dynamicContext) throws Exception {
        //无 key 配置，跳过
        KeyLimiterRule keyLimiterRule = dynamicContext.getKeyLimiterRule();
        if (keyLimiterRule == null) {
            return stop(requestParameter, dynamicContext, null);
        }
        //获取 value
        String valueOfKey = dynamicContext.getValueOfKey();
        if (StringUtils.isBlank(valueOfKey)) {
            logger.warn("java-forge 限流-Key限流拦截，valueOfKey 为空。");
            return stop(requestParameter, dynamicContext, null);
        }
        // 黑名单拦截
        if (
            keyLimiterRule.blacklistCount() != 0 // 开启了黑名单
            && null != blacklist.getIfPresent(valueOfKey) // 在黑名单中
            && blacklist.getIfPresent(valueOfKey) >= keyLimiterRule.blacklistCount() // 次数达到/超过黑名单阈值
        )
        {
            logger.info("java-forge 限流-Key黑名单拦截(24h)：{}", valueOfKey);
            throw new RateLimiterException(RateLimiterRuleType.KEY, RateLimiterTreatmentType.BLACKLIST_LIMIT.getCode());
        }
        /**
         *  Guava 的 RateLimiter 本质是 令牌桶（Token Bucket）算法实现对象。
         *  Guava 提供的限流器：每秒生成多少个令牌（每秒允许多少个该用户的请求通过）
         *  注：
         *       get(K key, java. util. concurrent. Callable<? extends V> loader ) 是 Guava 提供的支持线程安全创建单例对象的方法。
         *       Get 到，返回。当 Get 不到时，同一个 key 的并发加载会协调，避免重复加载，然后 put 到缓存中。
         */
        RateLimiter rateLimiter = loginRecord.get(valueOfKey, () -> RateLimiter.create(keyLimiterRule.permitsPerSecond()));

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
            if (keyLimiterRule.blacklistCount() != 0) { // 开启了黑名单
                //限流记账次数
                Long count = blacklist.getIfPresent(valueOfKey);
                long chargeCount = blacklist.getIfPresent(valueOfKey) == null ? 1L : count + 1;
                blacklist.put(valueOfKey, chargeCount);
            }
            logger.info("java-forge 限流-Key超频次拦截：{}", valueOfKey);
            throw new RateLimiterException(RateLimiterRuleType.KEY, RateLimiterTreatmentType.FREQUENCY_LIMIT.getCode());
        }
        return stop(requestParameter, dynamicContext, null);
    }
}
