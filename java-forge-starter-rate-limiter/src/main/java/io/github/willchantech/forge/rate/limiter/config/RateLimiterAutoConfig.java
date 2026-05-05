package io.github.willchantech.forge.rate.limiter.config;

import io.github.willchantech.forge.log.masking.design.framework.link.model2.chain.BusinessLinkedList;
import io.github.willchantech.forge.rate.limiter.aop.RateLimiterAOP;
import io.github.willchantech.forge.rate.limiter.factory.RateLimiterChainFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 限流 spring 配置类
 *
 * @author willchan-tech
 * 2025-05-07 14:16
 */
@Configuration
@EnableConfigurationProperties(RateLimiterAutoProperties.class)
public class RateLimiterAutoConfig {

    @Bean
    public RateLimiterAOP rateLimiterAOP(BusinessLinkedList<String, RateLimiterChainFactory.DynamicContext, String> rateLimiterChain, RateLimiterAutoProperties rateLimiterAutoProperties) {
        return new RateLimiterAOP( rateLimiterChain, rateLimiterAutoProperties);
    }

}
