package io.github.willchantech.forge.rate.limiter.factory;

import io.github.willchantech.forge.log.masking.design.framework.link.model2.LinkArmory;
import io.github.willchantech.forge.log.masking.design.framework.link.model2.chain.BusinessLinkedList;
import io.github.willchantech.forge.rate.limiter.filter.GlobalRateLimitFilter;
import io.github.willchantech.forge.rate.limiter.filter.IPRateLimitFilter;
import io.github.willchantech.forge.rate.limiter.filter.KeyRateLimitFilter;
import io.github.willchantech.forge.rate.limiter.types.annotations.GlobalLimiterRule;
import io.github.willchantech.forge.rate.limiter.types.annotations.IPLimiterRule;
import io.github.willchantech.forge.rate.limiter.types.annotations.KeyLimiterRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Desc : 限流过滤责任链组装工厂
 * @Author : Will Chan
 * @Date : 2026/4/30 0:06
 */
@Configuration
public class RateLimiterChainFactory {
    @Bean
    public GlobalRateLimitFilter globalRateLimitFilter() {
        return new GlobalRateLimitFilter();
    }
    @Bean
    public IPRateLimitFilter ipRateLimitFilter() {
        return new IPRateLimitFilter();
    }
    @Bean
    public KeyRateLimitFilter keyRateLimitFilter() {
        return new KeyRateLimitFilter();
    }

    @Bean("rateLimiterChain")
    public BusinessLinkedList<String, DynamicContext, String> rateLimiterChain(GlobalRateLimitFilter globalRateLimitFilter, IPRateLimitFilter ipRateLimitFilter, KeyRateLimitFilter keyRateLimitFilter) {
        // 组装链
        LinkArmory<String, DynamicContext, String> linkArmory = new LinkArmory<>(
                "限流处理链",
                globalRateLimitFilter,
                ipRateLimitFilter,
                keyRateLimitFilter
        );
        // 链对象
        return linkArmory.getLogicLink();
    }
    public static class DynamicContext extends io.github.willchantech.forge.log.masking.design.framework.link.model2.DynamicContext {

        private GlobalLimiterRule globalLimiterRule;
        private IPLimiterRule ipLimiterRule;
        private KeyLimiterRule keyLimiterRule;
        private String valueOfKey; // key 限流器对应的值
        private double globalPermitsPerSecond;// global 限流器的限流速率

        public double getGlobalPermitsPerSecond() {
            return globalPermitsPerSecond;
        }

        public void setGlobalPermitsPerSecond(double globalPermitsPerSecond) {
            this.globalPermitsPerSecond = globalPermitsPerSecond;
        }

        public String getValueOfKey() {
            return valueOfKey;
        }

        public void setValueOfKey(String valueOfKey) {
            this.valueOfKey = valueOfKey;
        }

        public GlobalLimiterRule getGlobalLimiterRule() {
            return globalLimiterRule;
        }

        public void setGlobalLimiterRule(GlobalLimiterRule globalLimiterRule) {
            this.globalLimiterRule = globalLimiterRule;
        }

        public IPLimiterRule getIpLimiterRule() {
            return ipLimiterRule;
        }

        public void setIpLimiterRule(IPLimiterRule ipLimiterRule) {
            this.ipLimiterRule = ipLimiterRule;
        }

        public KeyLimiterRule getKeyLimiterRule() {
            return keyLimiterRule;
        }

        public void setKeyLimiterRule(KeyLimiterRule keyLimiterRule) {
            this.keyLimiterRule = keyLimiterRule;
        }
    }
}
