package net.aegistudio.resonance.io;

import net.aegistudio.resonance.Frame;

/**
 * The work of interpolator, is to minimize of magnify a
 * one sample rate to another. However the sample rate is unknown
 * and the interpolation can only be judged on the input/output
 * frames.
 * @author aegistudio
 */

public interface Interpolator {
	public void interpolate(Frame input, Frame output);
}
