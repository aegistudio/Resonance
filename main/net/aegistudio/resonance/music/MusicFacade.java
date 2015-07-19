package net.aegistudio.resonance.music;

import net.aegistudio.resonance.Environment;
import net.aegistudio.resonance.serial.SerializedObject;

public interface MusicFacade extends SerializedObject
{
	public void reset(Environment environment);
	
	public void tick();
	
	public void setBeatsPerMinute(float bpm);
		
	public float getBeatsPerMinute();
	
	public void setBeatPosition(double position);
	
	public double getBeatPosition();
}
