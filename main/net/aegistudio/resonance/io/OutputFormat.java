package net.aegistudio.resonance.io;

import net.aegistudio.resonance.Frame;

public interface OutputFormat
{
	public void write(Frame frame, byte[] destination);
}
