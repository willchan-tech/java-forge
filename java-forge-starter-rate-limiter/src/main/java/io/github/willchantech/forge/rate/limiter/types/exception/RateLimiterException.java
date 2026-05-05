package io.github.willchantech.forge.rate.limiter.types.exception;

import io.github.willchantech.forge.rate.limiter.types.enums.RateLimiterRuleType;

/**
 * @Desc : 限流异常
 *      注：
 *          限流异常会被直接抛出去，不会被处理。业务层可以通过捕获该类型异常来进行后续的处理。
 * @Author : Will Chan
 * @Date : 2026/4/30 18:15
 */
public class RateLimiterException extends RuntimeException {

    /**
     * 被哪种规则拦截
     */
    private final RateLimiterRuleType type;

    /**
     * 业务状态码（可选）
     */
    private final String code;

    public RateLimiterException(RateLimiterRuleType type) {
        super( "java-forge rate-limiter rate limit triggered: " + type.getCode());
        this.type = type;
        this.code = "000";
    }

    public RateLimiterException(RateLimiterRuleType type, String code) {
        super( "java-forge rate-limiter rate limit triggered: " + type.getCode());
        this.type = type;
        this.code = code;
    }

    public RateLimiterRuleType getType() {
        return type;
    }

    public String getCode() {
        return code;
    }
}
