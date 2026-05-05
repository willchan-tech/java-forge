package io.github.willchantech.forge.rate.limiter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Desc : 限流自动配置属性
 * @Author : Will Chan
 * @Date : 2026/5/1 14:17
 */
@ConfigurationProperties(prefix = "java-forge.rate-limiter.global", ignoreInvalidFields = true)
public class RateLimiterAutoProperties {
    /**
     * 全局每秒许可数，<=0 表示非法参数。
     */
    private double permitsPerSecond = 0D; //spring 的松散绑定：驼峰命名法也可以识别 permits-per-second 这种格式。

    public double getPermitsPerSecond() {
        return permitsPerSecond;
    }

    public void setPermitsPerSecond(double permitsPerSecond) {
        this.permitsPerSecond = permitsPerSecond;
    }
}
