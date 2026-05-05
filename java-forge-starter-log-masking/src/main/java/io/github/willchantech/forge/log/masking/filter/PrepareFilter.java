package io.github.willchantech.forge.log.masking.filter;

import io.github.willchantech.forge.log.masking.design.framework.link.model2.handler.ILogicHandler;
import io.github.willchantech.forge.log.masking.factory.LogMaskingChainFactory;

/**
 * @Desc : 责任链预处理
 * @Author : Will Chan
 * @Date : 2026/4/27 17:32
 */
public class PrepareFilter  implements ILogicHandler<String, LogMaskingChainFactory.DynamicContext, String> {
    @Override
    public String apply(String requestParameter, LogMaskingChainFactory.DynamicContext dynamicContext) throws Exception {
        dynamicContext.processedLog = requestParameter;
        return next(requestParameter, dynamicContext);
    }
}
