package net.aegistudio.resonance;

import javax.sound.sampled.AudioFormat;

/**
 * This class controls the acoustic environment of the software.
 * Could be configured through device setting.
 * @author aegistudio
 */

public class Environment
{
	public final float sampleRate;
	public final int channels;
	public final Encoding wordEncoding;
	public final int samplesPerFrame;
	public final int prerenderFactor;
	
	public Environment(float sampleRate, int channels,
			Encoding wordEncoding, int samplesPerFrame, int prerenderFactor)
	{
		this.sampleRate = sampleRate;
		
		this.channels = channels;
		if(this.channels <= 0) throw new IllegalArgumentException("The channels should be greater than zero!");
		
		this.wordEncoding = wordEncoding;
		this.samplesPerFrame = samplesPerFrame;
		this.prerenderFactor = prerenderFactor;
	}
	
	public int getByteBufferSize()
	{
		return this.wordEncoding.getWordLength() / 8 * channels * this.samplesPerFrame;
	}
	
	public String toString()
	{
		return String.format("%.1f Hz, %s, %s, %s",
				sampleRate,
				(this.channels == 1)? "mono" :
					((this.channels == 2)? "stereo"
							: this.channels + " channels"),
				this.wordEncoding,
				this.prerenderFactor == 1? "realtime" : 
					String.format("%d x %d buffered", this.prerenderFactor, this.samplesPerFrame));
	}
	
	public AudioFormat getAudioFormat()
	{
		return new AudioFormat(this.wordEncoding.getWordTypeInstance(), sampleRate,
				this.wordEncoding.getWordLength(),
				channels, this.wordEncoding.getWordLength() * channels / 8, sampleRate / this.wordEncoding.getWordLength() * 8,
				this.wordEncoding.isBigEndian());
	}
	
	public boolean sameBufferState(Environment environment)
	{
		return (environment.prerenderFactor == this.prerenderFactor)
				&& (environment.samplesPerFrame == this.samplesPerFrame)
				&& (environment.channels == this.channels);
	}
	
	public boolean matchesBufferState(Frame frame)
	{
		return frame.getSamplesPerFrame() == this.samplesPerFrame
				&& frame.getChannels() == this.channels;
	}
}
