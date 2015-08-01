package net.aegistudio.resonance.io;

import net.aegistudio.resonance.Frame;

public class LinearInterpolator implements Interpolator
{
	@Override
	public void interpolate(Frame input, Frame output) {
		int inputSamples = input.getSamplesPerFrame();
		int outputSamples = output.getSamplesPerFrame();
		
		if(inputSamples == outputSamples)
			output.copy(input);
		else if(inputSamples > outputSamples)
		{
			
		}
	}

}
