package net.aegistudio.resonance.channel;

import net.aegistudio.resonance.KeywordArray;
import net.aegistudio.resonance.serial.SerializedObject;

public interface Channel extends SerializedObject
{
	public <T extends Clip> KeywordArray<Double, T> getClips(Class<T> clazz);
	
	public void doTick(double begin, double end, int samplesPerFrame);
}
