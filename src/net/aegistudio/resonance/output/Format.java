package net.aegistudio.resonance.output;

import net.aegistudio.resonance.Frame;

public interface Format
{
	public void write(Frame frame, byte[] destination);
}
