package net.aegistudio.resonance.format;

import net.aegistudio.resonance.common.Frame;

public interface InputFormat {
	public void read(Frame target, byte[] input);
}
