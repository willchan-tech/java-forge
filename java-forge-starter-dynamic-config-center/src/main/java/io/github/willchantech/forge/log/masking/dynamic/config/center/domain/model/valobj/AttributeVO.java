package io.github.willchantech.forge.log.masking.dynamic.config.center.domain.model.valobj;

/**
 * redis 发布/订阅的消息体对象。主要用于后期动态修改 java 对象的属性值。
 * @author willchan-tech
 */
public class AttributeVO {

    /** 键 - 属性 fileName */
    private String attribute;

    /** 值 */
    private String value;

    public AttributeVO() {
    }

    public AttributeVO(String attribute, String value) {
        this.attribute = attribute;
        this.value = value;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
