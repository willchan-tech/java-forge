package io.github.willchantech.forge.log.masking.dynamic.config.center.domain.service;

import io.github.willchantech.forge.log.masking.dynamic.config.center.config.properties.DynamicConfigCenterAutoProperties;
import io.github.willchantech.forge.log.masking.dynamic.config.center.domain.model.valobj.AttributeVO;
import io.github.willchantech.forge.log.masking.dynamic.config.center.types.annotations.DCCValue;
import io.github.willchantech.forge.log.masking.dynamic.config.center.types.common.Constants;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicConfigCenterService implements IDynamicConfigCenterService {

    private final Logger log = LoggerFactory.getLogger(DynamicConfigCenterService.class);

    private final DynamicConfigCenterAutoProperties properties;

    private final RedissonClient redissonClient;
    /**
     * dccBeanGroup 是共享资源，必须留意线程安全的问题（可能有多个管理人员并发去修改里面的值？）
     * 使用 ConcurrentHashMap:
     *           如果一个线程的 put 已经完成，另一个线程之后执行 get，是不会读到旧值的。（保证可见性）
     */
    private final Map<String, Object> dccBeanGroup = new ConcurrentHashMap<>();

    public DynamicConfigCenterService(DynamicConfigCenterAutoProperties properties, RedissonClient redissonClient) {
        this.properties = properties;
        this.redissonClient = redissonClient;
    }

    @Override
    public Object proxyObject(Object bean) {
        Class<?> targetBeanClass = bean.getClass();
        Object targetBeanObject = bean;
        // 注意；增加 AOP 代理后，获得本真类的方式要通过 AopProxyUtils.getTargetClass(bean); 不能直接 bean.class 因为代理后类的结构发生变化，这样不能获得到自己的自定义注解了。
        if (AopUtils.isAopProxy(bean)) {
            targetBeanClass = AopUtils.getTargetClass(bean);
            targetBeanObject = AopProxyUtils.getSingletonTarget(bean);
        }

        Field[] fields = targetBeanClass.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(DCCValue.class)) {
                continue;
            }

            DCCValue dccValue = field.getAnnotation(DCCValue.class);

            String value = dccValue.value();
            if (StringUtils.isBlank(value)) {
                throw new RuntimeException("java-forge dynamic-config: " + field.getName() + " @DCCValue is not config value,config case 「isSwitch/isSwitch:1」");
            }

            String[] splits = value.split(Constants.SYMBOL_COLON);
            // 拼接 key（格式）： yml 文件里配置的 【system】 + "_" + 【splits[0]】
            String key = properties.buildKey(splits[0].trim());

            String defaultValue = splits.length == 2 ? splits[1] : null;

            // 设置值
            String setValue = defaultValue;

            try {
                // 如果为空则抛出异常
                if (StringUtils.isBlank(defaultValue)) {
                    throw new RuntimeException("java-forge dynamic-config: " + "dcc config error - " + key + " is null - 请配置默认值！");
                }

                // Redis 操作，判断配置Key是否存在，不存在则创建，存在则获取最新值
                RBucket<String> bucket = redissonClient.getBucket(key); // 获取 key 的句柄，这一步不会发网络请求。
                boolean exists = bucket.isExists();
                if (!exists) {
                    bucket.set(defaultValue);
                } else {
                    setValue = bucket.get();
                }
                //setAccessible(true) 本质是：关闭 Java 语言访问控制检查。它不会改变字段本身的访问修饰符。仅仅意味着: 可以访问修改私有的字段。
                field.setAccessible(true);
                field.set(targetBeanObject, setValue);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            dccBeanGroup.put(key, targetBeanObject);
        }

        return bean;
    }

    /**
     * 动态配置：手动调整属性值
     * @param attributeVO 属性信息
     */
    @Override
    public void adjustAttributeValue(AttributeVO attributeVO) {
        // 属性信息
        String key = properties.buildKey(attributeVO.getAttribute());
        String value = attributeVO.getValue();

        // 设置值
        RBucket<String> bucket = redissonClient.getBucket(key);
        boolean exists = bucket.isExists();
        if (!exists) {
            log.error("java-forge dynamic-config 丢失 redis 缓存key：{}", key);
            return;
        }
        bucket.set(attributeVO.getValue()); //更新值到 redis
        // 更新值到 jvm 缓存
        Object objBean = dccBeanGroup.get(key);
        if (null == objBean) {
            log.error("java-forge dynamic-config 丢失 jvm 缓存key：{}", key);
            return;
        }

        Class<?> objBeanClass = objBean.getClass();
        // 检查 objBean 是否是代理对象
        if (AopUtils.isAopProxy(objBean)) {
            // 获取原生类
            objBeanClass = AopUtils.getTargetClass(objBean);
        }

        try {
            // 1. getDeclaredField 方法用于获取指定类中声明的所有字段，包括私有字段、受保护字段和公共字段。
            // 2. getField 方法只能获取到公共访问修饰符（public）的字段。
            Field field = objBeanClass.getDeclaredField(attributeVO.getAttribute());
            field.setAccessible(true);
            field.set(objBean, value);

            log.info("java-forge dynamic-config DCC 节点监听，动态设置值 {} : {}", key, value);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
