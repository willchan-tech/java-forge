package io.github.willchantech.forge.design.framework.biz.rule02.factory;

import io.github.willchantech.forge.log.masking.design.framework.link.model2.LinkArmory;
import io.github.willchantech.forge.log.masking.design.framework.link.model2.chain.BusinessLinkedList;
import io.github.willchantech.forge.design.framework.biz.rule02.logic.RuleLogic201;
import io.github.willchantech.forge.design.framework.biz.rule02.logic.RuleLogic202;
import io.github.willchantech.forge.design.framework.biz.rule02.logic.RuleLogic203;
import io.github.willchantech.forge.design.framework.biz.rule02.logic.XxxResponse;
import lombok.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

/**
 * @author willchan-tech
 * @description
 * @create 2025-01-18 09:19
 */
@Service
public class Rule02TradeRuleFactory {

    @Bean("demo01")
    public BusinessLinkedList<String, DynamicContext, XxxResponse> demo01(RuleLogic201 ruleLogic201,
                                                                          RuleLogic202 ruleLogic202,
                                                                          RuleLogic203 ruleLogic203) {

        LinkArmory<String, DynamicContext, XxxResponse> linkArmory = new LinkArmory<>("demo01", ruleLogic201, ruleLogic202, ruleLogic203);

        return linkArmory.getLogicLink();
    }

    @Bean("demo02")
    public BusinessLinkedList<String, DynamicContext, XxxResponse> demo02(RuleLogic202 ruleLogic202, RuleLogic203 ruleLogic203) {

        LinkArmory<String, DynamicContext, XxxResponse> linkArmory = new LinkArmory<>("demo02", ruleLogic202, ruleLogic203);

        return linkArmory.getLogicLink();
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicContext extends io.github.willchantech.forge.log.masking.design.framework.link.model2.DynamicContext {
        private String age;
    }

}
