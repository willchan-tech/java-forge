package io.github.willchantech.forge.log.masking.rule.passport;

import io.github.willchantech.forge.log.masking.config.MaskFlags;
import io.github.willchantech.forge.log.masking.rule.MaskRule;

/**
 * @Desc :  中国护照的掩码规则
 * @Author : Will Chan
 * @Date : 2026/4/27 16:10
 */
public abstract class AbstractPassportMaskRule implements PassportMaskRule {

    @Override
    public boolean supports(long flags) {
        return (flags & MaskFlags.PASSPORT) != 0;
    }

}
