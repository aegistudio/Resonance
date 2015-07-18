package net.aegistudio.resonance.dataflow;

import net.aegistudio.resonance.Environment;
import net.aegistudio.resonance.Frame;
import net.aegistudio.resonance.plugin.Plugin;
import net.aegistudio.resonance.serial.Structure;

public class DataflowController implements DataflowFacade, DataflowNode
{

	public final DataflowNode superSource;
	
	public DataflowController(DataflowNode superSource)
	{
		this.superSource = superSource;
	}
	
	@Override
	public void load(Structure input) {
		// DO NOTHING
	}

	@Override
	public void save(Structure output) {
		//DO NOTHING
	}
	
	@Override
	public void render(Frame frame)
	{
		this.superDrainFrame = frame;
		this.superSource.pass(frame);
	}

	@Override
	public void reset(Environment environment)
	{
		this.superSource.reset(new ResetEvent(environment));
	}

	@Override
	public void addInputNode(DataflowNode inputNode){	}

	@Override
	public void removeInputNode(DataflowNode inputNode) {	}

	@Override
	public void addOutputNode(DataflowNode outputNode) {	}

	@Override
	public void removeOutputNode(DataflowNode outputNode) {		}

	Frame superDrainFrame;
	
	@Override
	public void pass(Frame input) {
		if(superDrainFrame != null)
			superDrainFrame.copy(input);
	}

	@Override
	public void reset(ResetEvent event) {	}

	@Override
	public void setPlugin(Plugin plugin) {	}

	@Override
	public DataflowNode getSuperSource() {
		return this.superSource;
	}

	@Override
	public DataflowNode getSuperDrain() {
		return this;
	}
}

