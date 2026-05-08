package io.github.willchantech.forge.log.masking.adapter.logback;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.willchantech.forge.log.masking.design.framework.link.model2.chain.BusinessLinkedList;
import io.github.willchantech.forge.log.masking.factory.LogMaskingChainFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Desc :  logback 日志脱敏适配器:
 *      使用者注册到 logback.xml 中：
 *              <conversionRule conversionWord="mask" converterClass="io.github.willchantech.forge.log.masking.adapter.logback.LogbackMaskingConverter"/>
 *              然后在 pattern 中将日志变量替换成 %mask
 * @Author : Will Chan
 * @Date : 2026/4/27 17:03
 */
public class LogbackMaskingConverter extends ClassicConverter {

    /**
     * spring 静态注入：
     *      ClassicConverter 对象由第三方框架 logback 创建，但又想在里面引用 spring 的 bean。
     */
    private static BusinessLinkedList<String, LogMaskingChainFactory.DynamicContext, String> chain;
    public static void setChain(BusinessLinkedList<String, LogMaskingChainFactory.DynamicContext, String> logMaskingChain) {
        chain = logMaskingChain;
    }

    @Override
    public String convert(ILoggingEvent event) {
        String msg = event.getFormattedMessage();
        // 防止 chain 还没初始化就开始打印日志，判空 chain
        if (StringUtils.isEmpty(msg) || chain == null) {
            return msg;
        }
        // 日志脱敏
        try {
            msg = chain.apply(msg, new LogMaskingChainFactory.DynamicContext());
        } catch (Exception e) {
            //不做任何处理，直接返回即可。为了防止递归风险，这里也不打印任何日志。
        }
        return msg;
    }
}
