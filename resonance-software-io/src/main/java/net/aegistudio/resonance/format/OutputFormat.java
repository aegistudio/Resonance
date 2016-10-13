package net.aegistudio.resonance.format;

import net.aegistudio.resonance.common.Frame;

public interface OutputFormat
{
	public void write(Frame frame, byte[] destination);
}
