package io.github.willchantech.forge.log.masking.rule.phone;

import io.github.willchantech.forge.log.masking.config.MaskFlags;
import io.github.willchantech.forge.log.masking.rule.MaskRule;

/**
 * @Desc :  手机号码的掩码规则
 * @Author : Will Chan
 * @Date : 2026/4/27 16:08
 */
public abstract class AbstractPhoneMaskRule implements PhoneMaskRule {

    @Override
    public boolean supports(long flags) {
        return (flags & MaskFlags.PHONE) != 0;
    }
}
