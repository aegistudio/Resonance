package net.aegistudio.resonance.dataflow;

import net.aegistudio.resonance.common.Frame;
import net.aegistudio.resonance.common.ResetEvent;
import net.aegistudio.resonance.plugin.Plugin;

public class StripNode implements DataflowNode
{
	protected DataflowNode inputNode;
	protected DataflowNode outputNode;
	
	@Override
	public void addInputNode(DataflowNode inputNode) {
		if(this.inputNode == null)
			this.inputNode = inputNode;
	}

	@Override
	public void removeInputNode(DataflowNode inputNode) {
		if(this.inputNode == inputNode)
			this.inputNode = null;
	}

	@Override
	public void addOutputNode(DataflowNode outputNode) {
		if(this.outputNode == null)
			this.outputNode = outputNode;
	}

	@Override
	public void removeOutputNode(DataflowNode outputNode) {
		if(this.outputNode == outputNode)
			this.outputNode = null;
	}

	@Override
	public void pass(Frame input) {
		if(containedPlugin != null)
		{
			this.containedPlugin.process(input, writeFrame);
			this.outputNode.pass(writeFrame);
		}
		else outputNode.pass(input);
	}

	protected Plugin containedPlugin;
	protected Frame writeFrame;
	
	@Override
	public void reset(ResetEvent event) {
		if(containedPlugin != null)
			containedPlugin.trigger(event);
		if(writeFrame == null 
			|| !event.environment.matchesBufferState(writeFrame))
				writeFrame = new Frame(event.environment.channels,
					event.environment.samplesPerFrame);
		if(outputNode != null) outputNode.reset(event);
	}

	@Override
	public void setPlugin(Plugin plugin) {
		this.containedPlugin = plugin;
	}
}
