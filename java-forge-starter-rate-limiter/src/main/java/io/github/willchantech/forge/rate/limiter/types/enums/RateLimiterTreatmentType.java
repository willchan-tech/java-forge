package io.github.willchantech.forge.rate.limiter.types.enums;

/**
 * @Desc : 限流处理方式枚举
 * @Author : Will Chan
 * @Date : 2026/5/1 16:32
 */
public enum RateLimiterTreatmentType {
    FREQUENCY_LIMIT("001", "超频次限流"),

    BLACKLIST_LIMIT("002", "黑名单限流"),
    ;

    /**
     * 标识码
     */
    private final String code;

    /**
     * 中文说明
     */
    private final String desc;

    RateLimiterTreatmentType(String code, String desc) {
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
