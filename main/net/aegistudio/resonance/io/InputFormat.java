package net.aegistudio.resonance.io;

import net.aegistudio.resonance.Frame;

public interface InputFormat {
	public void read(Frame target, byte[] input);
}
