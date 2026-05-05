package io.github.willchantech.forge.log.masking.design.framework.link.model3;

public class Launcher<P, C, R> {

	private HandlerInitializer<P, C, R> handlerInitializer;
	
	public void initializeHandlers(HandlerInitializer<P, C, R> handlerInitializer){
		this.handlerInitializer = handlerInitializer;
		handlerInitializer.initChannel(handlerInitializer);
	}
	
	public R execute(P params, C context, R response){
		// 执行责任链
		return execHandler(handlerInitializer.firstHandler, params, context, response);
	}
	
	private R execHandler(Handler<P, C, R> handler, P params, C context, R response){
		//执行业务方法
		handler.doHandler(params, context, response);
		//递归执行下一个节点
		if (handler.nextHandler != null) {
			execHandler(handler.nextHandler,params, context, response);
		}
		return response;
	}
}
