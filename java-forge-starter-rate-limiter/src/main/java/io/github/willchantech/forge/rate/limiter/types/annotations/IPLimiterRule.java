package io.github.willchantech.forge.rate.limiter.types.annotations;

import java.lang.annotation.*;

/**
 * @Desc : IP 类型规则对象
 * @Author : Will Chan
 * @Date : 2026/4/30 11:40
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface IPLimiterRule {

    double permitsPerSecond();

    double blacklistCount() default 0;
}

