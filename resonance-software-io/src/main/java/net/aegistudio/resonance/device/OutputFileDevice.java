package net.aegistudio.resonance.device;

// Currently dodged compiler warnings.

/*
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;

import net.aegistudio.resonance.core.common.Environment;
import net.aegistudio.resonance.core.common.OutputDevice;
*/
public class OutputFileDevice /*implements OutputDevice*/ {
/*
	private final OutputStream output;
	private final AudioFileFormat.Type type;
	
	public OutputFileDevice(OutputStream file, AudioFileFormat.Type type) {
		this.output = file;
		this.type = type;
		if(!AudioSystem.isFileTypeSupported(type))
			throw new IllegalArgumentException("Unsupported file format: " + type);
	}
	
	private PipedOutputStream buffer;
	private PipedInputStream piped;
	private Environment environment;
	
	@Override
	public void create(Environment environment)	{
		try {
			this.environment = environment;
			buffer = new PipedOutputStream();
			piped = new PipedInputStream(buffer);
		}
		catch(Exception e) {
			throw new IllegalArgumentException("Error in pipe creation: " + e.getMessage());
		}
	}
	
	@Override
	public void write(byte[] buffer) {
	}

	@Override
	public void destroy() {
		
	}

	@Override
	public void stop() {
		throw new IllegalArgumentException("File output could not be stopped.");
	}

	@Override
	public AudioFormat[] getAvailableFormats() {
		
		return new AudioFormat[0];
	}
*/	
}
