package net.aegistudio.resonance.io;

import net.aegistudio.resonance.Frame;

public class DoubleWordFormat implements OutputFormat, InputFormat{
	public boolean bigEndian;
	public boolean signed;
	
	public DoubleWordFormat(boolean isBigEndian, boolean signed)
	{
		this.bigEndian = isBigEndian;
		this.signed = signed;
	}

	@Override
	public void write(Frame frame, byte[] destination) {
		int channels = frame.getChannels();
		int samplesPerFrame = frame.getSamplesPerFrame();
		int pointer = 0;
		for(int i = 0; i < samplesPerFrame; i ++)
			for(int ch = 0; ch < channels; ch ++)
			{
				int value = (int)(Integer.MAX_VALUE * frame.getSamples(ch)[i]);
				if(!this.signed) value += Integer.MAX_VALUE;
				
				if(bigEndian)
				{
					destination[pointer + 0] = (byte) ((value & 0x0ff000000) >> 24);
					destination[pointer + 1] = (byte) ((value & 0x00ff0000) >> 16);
					destination[pointer + 2] = (byte) ((value & 0x0000ff00) >> 8);
					destination[pointer + 3] = (byte) ((value & 0x000000ff) >> 0);
				}
				else
				{
					destination[pointer + 3] = (byte) ((value & 0x0ff000000) >> 24);
					destination[pointer + 2] = (byte) ((value & 0x00ff0000) >> 16);
					destination[pointer + 1] = (byte) ((value & 0x0000ff00) >> 8);
					destination[pointer + 0] = (byte) ((value & 0x000000ff) >> 0);	
				}
				pointer += 4;
			}
	}

	@Override
	public void read(Frame frame, byte[] source) {
		int channels = frame.getChannels();
		int samplesPerFrame = frame.getSamplesPerFrame();
		int pointer = 0;
		for(int i = 0; i < samplesPerFrame; i ++)
			for(int ch = 0; ch < channels; ch ++)
			{	
				int value = 0;
				if(bigEndian)
				{
					value |= (source[pointer + 0] << 24) & 0x0ff000000;
					value |= (source[pointer + 1] << 16) & 0x00ff0000;
					value |= (source[pointer + 2] << 8) & 0x0000ff00;
					value |= (source[pointer + 3] << 0) & 0x000000ff;
				}
				else
				{
					value |= (source[pointer + 3] << 24) & 0x0ff000000;
					value |= (source[pointer + 2] << 16) & 0x00ff0000;
					value |= (source[pointer + 1] << 8) & 0x0000ff00;
					value |= (source[pointer + 0] << 0) & 0x000000ff;	
				}
				double result = 1.0 * value / Integer.MAX_VALUE;
				if(!this.signed) result += 0.5;
				frame.getSamples(ch)[i] = result;
				pointer += 4;
			}
	}
}
