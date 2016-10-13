package net.aegistudio.resonance.dataflow;

import net.aegistudio.resonance.common.Frame;
import net.aegistudio.resonance.common.ResetEvent;
import net.aegistudio.resonance.plugin.Plugin;

public class RouterDrainNode implements DataflowNode
{
	DataflowNode directNode;
	RouterSourceNode mutedNode;
	
	DataflowNode outputNode;
	
	@Override
	public void addInputNode(DataflowNode inputNode) {
		if(inputNode instanceof RouterSourceNode)
		{
			if(mutedNode == null)
				mutedNode = (RouterSourceNode) outputNode;
		}
		else
		{
			if(directNode == null)
				directNode = inputNode;
		}
	}

	@Override
	public void removeInputNode(DataflowNode inputNode) {
		if(inputNode instanceof RouterSourceNode)
		{
			if(inputNode == mutedNode)
				mutedNode = null;
		}
		else
		{
			if(directNode == inputNode)
				directNode = null;
		}
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
		outputNode.pass(input);
	}

	@Override
	public void reset(ResetEvent event) {
		outputNode.reset(event);
	}

	@Override
	public void setPlugin(Plugin plugin) {
		throw new IllegalArgumentException("Plugin cannot be passed into a router node!");	
	}

}

