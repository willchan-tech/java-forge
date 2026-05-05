package io.github.willchantech.forge.log.masking.rule.phone;

/**
 * @Desc :  手机号的日志脱敏规则（默认实现），可以自行覆盖这个规则实现。
 * @Author : Will Chan
 * @Date : 2026/4/27 20:21
 */
public class DefaultPhoneMaskRule extends AbstractPhoneMaskRule {
    /**
     * 13812348888
     * ↓
     * 138****8888
     * @param text
     * @return
     */
    @Override
    public String mask(String text) {
        return text.replaceAll(
                "(?<!\\d)(1[3-9]\\d)\\d{4}(\\d{4})(?!\\d)",
                "$1****$2"
        );
    }
}
