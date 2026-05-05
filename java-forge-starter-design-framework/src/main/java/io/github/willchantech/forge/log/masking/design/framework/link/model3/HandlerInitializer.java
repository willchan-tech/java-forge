package io.github.willchantech.forge.log.masking.design.framework.link.model3;

public abstract class HandlerInitializer<P, C, R> extends PipelineImpl<P, C, R> {

	protected abstract void initChannel(Pipeline<P, C, R> pipeline);
}
