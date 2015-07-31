package net.aegistudio.resonance.io;

import net.aegistudio.resonance.Frame;

public class WordFormat implements OutputFormat, InputFormat
{

	public boolean bigEndian;
	public boolean signed;
	
	public WordFormat(boolean isBigEndian, boolean signed)
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
				short value = (short)(Short.MAX_VALUE * frame.getSamples(ch)[i]);
				if(!this.signed) value += Short.MAX_VALUE;
				
				if(bigEndian)
				{
					destination[pointer + 0] = (byte) ((value & 0x0ff00) >> 8);
					destination[pointer + 1] = (byte) ((value & 0x000ff) >> 0);
				}
				else
				{
					destination[pointer + 0] = (byte) ((value & 0x000ff) >> 0);
					destination[pointer + 1] = (byte) ((value & 0x0ff00) >> 8);
				}
				pointer += 2;
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
				short value = 0;
				
				if(bigEndian)
				{
					value |= (source[pointer + 0] << 8) & 0x0ff00;
					value |= (source[pointer + 1] << 0) & 0x000ff;
				}
				else
				{
					value |= (source[pointer + 1] << 8) & 0x0ff00;
					value |= (source[pointer + 0] << 0) & 0x000ff;
				}
				double result = 1.0 * value / Short.MAX_VALUE;
				if(!this.signed) result += 0.5;
				frame.getSamples(ch)[i] = result;
				pointer += 2;
			}
	}
}
