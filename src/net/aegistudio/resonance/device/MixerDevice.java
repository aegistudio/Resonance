package net.aegistudio.resonance.device;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

import net.aegistudio.resonance.Environment;

public class MixerDevice implements OutputDevice
{
	Mixer.Info sic;
	public MixerDevice(Mixer.Info sic)
	{
		this.sic = sic;
	}
	
	SourceDataLine sourceDataLine;
	int bufferSize;
	boolean realTimed;
	
	@Override
	public synchronized void create(Environment environment)
	{
		if(sourceDataLine != null) return;
		try {
			AudioFormat format = environment.getAudioFormat();
			sourceDataLine = AudioSystem.getSourceDataLine(format, sic);
			sourceDataLine.open(format);
			sourceDataLine.start();
			bufferSize = environment.getByteBufferSize();
			realTimed = environment.prerenderFactor == 1;
		}
		catch (Throwable e) {
			throw new IllegalArgumentException("Could not create corresponding data line from the device.");
		}
	}
	
	@Override
	public synchronized void write(byte[] buffer)
	{
		this.sourceDataLine.write(buffer, 0, bufferSize);
		//if(!realTimed) this.sourceDataLine.drain();
	}
	
	@Override
	public synchronized void destroy()
	{
		this.sourceDataLine.stop();
		this.sourceDataLine.close();
		this.sourceDataLine = null;
	}
	
	@Override
	public void stop()
	{
		this.sourceDataLine.stop();
		this.sourceDataLine.flush();
		this.sourceDataLine.start();
	}
	
}
