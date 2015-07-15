package net.aegistudio.resonance.plugin;

import net.aegistudio.resonance.Frame;
import net.aegistudio.resonance.serial.Structure;

public interface Plugin
{
	public void create(Structure parameter);
	
	public void trigger(Event event);
	
	public void process(Frame input, Frame output);
	
	public void destroy();
}
