package net.aegistudio.resonance.output;

import net.aegistudio.resonance.Frame;

public class WordFormat implements Format{

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
}
