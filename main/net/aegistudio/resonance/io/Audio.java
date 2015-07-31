package net.aegistudio.resonance.io;

import net.aegistudio.resonance.Environment;
import net.aegistudio.resonance.Frame;

public interface Audio {
	public void reset(Environment environment);
	
	public void next(Frame frame);
}
