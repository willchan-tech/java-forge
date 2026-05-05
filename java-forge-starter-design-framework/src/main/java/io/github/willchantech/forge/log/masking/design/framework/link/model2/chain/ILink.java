package io.github.willchantech.forge.log.masking.design.framework.link.model2.chain;

/**
 * @author willchan-tech
 * @description 链接口
 * @create 2025-01-18 09:27
 */
public interface ILink<E> {

    boolean add(E e);

    boolean addFirst(E e);

    boolean addLast(E e);

    boolean remove(Object o);

    E get(int index);

    void printLinkList();

}
