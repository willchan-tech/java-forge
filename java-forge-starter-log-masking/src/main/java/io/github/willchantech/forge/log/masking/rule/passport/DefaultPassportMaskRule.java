package io.github.willchantech.forge.log.masking.rule.passport;

import io.github.willchantech.forge.log.masking.config.MaskFlags;
import io.github.willchantech.forge.log.masking.rule.MaskRule;

/**
 * @Desc :  护照号码的日志脱敏规则（默认实现），可以自行覆盖这个规则实现。
 * @Author : Will Chan
 * @Date : 2026/4/27 16:10
 */
public class DefaultPassportMaskRule extends AbstractPassportMaskRule {

    @Override
    public String mask(String text) {
        //前面不能是字母数字，后面也不能是字母和数字。防止边界污染，因此必须是独立字段，而不是别人字符串的一部分。
        return text.replaceAll(
                "(?<![A-Za-z0-9])([EeGg])([A-Za-z0-9]{4})([A-Za-z0-9]{4})(?![A-Za-z0-9])",
                "$1****$3"
        );
    }
}
