package io.github.willchantech.forge.log.masking.dynamic.config.center.domain.service;

import io.github.willchantech.forge.log.masking.dynamic.config.center.domain.model.valobj.AttributeVO;

/**
 * 动态配置中心服务接口
 * @author willchan-tech
 * 2025-04-19 09:54
 */
public interface IDynamicConfigCenterService {

    Object proxyObject(Object bean);

    /**
     * 调整属性值：
     *      外部需要对属性值进行修改
     */
    void adjustAttributeValue(AttributeVO attributeVO);

}
