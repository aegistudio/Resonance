package net.aegistudio.resonance.device;

import java.io.InputStream;

import net.aegistudio.resonance.Environment;

public interface InputDevice
{
	public InputStream getInputStream(Environment environment);
}
