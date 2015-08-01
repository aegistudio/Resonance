package net.aegistudio.resonance.device;

import net.aegistudio.resonance.Environment;
import net.aegistudio.resonance.Frame;

public interface InputDevice
{
	public void create(Environment environment);
	
	public void read(Frame target);
}
