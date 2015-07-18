package net.aegistudio.resonance.device;

import net.aegistudio.resonance.Environment;

public interface OutputDevice
{
	/**
	 * Create output stream by the given environment.
	 * @param environment
	 */
	public void create(Environment environment);
	
	/**
	 * Write specific data to the output device.
	 * @param buffer
	 */
	public void write(byte[] buffer);
	
	/**
	 * Shuts the sound output of the stream instantly.
	 */
	public void stop();
	
	/**
	 * Release the sound output resource.
	 */
	public void destroy();
}
