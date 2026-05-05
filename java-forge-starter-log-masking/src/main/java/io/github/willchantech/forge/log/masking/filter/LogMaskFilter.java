package io.github.willchantech.forge.log.masking.filter;

import io.github.willchantech.forge.log.masking.config.LogMaskingAutoProperties;
import io.github.willchantech.forge.log.masking.design.framework.link.model2.handler.ILogicHandler;
import io.github.willchantech.forge.log.masking.factory.LogMaskingChainFactory;
import io.github.willchantech.forge.log.masking.rule.MaskRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Desc :  对日志进行脱敏
 * @Author : Will Chan
 * @Date : 2026/4/27 14:17
 */
public class LogMaskFilter implements ILogicHandler<String, LogMaskingChainFactory.DynamicContext, String> {

    private LogMaskingAutoProperties properties;

    private final List<MaskRule> logMaskRules;

    public LogMaskFilter(List<MaskRule> logMaskRules, LogMaskingAutoProperties properties) {
        this.logMaskRules = logMaskRules;
        this.properties = properties;
    }

    @Override
    public String apply(String requestParameter, LogMaskingChainFactory.DynamicContext dynamicContext) throws Exception {
        //总开关关闭，结束责任链
        if (!properties.isEnabled()) {
            return stop(requestParameter, dynamicContext, dynamicContext.processedLog);
        }
        String result = dynamicContext.processedLog;
        long flagsNum = properties.toFlagsNum();
        for (MaskRule rule : logMaskRules) {
            //filter 的开关是否开启？
            if (rule.supports(flagsNum)) {
                result = rule.mask(result);
            }
        }
        dynamicContext.processedLog = result;
        return stop(requestParameter, dynamicContext, result);
    }
}
