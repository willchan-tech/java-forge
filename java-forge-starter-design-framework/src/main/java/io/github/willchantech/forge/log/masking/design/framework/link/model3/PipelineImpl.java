package io.github.willchantech.forge.log.masking.design.framework.link.model3;

public class PipelineImpl<P, C, R> implements Pipeline<P, C, R> {

	public Handler<P, C, R> firstHandler = null;
	public Handler<P, C, R> currentHandler = null;
	
	@Override
	public void addLast(Handler<P, C, R> handler) {
		if(currentHandler == null) {
			currentHandler = handler;
			firstHandler = handler;
		} else {
			currentHandler.setNextHandler(handler);
			currentHandler = handler;
		}
	}
}
