package net.aegistudio.resonance.device;

import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

import net.aegistudio.resonance.common.Environment;
import net.aegistudio.resonance.common.OutputDevice;

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
	
	public Mixer.Info getDevice()
	{
		return this.sic;
	}
	
	@Override
	public AudioFormat[] getAvailableFormats()
	{
		ArrayList<AudioFormat> availableEnvironments = new ArrayList<AudioFormat>();
		AudioFormat.Encoding[] encodings = new AudioFormat.Encoding[]{AudioFormat.Encoding.PCM_SIGNED,
				AudioFormat.Encoding.PCM_UNSIGNED, AudioFormat.Encoding.PCM_FLOAT, AudioFormat.Encoding.ALAW, AudioFormat.Encoding.ULAW};
		int[] bitdepths = new int[]{8, 16, 24, 32};
		int[] channels = new int[] {1, 2, 3, 4};
		float[] sampleRates = new float[] {22050.f, 44100.f, 88200.f};
		
		for(AudioFormat.Encoding encoding : encodings)
			for(int bitdepth : bitdepths) 
				for(int channel : channels) 
					for(float sampleRate : sampleRates) try {
						int frameSize = bitdepth / 8;
						AudioFormat format = new AudioFormat(encoding, sampleRate, bitdepth, channel,
								frameSize, sampleRate / frameSize, false);
						
						/** Exception Will Be Generated If Unable To Create Data Line. **/
						AudioSystem.getSourceDataLine(format, sic);
						availableEnvironments.add(format);
					}
					catch(Throwable th) {
					}
		
		return availableEnvironments.toArray(new AudioFormat[0]);
	}
	
	public String toString() {
		return sic.getName();
	}
	
	public boolean equals(Object another) {
		if(!(another instanceof MixerDevice))
			return false;
		return ((MixerDevice)another).sic.getName()
				.equals(this.sic.getName());
	}
	
	public static void main(String[] arguments)
	{
		for(Mixer.Info info : AudioSystem.getMixerInfo())
		{
			MixerDevice mx = new MixerDevice(info);
			for(AudioFormat audioFmt : mx.getAvailableFormats())
				System.out.println(info.getName() + " " + audioFmt);
		}
	}
}
