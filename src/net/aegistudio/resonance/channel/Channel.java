package net.aegistudio.resonance.channel;

import net.aegistudio.resonance.serial.SerializedObject;

public interface Channel extends SerializedObject
{
	public void doTick(double begin, double end, int samplesPerFrame);
}
