package net.aegistudio.resonance.dataflow;

import net.aegistudio.resonance.Environment;
import net.aegistudio.resonance.Frame;
import net.aegistudio.resonance.serial.SerializedObject;

public interface DataflowFacade extends SerializedObject
{
	public void render(Frame frame);
	
	public void reset(Environment environment);
}
