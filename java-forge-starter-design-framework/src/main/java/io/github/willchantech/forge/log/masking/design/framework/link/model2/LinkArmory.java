package io.github.willchantech.forge.log.masking.design.framework.link.model2;

import io.github.willchantech.forge.log.masking.design.framework.link.model2.chain.BusinessLinkedList;
import io.github.willchantech.forge.log.masking.design.framework.link.model2.handler.ILogicHandler;

/**
 * @author willchan-tech
 * @description 链路装配
 * @create 2025-01-18 10:02
 */
public class LinkArmory<T, D extends DynamicContext, R> {

    private final BusinessLinkedList<T, D, R> logicLink;

    @SafeVarargs
    public LinkArmory(String linkName, ILogicHandler<T, D, R>... logicHandlers) {
        logicLink = new BusinessLinkedList<>(linkName);
        for (ILogicHandler<T, D, R> logicHandler: logicHandlers){
            logicLink.add(logicHandler);
        }
    }

    public BusinessLinkedList<T, D, R> getLogicLink() {
        return logicLink;
    }

}
