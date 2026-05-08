package io.github.willchantech.forge.log.mask.rule;

import io.github.willchantech.forge.log.masking.rule.email.AbstractEmailMaskRule;
import io.github.willchantech.forge.log.masking.rule.phone.AbstractPhoneMaskRule;
import org.springframework.stereotype.Component;

/**
 * @Desc :
 * @Author : Will Chan
 * @Date : 2026/4/27 20:13
 */
//@Component
public class MyEmailMaskRule extends AbstractEmailMaskRule {
    @Override
    public String mask(String text) {
        return text.replaceAll(
                "([a-zA-Z0-9._%+-]{1})[a-zA-Z0-9._%+-]*(@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})",
                "$1呼呼呼$2"
        );
    }


//    @Override
//    public boolean supports(long flags) {
//        return PhoneMaskRule.super.supports(flags);
//    }
//
//    @Override
//    public String mask(String text) {
//        return "";
//    }
}
