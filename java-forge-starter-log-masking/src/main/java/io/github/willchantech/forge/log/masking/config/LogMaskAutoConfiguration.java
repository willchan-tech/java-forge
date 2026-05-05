package io.github.willchantech.forge.log.masking.config;

import io.github.willchantech.forge.log.masking.adapter.log4j2.Log4j2MaskRewritePolicy;
import io.github.willchantech.forge.log.masking.adapter.logback.LogbackMaskingConverter;
import io.github.willchantech.forge.log.masking.design.framework.link.model2.chain.BusinessLinkedList;
import io.github.willchantech.forge.log.masking.factory.LogMaskingChainFactory;
import io.github.willchantech.forge.log.masking.rule.email.DefaultEmailMaskRule;
import io.github.willchantech.forge.log.masking.rule.email.EmailMaskRule;
import io.github.willchantech.forge.log.masking.rule.idcard.DefaultIdCardMaskRule;
import io.github.willchantech.forge.log.masking.rule.idcard.IdCardMaskRule;
import io.github.willchantech.forge.log.masking.rule.passport.DefaultPassportMaskRule;
import io.github.willchantech.forge.log.masking.rule.passport.PassportMaskRule;
import io.github.willchantech.forge.log.masking.rule.phone.AbstractPhoneMaskRule;
import io.github.willchantech.forge.log.masking.rule.phone.DefaultPhoneMaskRule;
import io.github.willchantech.forge.log.masking.rule.phone.PhoneMaskRule;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Desc :  LogMask spring 配置类
 * @Author : Will Chan
 * @Date : 2026/4/27 16:19
 */
@Configuration
@EnableConfigurationProperties(value = { LogMaskingAutoProperties.class })
public class LogMaskAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(PhoneMaskRule.class)
    public PhoneMaskRule phoneMaskRule() {
        return new DefaultPhoneMaskRule();
    }

    @Bean
    @ConditionalOnMissingBean(IdCardMaskRule.class)
    public IdCardMaskRule idCardMaskRule() {
        return new DefaultIdCardMaskRule();
    }
    @Bean
    @ConditionalOnMissingBean(PassportMaskRule.class)
    public PassportMaskRule passportMaskRule() {
        return new DefaultPassportMaskRule();
    }
    @Bean
    @ConditionalOnMissingBean(EmailMaskRule.class)
    public EmailMaskRule emailMaskRule() {
        return new DefaultEmailMaskRule();
    }

    /**
     * Spring Boot 大致顺序：
     * 1. 创建 Bean
     * 2. 注入依赖
     * 3. InitializingBean / @PostConstruct       ---> 这里执行
     * 4. Context refresh 完成
     * 5. ApplicationRunner / CommandLineRunner
     * 6. 应用 fully started
     *
     * @param logMaskingChain
     * @return
     */
    @Bean
    public InitializingBean  initConverter(BusinessLinkedList<String, LogMaskingChainFactory.DynamicContext, String> logMaskingChain) {
        return () -> LogbackMaskingConverter.setChain(logMaskingChain);
    }
    @Bean
    public InitializingBean initLog4j2Converter(BusinessLinkedList<String, LogMaskingChainFactory.DynamicContext, String> chain) {
        return () -> {
            Log4j2MaskRewritePolicy.setChain(chain);
        };
    }
}
