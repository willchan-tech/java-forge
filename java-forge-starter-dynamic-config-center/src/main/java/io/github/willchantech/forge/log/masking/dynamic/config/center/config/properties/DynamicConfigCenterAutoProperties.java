package io.github.willchantech.forge.log.masking.dynamic.config.center.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 动态配置中心配置文件
 *
 * @author willchan-tech
 */
@ConfigurationProperties(prefix = "java-forge.dynamic-config", ignoreInvalidFields = true)
public class DynamicConfigCenterAutoProperties {

    /**
     * 系统名称。用作 redis 发布/订阅的主题的一部分：DYNAMIC_CONFIG_CENTER_REDIS_TOPIC_【system】
     */
    private String system;

    /**
     * 构建 key：【system】_【attributeName】，作为 redis 缓存里的 key，前面先拼接一手 【system】，防重。
     * @param attributeName @DCCValue 里的属性名称
     * @return
     */
    public String buildKey(String attributeName) {
        return this.system + "_" + attributeName;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

}
