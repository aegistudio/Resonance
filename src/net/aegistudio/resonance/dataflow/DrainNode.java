package net.aegistudio.resonance.dataflow;

import java.util.HashSet;
import java.util.Set;

import net.aegistudio.resonance.Frame;

public class DrainNode extends StripNode
{
	Set<DataflowNode> inputNodes = new HashSet<DataflowNode>();
	int factor;
	
	@Override
	public synchronized void addInputNode(DataflowNode outputNode) {
		this.inputNodes.add(outputNode);
	}
	
	@Override
	public synchronized void removeInputNode(DataflowNode outputNode) {
		this.inputNodes.remove(outputNode);
	}
	
	@Override
	public synchronized void pass(Frame input)
	{
		writeFrame.mix(input, 1.0, 1.0);
		this.factor = (this.factor + 1) % inputNodes.size();
		if(this.factor == 0)
		{
			this.containedPlugin.process(input, writeFrame);
			if(this.outputNode != null) this.outputNode.pass(writeFrame);
		}
	}
	
	@Override
	public void reset(ResetEvent event) {
		super.reset(event);
		this.factor = 0;
	}
}
