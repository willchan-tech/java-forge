package io.github.willchantech.forge.log.masking.dynamic.config.center.listener;

import io.github.willchantech.forge.log.masking.dynamic.config.center.domain.model.valobj.AttributeVO;
import io.github.willchantech.forge.log.masking.dynamic.config.center.domain.service.IDynamicConfigCenterService;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynamicConfigCenterAdjustListener implements MessageListener<AttributeVO> {

    private final Logger log = LoggerFactory.getLogger(DynamicConfigCenterAdjustListener.class);

    private final IDynamicConfigCenterService dynamicConfigCenterService;

    public DynamicConfigCenterAdjustListener(IDynamicConfigCenterService dynamicConfigCenterService) {
        this.dynamicConfigCenterService = dynamicConfigCenterService;
    }

    /**
     * 动态更改属性值采用 redis 发送消息的方式，原因是更改入口不限制在某一个应用中，只要在 redis 覆盖的范围都行。
     * @param charSequence
     * @param attributeVO
     */
    @Override
    public void onMessage(CharSequence charSequence, AttributeVO attributeVO) {
        try {
            log.info("java-forge dcc config attribute:{} ,value:{}", attributeVO.getAttribute(), attributeVO.getValue());
            dynamicConfigCenterService.adjustAttributeValue(attributeVO);
        } catch (Exception e) {
            log.error("java-forge dcc config attribute:{} ,value:{}", attributeVO.getAttribute(), attributeVO.getValue(), e);
        }
    }

}
