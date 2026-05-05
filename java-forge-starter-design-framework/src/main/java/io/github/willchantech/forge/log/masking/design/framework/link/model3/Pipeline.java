package io.github.willchantech.forge.log.masking.design.framework.link.model3;


public interface Pipeline<P, C, R> {

	public void addLast(Handler<P, C, R> handler);

}
