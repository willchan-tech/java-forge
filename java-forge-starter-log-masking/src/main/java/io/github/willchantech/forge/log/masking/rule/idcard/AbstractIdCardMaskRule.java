package io.github.willchantech.forge.log.masking.rule.idcard;

import io.github.willchantech.forge.log.masking.config.MaskFlags;
import io.github.willchantech.forge.log.masking.rule.MaskRule;

/**
 * @Desc :  中国身份证的掩码规则
 * @Author : Will Chan
 * @Date : 2026/4/27 16:04
 */
public abstract class AbstractIdCardMaskRule implements IdCardMaskRule {
    @Override
    public boolean supports(long flags) {
        return (flags & MaskFlags.ID_CARD) != 0;
    }

}
