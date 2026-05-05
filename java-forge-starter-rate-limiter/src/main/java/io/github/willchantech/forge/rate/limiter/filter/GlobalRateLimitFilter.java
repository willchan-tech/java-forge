package io.github.willchantech.forge.rate.limiter.filter;

import com.google.common.util.concurrent.RateLimiter;
import io.github.willchantech.forge.log.masking.design.framework.link.model2.handler.ILogicHandler;
import io.github.willchantech.forge.rate.limiter.types.enums.RateLimiterRuleType;
import io.github.willchantech.forge.rate.limiter.factory.RateLimiterChainFactory;
import io.github.willchantech.forge.rate.limiter.types.exception.RateLimiterException;

/**
 * @Desc : 全局限流过滤器
 * @Author : Will Chan
 * @Date : 2026/4/30 13:28
 */
public class GlobalRateLimitFilter implements ILogicHandler<String, RateLimiterChainFactory.DynamicContext, String> {

    private volatile RateLimiter limiter; //全局共享一个限流器

    @Override
    public String apply(String requestParameter, RateLimiterChainFactory.DynamicContext dynamicContext) throws Exception {
        //无 global 配置，跳过
        if (dynamicContext.getGlobalLimiterRule() == null) {
            return next(requestParameter, dynamicContext);
        }
        //双重判断加锁创建单例
        if (limiter == null) {
            synchronized (this) {
                if (limiter == null) {
                    limiter = RateLimiter.create(dynamicContext.getGlobalPermitsPerSecond());
                }
            }
        }
        if (!limiter.tryAcquire()) {
            throw new RateLimiterException(RateLimiterRuleType.GLOBAL);
        }

        return next(requestParameter, dynamicContext);
    }
}
