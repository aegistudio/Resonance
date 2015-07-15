package net.aegistudio.resonance.dataflow;

import net.aegistudio.resonance.Frame;
import net.aegistudio.resonance.plugin.Plugin;

public class RouterSourceNode implements DataflowNode
{
	DataflowNode inputNode;
	DataflowNode directNode;
	RouterDrainNode mutedNode;
	
	@Override
	public void addInputNode(DataflowNode inputNode) {
		if(inputNode == null)
			this.inputNode = inputNode;
	}

	@Override
	public void removeInputNode(DataflowNode inputNode) {
		if(this.inputNode == inputNode)
			this.inputNode = null;
	}

	@Override
	public void addOutputNode(DataflowNode outputNode) {
		if(outputNode instanceof RouterDrainNode)
		{
			if(mutedNode == null)
				mutedNode = (RouterDrainNode) outputNode;
		}
		else
		{
			if(directNode == null)
				directNode = outputNode;
		}
	}

	@Override
	public void removeOutputNode(DataflowNode outputNode) {
		if(outputNode instanceof RouterDrainNode)
		{
			if(outputNode == mutedNode)
				mutedNode = null;
		}
		else
		{
			if(directNode == outputNode)
				directNode = null;
		}
	}

	private boolean isMuted = false;
	public void setMuted(boolean isMuted)
	{
		this.isMuted = isMuted;
	}
	
	public boolean isMuted()
	{
		return this.isMuted;
	}
	
	@Override
	public void pass(Frame input) {
		if(this.isMuted)
		{
			if(mutedNode != null)
				mutedNode.pass(input);
		}
		else if(directNode != null)
			directNode.pass(input);
	}

	@Override
	public void reset(ResetEvent event) {
		mutedNode.reset(event);
		directNode.reset(event);
	}

	@Override
	public void setPlugin(Plugin plugin) {
		throw new IllegalArgumentException("Plugin cannot be passed into a router node!");	
	}

}
