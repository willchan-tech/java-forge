package io.github.willchantech.forge.log.masking.rule.email;

import io.github.willchantech.forge.log.masking.config.MaskFlags;
import io.github.willchantech.forge.log.masking.rule.MaskRule;

/**
 * @Desc :  邮箱的日志脱敏规则（默认实现），可以自行覆盖这个规则实现。
 * @Author : Will Chan
 * @Date : 2026/4/27 16:09
 */
public class DefaultEmailMaskRule extends AbstractEmailMaskRule {

    /**
     * abc123@gmail.com
     * ↓
     * a***@gmail.com
     * @param text
     * @return
     */
    @Override
    public String mask(String text) {
        return text.replaceAll(
                "(?<![A-Za-z0-9._%+-])([A-Za-z0-9])[A-Za-z0-9._%+-]*(@[A-Za-z0-9.-]+\\.[A-Za-z]{2,})(?![A-Za-z0-9._%+-])",
                "$1***$2"
        );
    }
}
