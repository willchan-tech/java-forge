package io.github.willchantech.forge.log.masking.design.framework.tree;

/**
 * @author willchan-tech
 * @description 受理策略处理
 * T 入参类型
 * D 上下文参数
 * R 返参类型
 * @create 2024-12-14 12:06
 */
public interface StrategyHandler<T, D extends DynamicContext, R> {
    /**
     * StrategyHandler 是一个函数式接口（只有一个抽象方法：apply），因此可以使用 lambda 表达式：把一个方法，当作对象传递。
     * DEFAULT 是一个公共，静态，不可变的常量字段，声明了一个对象，类型为 StrategyHandler，对象内部同时实现了一个匿名方法（隐式对应唯一的抽象方法 apply）。
     * 作用：
     *      DEFAULT 字段是一个“默认策略实现”
     *      —— 当你“没有合适策略可用”时，用它来兜底，什么都不做，直接返回 null
     */
    StrategyHandler DEFAULT = (T, D) -> null;

    default R proceed(T requestParameter, D dynamicContext) {
        return null;
    }

    R apply(T requestParameter, D dynamicContext) throws Exception;

}