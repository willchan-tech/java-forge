package io.github.willchantech.forge.trigger;

import io.github.willchantech.forge.rate.limiter.types.annotations.IPLimiterRule;
import io.github.willchantech.forge.rate.limiter.types.annotations.KeyLimiterRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author willchan-tech
 * 2025-05-07 14:41
 */
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/index/")
public class IndexController {

    /**
     * curl --request GET \
     *   --url 'http://127.0.0.1:9191/api/v1/index/draw?userId=willchan'
     */
//    @GlobalLimiterRule
    @IPLimiterRule(permitsPerSecond = 2, blacklistCount = 2)
    @KeyLimiterRule(key = "userId", permitsPerSecond = 2, blacklistCount = 2)
    @RequestMapping(value = "draw", method = RequestMethod.GET)
    public String draw(String userId) {
        return "test";
    }

    public String drawErrorRateLimiter(String userId) {
        return "rateLimiter";
    }

}
