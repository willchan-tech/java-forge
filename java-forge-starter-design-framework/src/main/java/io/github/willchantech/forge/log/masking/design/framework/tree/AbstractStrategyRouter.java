package io.github.willchantech.forge.log.masking.design.framework.tree;

import lombok.Getter;
import lombok.Setter;

/**
 * @author willchan-tech
 * @description 策略路由抽象类
 * @create 2024-12-14 13:25
 */
public abstract class AbstractStrategyRouter<T, D extends DynamicContext, R> implements StrategyMapper<T, D, R>, StrategyHandler<T, D, R> {

    @Getter
    @Setter
    protected StrategyHandler<T, D, R> defaultStrategyHandler = StrategyHandler.DEFAULT;

    public R router(T requestParameter, D dynamicContext) throws Exception {
        StrategyHandler<T, D, R> strategyHandler = get(requestParameter, dynamicContext);
        if(null != strategyHandler) return strategyHandler.apply(requestParameter, dynamicContext);
        return defaultStrategyHandler.apply(requestParameter, dynamicContext);
    }

}
