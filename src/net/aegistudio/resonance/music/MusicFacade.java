package net.aegistudio.resonance.music;

import net.aegistudio.resonance.serial.SerializedObject;

public interface MusicFacade extends SerializedObject
{
	public void tick(long tick);
	
	public void setBeatsPerMinute(float bpm);
	
	public void setBeatsPerMeasure(int beats);
	
	public float getBeatsPerMinute();
	
	public int getBeatsPerMeasure();
}
