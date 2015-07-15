package net.aegistudio.resonance.device;

import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;

import net.aegistudio.resonance.Environment;

public class OutputFileDevice implements OutputDevice
{
	private final File file;
	private final AudioFileFormat.Type type;
	
	public OutputFileDevice(File file, AudioFileFormat.Type type)
	{
		this.file = file;
		this.type = type;
	}
	
	@Override
	public void create(Environment environment)
	{
		try
		{
			if(!this.file.exists()) this.file.createNewFile();
//			AudioFileFormat format = new AudioFileFormat(type, environment.getAudioFormat(), AudioSystem.NOT_SPECIFIED);

		}
		catch(Exception e)
		{
			throw new IllegalArgumentException("Output file fails, caused by: " + e.getMessage());
		}
	}

	@Override
	public void write(byte[] buffer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		throw new IllegalArgumentException("File output could not be stopped.");
	}

}
