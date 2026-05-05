package io.github.willchantech.forge.log.masking.dynamic.config.center.types.annotations;

import java.lang.annotation.*;

/**
 * 注解，动态配置中心标记
 *
 * @author willchan-tech
 * 2025年04月19日09:51:38
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented //控制注解是否包含在 JavaDoc 文档中。
public @interface DCCValue {
    /**
     *  @DCCValue("scBlacklist:s02c02") ：
     *      value 的值以冒号分割，第一个字段为 key（同时也作为 redis 的 key 保存），第二个字段为默认值。
     */
    String value() default "";

}
