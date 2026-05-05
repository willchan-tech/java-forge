package io.github.willchantech.forge.log.masking.rule.email;

import io.github.willchantech.forge.log.masking.config.MaskFlags;
import io.github.willchantech.forge.log.masking.rule.MaskRule;

/**
 * @Desc :  邮箱的掩码规则
 * @Author : Will Chan
 * @Date : 2026/4/27 16:09
 */
public abstract class AbstractEmailMaskRule implements EmailMaskRule {

    @Override
    public boolean supports(long flags) {
        return (flags & MaskFlags.EMAIL) != 0;
    }

}
