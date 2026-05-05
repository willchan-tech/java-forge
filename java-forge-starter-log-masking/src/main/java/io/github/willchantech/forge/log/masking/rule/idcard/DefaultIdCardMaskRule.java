package io.github.willchantech.forge.log.masking.rule.idcard;

import io.github.willchantech.forge.log.masking.config.MaskFlags;
import io.github.willchantech.forge.log.masking.rule.MaskRule;

/**
 * @Desc :  身份证号码的日志脱敏规则（默认实现），可以自行覆盖这个规则实现。
 * @Author : Will Chan
 * @Date : 2026/4/27 16:04
 */
public class DefaultIdCardMaskRule extends AbstractIdCardMaskRule {

    @Override
    public String mask(String text) {
        /**
         * 前4位保留
         * 中间10位隐藏
         * 后4位保留
         */
        return text.replaceAll(
                "(?<!\\d)(\\d{4})\\d{10}(\\d{3}[0-9Xx])(?!\\d)",
                "$1**********$2"
        );
    }
}
