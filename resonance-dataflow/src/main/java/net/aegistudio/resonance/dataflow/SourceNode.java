package net.aegistudio.resonance.dataflow;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import net.aegistudio.resonance.common.Frame;
import net.aegistudio.resonance.common.ResetEvent;

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
		Frame passing = input;
		if(containedPlugin != null)	{
			this.containedPlugin.process(input, writeFrame);
			passing = writeFrame;
		}

		final Frame passed = passing;
		this.parallelDo(o -> o.pass(passed));
	}
	
	@Override
	public void reset(ResetEvent event) {
		super.reset(event);
		this.parallelDo(o -> o.reset(event));
	}	
	
	private synchronized void parallelDo(Consumer<DataflowNode> r) {
		synchronized(this.outputNodes) {
			ArrayList<Thread> joinPool = new ArrayList<Thread>();
			for(DataflowNode outputNode : outputNodes)
				joinPool.add(new Thread(() -> r.accept(outputNode)));
			
			joinPool.forEach(t -> t.start());
			joinPool.forEach(t -> {try { t.join(); } catch(Exception e) {}});
		}
	}
}
