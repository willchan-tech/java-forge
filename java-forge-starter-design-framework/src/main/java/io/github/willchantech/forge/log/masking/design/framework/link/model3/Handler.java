package io.github.willchantech.forge.log.masking.design.framework.link.model3;

public abstract class Handler<P, C, R> {

	public Handler<P, C, R> nextHandler;

	public void setNextHandler(Handler<P, C, R> nextHandler) {
		this.nextHandler = nextHandler;
	}

	public abstract void doHandler(P params, C context, R response);
}
