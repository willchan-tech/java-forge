package io.github.willchantech.forge.rate.limiter.types.enums;

/**
 * @Desc : 限流类型枚举
 * @Author : Will Chan
 * @Date : 2026/4/30 12:20
 */
public enum RateLimiterRuleType {

    GLOBAL("GLOBAL_LIMIT", "全局限流"),

    IP("IP_LIMIT", "IP限流"),

    KEY("KEY_LIMIT", "指定Key限流");

    /**
     * 错误码 / 标识码
     */
    private final String code;

    /**
     * 中文说明
     */
    private final String desc;

    RateLimiterRuleType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
