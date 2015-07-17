package net.aegistudio.resonance;

import java.util.Arrays;

public class Frame
{
	private final double[][] frameData;
	
	private final int channels;
	private final int samplesPerFrame;
	
	public Frame(int channels, int samplesPerFrame)
	{
		this.channels = channels;
		this.samplesPerFrame = samplesPerFrame;
		this.frameData = new double[channels][samplesPerFrame];
	}
	
	public double[] getSamples(int channel)
	{
		if(channel >= this.channels || channel < 0) return null;
		return frameData[channel];
	}
	
	public int getSamplesPerFrame()
	{
		return this.samplesPerFrame;
	}
	
	public int getChannels()
	{
		return this.channels;
	}
	
	public void zero()
	{
		for(int i = 0; i < this.channels; i ++)
			Arrays.fill(this.frameData[i], 0);
	}
	
	public Frame clone()
	{
		Frame returnValue = new Frame(this.channels, this.samplesPerFrame);
		returnValue.copy(this);
		return returnValue;
	}
	
	public void copy(Frame frame)
	{
		for(int i = 0; i < this.channels; i ++)
			System.arraycopy(frame.frameData[i], 0, this.frameData[i], 0, this.samplesPerFrame);
	}
	
	public void mix(Frame anotherFrame, double dry, double wet)
	{
		if(anotherFrame.samplesPerFrame != this.samplesPerFrame
				|| anotherFrame.channels != this.channels)
			throw new IllegalArgumentException("Can't blend two frames that are different in size.");
		for(int i = 0; i < this.channels; i ++)
			for(int j = 0; j < this.samplesPerFrame; j ++) 
				this.frameData[i][j] = dry * this.frameData[i][j]
						+ wet * anotherFrame.frameData[i][j];
	}	
}
