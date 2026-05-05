package io.github.willchantech.forge.log.masking.design.framework.link.model2.handler;

import io.github.willchantech.forge.log.masking.design.framework.link.model2.DynamicContext;

/**
 * @author willchan-tech
 * @description 逻辑处理器
 * @create 2025-01-18 09:43
 */
public interface ILogicHandler<T, D extends DynamicContext, R> {

    default R next(T requestParameter, D dynamicContext) {
        dynamicContext.setJump(false);
        dynamicContext.setProceed(true);
        return null;
    }

    default R stop(T requestParameter, D dynamicContext, R result) {
        dynamicContext.setJump(false);
        dynamicContext.setProceed(false);
        return result;
    }

    default R jump(T requestParameter, D dynamicContext, R result) {
        dynamicContext.setJump(true);
        dynamicContext.setProceed(true);
        return result;
    }

    R apply(T requestParameter, D dynamicContext) throws Exception;

    default R applyBefore(T requestParameter, D dynamicContext) throws Exception {
        dynamicContext.setJump(false);
        return null;
    }

    default void applyAfter(T requestParameter, D dynamicContext, R result) throws Exception {
    }

    default void applyAfterException(T requestParameter, D dynamicContext, Exception e) throws Exception {
    }

}
