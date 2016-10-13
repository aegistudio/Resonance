package net.aegistudio.resonance.format;

import net.aegistudio.resonance.common.Frame;

public class ByteFormat extends Format implements OutputFormat, InputFormat
{
	public boolean signed;
	
	public ByteFormat(boolean signed)
	{
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
				byte value = (byte)(Byte.MAX_VALUE * frame.getSamples(ch)[i]);
				if(!signed) value = (byte) (Byte.MAX_VALUE + value);
				destination[pointer] = value;
				pointer ++;
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
				double value = 1.0 * source[pointer] / Byte.MAX_VALUE;
				if(!signed) value += 0.5;
				frame.getSamples(ch)[i] = value;
				pointer ++;
			}
	}
}
