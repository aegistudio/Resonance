package net.aegistudio.resonance.common;

import javax.sound.sampled.AudioFormat;

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
	
	/**
	 * Get possible/safe format that could be created by this device. <br>
	 * <b>Warning:</b> By convenience, the sample rate is restricted to 44100 hertz, mono,
	 * little endian and should be manually set when other formats are needed in software.
	 */
	public AudioFormat[] getAvailableFormats();
}
