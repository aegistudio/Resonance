package net.aegistudio.resonance.common;

public interface InputDevice
{
	public void create(Environment environment);
	
	public void read(Frame target);
}
