package net.aegistudio.resonance;

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
	
	public Frame clone()
	{
		Frame returnValue = new Frame(this.channels, this.samplesPerFrame);
		for(int i = 0; i < this.channels; i ++)
			System.arraycopy(this.frameData[i], 0, returnValue.frameData[i], 0, this.samplesPerFrame);
		return returnValue;
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
