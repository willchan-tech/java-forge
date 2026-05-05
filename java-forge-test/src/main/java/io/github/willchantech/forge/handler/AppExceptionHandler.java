package io.github.willchantech.forge.handler;

import io.github.willchantech.forge.rate.limiter.types.enums.RateLimiterRuleType;
import io.github.willchantech.forge.rate.limiter.types.enums.RateLimiterTreatmentType;
import io.github.willchantech.forge.rate.limiter.types.exception.RateLimiterException;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Desc : 统一异常处理
 * @Author : Will Chan
 * @Date : 2026/5/1 16:26
 */
@RestControllerAdvice
public class AppExceptionHandler {
    @ExceptionHandler(RateLimiterException.class)
    public ResponseEntity<?> handle(RateLimiterException e) {
        // ip 类型
        if (e.getType() == RateLimiterRuleType.IP) {
            String msg = "";
            if (StringUtils.equals(RateLimiterTreatmentType.BLACKLIST_LIMIT.getCode(), e.getCode())) {
                msg = "IP访问过于频繁，请稍后再试。";
            } else {
                msg = "IP访问过于频繁。";
            }
            return ResponseEntity.status(429)
                    .body(msg);
        }
        // key 类型
        if (e.getType() == RateLimiterRuleType.KEY) {
            String msg = "";
            if (StringUtils.equals(RateLimiterTreatmentType.BLACKLIST_LIMIT.getCode(), e.getCode())) {
                msg = "用户请求过快，请稍后再试。";
            } else {
                msg = "用户请求过快。";
            }
            return ResponseEntity.status(429)
                    .body(msg);
        }
        // global 类型
        return ResponseEntity.status(429)
                .body("系统繁忙，请稍后再试。");
    }
}
