package net.aegistudio.resonance.output;

import net.aegistudio.resonance.Frame;

public class DoubleWordFormat implements Format{
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
}
