package io.github.willchantech.forge.log.masking.dynamic.config.center.domain.processor;

import io.github.willchantech.forge.log.masking.dynamic.config.center.domain.service.IDynamicConfigCenterService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * BeanPostProcessor 是 Spring 框架提供的一个重要的扩展接口，用于在 Spring 容器实例化、配置和初始化 bean 的过程中进行自定义处理。
 */
@Component
public class DynamicConfigCenterProcessor implements BeanPostProcessor {
    //final 表示这个变量只能被赋值一次，赋值后不能再改变引用指向，必须在声明时或构造函数中初始化。
    private final IDynamicConfigCenterService dynamicConfigCenterService;

    // 构造函数注入 bean 对象
    public DynamicConfigCenterProcessor(IDynamicConfigCenterService dynamicConfigCenterService) {
        this.dynamicConfigCenterService = dynamicConfigCenterService;
    }

    /**
     * 这段代码在每个 bean 初始化完成后执行，对 bean 进行动态代理处理
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return dynamicConfigCenterService.proxyObject(bean);
    }

}
