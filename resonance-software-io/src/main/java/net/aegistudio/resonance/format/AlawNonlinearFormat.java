package net.aegistudio.resonance.format;

import net.aegistudio.resonance.common.Frame;

public class AlawNonlinearFormat extends Format
{
	private final Format linearFormat;
	private final double threshold;
	
	public AlawNonlinearFormat(Format linearFormat, double threshold) 
	{
		this.linearFormat = linearFormat;
		this.threshold = threshold;
	}
	
	public AlawNonlinearFormat(Format linearFormat)
	{
		this(linearFormat, 87.6);
	}
	
	@Override
	public void write(Frame frame, byte[] destination)
	{
		double thresholdInverse = 1.0 / threshold;
		double thresholdLog = Math.log(threshold);
		double thresholdLogPlusOne = thresholdLog + 1;
		
		for(int ch = 0; ch < frame.getChannels(); ch ++)
			for(int i = 0; i < frame.getSamplesPerFrame(); i ++)
			{
				double value = frame.getSamples(i)[ch];
				double amplitude = value >= 0? value : - value;
				if(amplitude == 0) value = 0;
				else if(amplitude <= thresholdInverse)
					value = threshold * value / thresholdLogPlusOne;
				else
					value = Math.signum(value) / thresholdLogPlusOne
						* (1 + Math.log(threshold * amplitude));
				frame.getSamples(i)[ch] = value;
			}
		this.linearFormat.write(frame, destination);
	}

	@Override
	public void read(Frame frame, byte[] input) 
	{
		this.linearFormat.read(frame, input);
		double thresholdLog = Math.log(threshold);
		double thresholdLogPlusOne = thresholdLog + 1;
		double thresholdLogInverse = 1.0 / thresholdLogPlusOne;
		
		for(int ch = 0; ch < frame.getChannels(); ch ++)
			for(int i = 0; i < frame.getSamplesPerFrame(); i ++)
			{
				double value = frame.getSamples(i)[ch];
				double amplitude = value >= 0? value : - value;
				if(amplitude == 0) value = 0;
				else if(amplitude <= thresholdLogInverse)
					value = thresholdLogPlusOne * value / threshold;
				else
					value = Math.signum(value) / threshold
						* Math.exp(thresholdLogPlusOne * amplitude - 1.0);
				frame.getSamples(i)[ch] = value;
			}
	}

}
