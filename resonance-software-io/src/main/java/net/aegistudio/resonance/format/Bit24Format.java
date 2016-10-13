package net.aegistudio.resonance.format;

import net.aegistudio.resonance.common.Frame;

public class Bit24Format extends Format{
	public boolean bigEndian;
	public boolean signed;
	
	public static final int MAX_VALUE = (1 << 23) - 1;
	
	public Bit24Format(boolean isBigEndian, boolean signed)
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
				int value = (int)(MAX_VALUE * frame.getSamples(ch)[i]);
				if(!this.signed) value += MAX_VALUE;
				
				if(bigEndian)
				{
					destination[pointer + 0] = (byte) ((value & 0x00ff0000) >> 16);
					destination[pointer + 1] = (byte) ((value & 0x0000ff00) >> 8);
					destination[pointer + 2] = (byte) ((value & 0x000000ff) >> 0);
				}
				else
				{
					destination[pointer + 2] = (byte) ((value & 0x00ff0000) >> 16);
					destination[pointer + 1] = (byte) ((value & 0x0000ff00) >> 8);
					destination[pointer + 0] = (byte) ((value & 0x000000ff) >> 0);	
				}
				pointer += 3;
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
					value |= (source[pointer + 0] << 16) & 0x00ff0000;
					value |= (source[pointer + 1] << 8) & 0x0000ff00;
					value |= (source[pointer + 2] << 0) & 0x000000ff;
				}
				else
				{
					value |= (source[pointer + 2] << 16) & 0x00ff0000;
					value |= (source[pointer + 1] << 8) & 0x0000ff00;
					value |= (source[pointer + 0] << 0) & 0x000000ff;	
				}
				double result = 1.0 * value / MAX_VALUE;
				if(!this.signed) result += 0.5;
				frame.getSamples(ch)[i] = result;
				pointer += 3;
			}
	}
}
