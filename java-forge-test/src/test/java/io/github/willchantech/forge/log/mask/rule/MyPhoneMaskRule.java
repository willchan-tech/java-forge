package io.github.willchantech.forge.log.mask.rule;

import io.github.willchantech.forge.log.masking.rule.phone.AbstractPhoneMaskRule;
import org.springframework.stereotype.Component;

/**
 * @Desc :
 * @Author : Will Chan
 * @Date : 2026/4/27 20:13
 */
//@Component
public class MyPhoneMaskRule extends AbstractPhoneMaskRule {
    @Override
    public String mask(String text) {
        return text.replaceAll(
                "(?<!\\d)(1[3-9]\\d)\\d{4}(\\d{4})(?!\\d)",
                "$1哈哈哈哈$2"
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
