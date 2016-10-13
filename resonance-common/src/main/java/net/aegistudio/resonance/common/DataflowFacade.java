package net.aegistudio.resonance.common;

import net.aegistudio.resonance.serial.SerializedObject;

public interface DataflowFacade extends SerializedObject {
	public void render(Frame frame);
	
	public void reset(Environment environment);
}
