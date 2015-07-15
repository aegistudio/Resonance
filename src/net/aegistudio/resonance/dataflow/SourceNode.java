package net.aegistudio.resonance.dataflow;

import java.util.HashSet;
import java.util.Set;

import net.aegistudio.resonance.Frame;

public class SourceNode extends StripNode
{
	Set<DataflowNode> outputNodes = new HashSet<DataflowNode>();

	@Override
	public void addOutputNode(DataflowNode outputNode) {
		this.outputNodes.add(outputNode);
	}
	
	@Override
	public void removeOutputNode(DataflowNode outputNode) {
		this.outputNodes.remove(outputNode);
	}
	
	@Override
	public void pass(Frame input) {
		if(containedPlugin != null)
		{
			this.containedPlugin.process(input, writeFrame);
			for(DataflowNode outputNode : outputNodes)
				outputNode.pass(writeFrame);
		}
		else for(DataflowNode outputNode : outputNodes)
			outputNode.pass(input);
	}
	
	@Override
	public void reset(ResetEvent event) {
		super.reset(event);
		for(DataflowNode outputNode : outputNodes)
			outputNode.reset(event);
	}	
}
