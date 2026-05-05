package io.github.willchantech.forge.rate.limiter.aop;

import io.github.willchantech.forge.log.masking.design.framework.link.model2.chain.BusinessLinkedList;
import io.github.willchantech.forge.log.masking.dynamic.config.center.types.annotations.DCCValue;
import io.github.willchantech.forge.rate.limiter.config.RateLimiterAutoProperties;
import io.github.willchantech.forge.rate.limiter.factory.RateLimiterChainFactory;
import io.github.willchantech.forge.rate.limiter.types.annotations.GlobalLimiterRule;
import io.github.willchantech.forge.rate.limiter.types.annotations.IPLimiterRule;
import io.github.willchantech.forge.rate.limiter.types.annotations.KeyLimiterRule;
import io.github.willchantech.forge.rate.limiter.types.exception.RateLimiterException;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 限流切面
 * @author willchan-tech
 * 2025-05-07 14:18
 */
@Aspect
public class RateLimiterAOP {

    private final Logger logger = LoggerFactory.getLogger(RateLimiterAOP.class);

    @DCCValue("rateLimiterSwitch:open")
    private String rateLimiterSwitch;

    private BusinessLinkedList<String, RateLimiterChainFactory.DynamicContext, String> chain;
    private RateLimiterAutoProperties properties;
    public RateLimiterAOP(BusinessLinkedList<String, RateLimiterChainFactory.DynamicContext, String> chain, RateLimiterAutoProperties properties) {
        this.chain = chain;
        this.properties = properties;
    }

    /**
     * 标注了任意一个注解，则进入切面
     */
    @Pointcut(
        "@annotation(io.github.willchantech.forge.rate.limiter.types.annotations.GlobalLimiterRule) || " +
        "@annotation(io.github.willchantech.forge.rate.limiter.types.annotations.IPLimiterRule) || " +
        "@annotation(io.github.willchantech.forge.rate.limiter.types.annotations.KeyLimiterRule)"
    )
    public void aopPoint() {
    }

    /**
     * 满足切面条件，进入限流流程。
     *      注：
     *          多个限流注解类型可以叠加到一个方法上，目前已有的限流类型：Global、IP、Key。
     *          过滤器的优先级次序分别是：Global、IP、Key。
     *          所有的限流器都不区分归属哪个接口，默认所有的接口共享限流器。
     *              例如：
     *                  用户在短时间内分别访问了接口 A，B，C，且三个接口都有相同的限流注解，则所有的访问行为会被一起统计。
     *          Global 限流：
     *              所有标注了 Global 注解的接口，都会共享一个限流器，这是系统级别的调节器，根据流量的波动，实现动态的阻断 / 恢复。
     *          IP 限流：
     *              根据 IP 地址进行限流，每个 IP 地址对应一个限流器，若某个 IP 访问频次过高，会被限流，同时记账黑名单（开始倒计时），达到一定的记账次数，会一直被限流，要等到倒计时结束才能继续访问。
     *          Key 限流：
     *              根据入参 key 进行限流，每个 key 的值对应一个限流器，若某个 key 访问频次过高，会被限流，同时记账黑名单（开始倒计时），达到一定的记账次数，会一直被限流，要等到倒计时结束才能继续访问。
     */
    @Around("aopPoint()")
    public Object doRouter(ProceedingJoinPoint jp) throws Throwable {
        // 0. 限流开关【open 开启、close 关闭】关闭后，不会走限流策略
        if (StringUtils.isBlank(rateLimiterSwitch) || "close".equals(rateLimiterSwitch)) {
            return jp.proceed();
        }
        MethodSignature signature = (MethodSignature) jp.getSignature();
        //拿到真实的方法，而非代理类的方法
        Method method = jp.getTarget()
                .getClass()
                .getMethod(
                    signature.getName(),
                    signature.getParameterTypes()
                );
        GlobalLimiterRule globalAnno = method.getAnnotation(GlobalLimiterRule.class);
        IPLimiterRule ipAnno = method.getAnnotation(IPLimiterRule.class);
        KeyLimiterRule keyAnno = method.getAnnotation(KeyLimiterRule.class);
        //检查配置参数
        if (globalAnno != null && properties.getPermitsPerSecond() <= 0) {
            throw new IllegalArgumentException("java-forge 限流-GlobalLimiterRule限流参数配置错误：请配置限流器限流速率。");
        }
        //启动限流过滤流程
        RateLimiterChainFactory.DynamicContext ctx = new RateLimiterChainFactory.DynamicContext();
        ctx.setGlobalLimiterRule( globalAnno);
        if (globalAnno != null) {
            ctx.setGlobalPermitsPerSecond(properties.getPermitsPerSecond());
        }
        ctx.setIpLimiterRule( ipAnno);
        ctx.setKeyLimiterRule( keyAnno);
        if (keyAnno != null) {
            // 获取拦截字段
            String valueOfKey = getAttrValue(keyAnno.key(), jp.getArgs());
            ctx.setValueOfKey(valueOfKey);
        }
        try {
            chain.apply(null, ctx);
        } catch (RateLimiterException e) {
            logger.info("java-forge 限流-限流拦截成功：{}", e.getType().getCode() + ":" + e.getCode());
            throw e;
        }
        // 返回结果
        return jp.proceed();
    }

    /**
     * 实际根据自身业务调整，主要是为了获取通过某个值做拦截
     */
    public String getAttrValue(String attr, Object[] args) {
        //如果第一个参数就是字符串，直接返回
        if (args[0] instanceof String) {
            return args[0].toString();
        }
        String filedValue = null;
        for (Object arg : args) {
            try {
                if (StringUtils.isNotBlank(filedValue)) {
                    break;
                }
                // filedValue = BeanUtils.getProperty(arg, attr);
                // fix: 使用lombok时，uId这种字段的get方法与idea生成的get方法不同，会导致获取不到属性值，改成反射获取解决
                filedValue = String.valueOf(this.getValueByName(arg, attr));
            } catch (Exception e) {
                logger.error("java-forge 限流-获取路由属性值失败 attr：{}", attr, e);
            }
        }
        return filedValue;
    }

    /**
     * 获取对象的特定属性值
     *
     * @param item 对象
     * @param name 属性名
     * @return 属性值
     * @author tang
     */
    private Object getValueByName(Object item, String name) {
        try {
            Field field = getFieldByName(item, name);
            if (field == null) {
                return null;
            }
            field.setAccessible(true);
            Object o = field.get(item); //从对象 item 身上，把字段 field 当前保存的值取出来
            return o;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * 根据名称获取方法，该方法同时兼顾继承类获取父类的属性
     *
     * @param item 对象
     * @param name 属性名
     * @return 该属性对应方法
     * @author tang
     */
    private Field getFieldByName(Object item, String name) {
        try {
            Field field;
            try {
                field = item.getClass().getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                field = item.getClass().getSuperclass().getDeclaredField(name);
            }
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

}
