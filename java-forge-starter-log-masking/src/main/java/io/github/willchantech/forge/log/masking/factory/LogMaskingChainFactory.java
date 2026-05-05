package io.github.willchantech.forge.log.masking.factory;

import io.github.willchantech.forge.log.masking.config.LogMaskingAutoProperties;
import io.github.willchantech.forge.log.masking.design.framework.link.model2.DynamicContext;
import io.github.willchantech.forge.log.masking.design.framework.link.model2.LinkArmory;
import io.github.willchantech.forge.log.masking.design.framework.link.model2.chain.BusinessLinkedList;
import io.github.willchantech.forge.log.masking.filter.LogMaskFilter;
import io.github.willchantech.forge.log.masking.filter.PrepareFilter;
import io.github.willchantech.forge.log.masking.rule.MaskRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Desc : 日志脱敏责任链 spring bean 工厂
 * @Author : Will Chan
 * @Date : 2026/4/27 13:43
 */
@Configuration
public class LogMaskingChainFactory {

    @Bean
    public PrepareFilter prepareFilter() {
        return new PrepareFilter();
    }

    @Bean
    public LogMaskFilter logMaskFilter(List<MaskRule> logMaskRules, LogMaskingAutoProperties properties) {
        return new LogMaskFilter(logMaskRules, properties);
    }

    @Bean("logMaskingChain")
    public BusinessLinkedList<String, DynamicContext, String> logMaskingChain(PrepareFilter prepareFilter, LogMaskFilter logMaskFilter) {
        // 组装链
        LinkArmory<String, DynamicContext, String> linkArmory = new LinkArmory<>(
                "日志处理链",
                prepareFilter,
                logMaskFilter
        );
        // 链对象
        return linkArmory.getLogicLink();
    }

    public static class DynamicContext extends io.github.willchantech.forge.log.masking.design.framework.link.model2.DynamicContext {
        public String processedLog;
    }
}
