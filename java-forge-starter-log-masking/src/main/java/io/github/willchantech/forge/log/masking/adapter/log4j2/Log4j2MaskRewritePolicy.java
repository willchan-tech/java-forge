package io.github.willchantech.forge.log.masking.adapter.log4j2;

import io.github.willchantech.forge.log.masking.design.framework.link.model2.chain.BusinessLinkedList;
import io.github.willchantech.forge.log.masking.factory.LogMaskingChainFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rewrite.RewritePolicy;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.SimpleMessage;

/**
 * @Desc :  对于 log4j2 日志脱敏适配器：
 *      使用者注册到 log4j2.xml 中：
 *
 *      <?xml version="1.0" encoding="UTF-8"?>
 *      <Configuration packages="io.github.willchantech.javaforge.logmask.adapter.log4j2">
 *
 *       <Appenders>
 *
 *         <Console name="Console" target="SYSTEM_OUT">
 *             <PatternLayout pattern="%d %p %c - %msg%n"/>
 *         </Console>
 *
 *         <Rewrite name="MaskRewrite">
 *             <LogMaskRewritePolicy/>
 *             <AppenderRef ref="Console"/>
 *         </Rewrite>
 *
 *       </Appenders>
 *
 *       <Loggers>
 *         <Root level="info">
 *             <AppenderRef ref="MaskRewrite"/>
 *         </Root>
 *       </Loggers>
 *
 *      </Configuration>
 * @Author : Will Chan
 * @Date : 2026/4/27 18:28
 */
public class Log4j2MaskRewritePolicy implements RewritePolicy {
    /**
     * spring 静态注入：
     *      RewritePolicy 对象由第三方框架 log4j2 创建，但又想在里面引用 spring 的 bean。
     */
    private static volatile BusinessLinkedList<String, LogMaskingChainFactory.DynamicContext, String> chain;
    public static void setChain(BusinessLinkedList<String, LogMaskingChainFactory.DynamicContext, String> c) {
        chain = c;
    }

    @Override
    public LogEvent rewrite(LogEvent source) {
        //防止 chain 还没初始化就开始打印日志，判空 chain
        if (chain == null) {
            return source;
        }
        String msg = source.getMessage().getFormattedMessage();
        if (StringUtils.isEmpty(msg)) {
            return source;
        }
        //日志脱敏
        try {
            String masked = chain.apply(msg, new LogMaskingChainFactory.DynamicContext());

            return new Log4jLogEvent.Builder(source)
                    .setMessage(new SimpleMessage(masked))
                    .build();

        } catch (Exception e) {
            //不做任何处理，直接返回日志。
            return source;
        }
    }

}
