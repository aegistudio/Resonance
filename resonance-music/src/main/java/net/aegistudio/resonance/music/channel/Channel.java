package net.aegistudio.resonance.music.channel;

import net.aegistudio.resonance.music.KeywordArray;
import net.aegistudio.resonance.music.LengthObject;
import net.aegistudio.resonance.serial.SerializedObject;

public interface Channel extends SerializedObject, LengthObject
{
	public <T extends Clip> KeywordArray<Double, T> getClips(Class<T> clazz);
	
	public void doTick(double begin, double end, int samplesPerFrame);
	
	public void reset();
}
