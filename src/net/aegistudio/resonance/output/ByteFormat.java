package net.aegistudio.resonance.output;

import net.aegistudio.resonance.Frame;

public class ByteFormat implements Format
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
}
