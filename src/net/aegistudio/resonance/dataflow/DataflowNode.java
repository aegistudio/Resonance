package net.aegistudio.resonance.dataflow;

import net.aegistudio.resonance.Frame;
import net.aegistudio.resonance.plugin.Plugin;

public interface DataflowNode
{
	public void addInputNode(DataflowNode inputNode);
	
	public void removeInputNode(DataflowNode inputNode);
	
	public void addOutputNode(DataflowNode outputNode);
	
	public void removeOutputNode(DataflowNode outputNode);
	
	public void pass(Frame input);
	
	public void reset(ResetEvent event);
	
	public void setPlugin(Plugin plugin);
}
