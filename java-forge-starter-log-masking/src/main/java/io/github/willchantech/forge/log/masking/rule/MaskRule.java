package io.github.willchantech.forge.log.masking.rule;

/**
 * @Desc :
 *      使用者想重写框架现有的某个规则的时候，可以定义一个 spring 类，然后继承抽象类，例如 AbstractIdCardMaskRule，AbstractPhoneMaskRule...
 *      若使用者想定义新的日志脱敏规则，直接实现 MaskRule 接口。
 * @Author : Will Chan
 * @Date : 2026/4/27 15:55
 */
public interface MaskRule {
    /**
     * 默认返回 true。当用户自定义一个 rule 的时候，默认就是 “开启” 支持的。
     * @param flags
     * @return
     */
    default boolean supports(long flags) { return true; }

    String mask(String text);

}
