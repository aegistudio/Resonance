package net.aegistudio.resonance.music;

import net.aegistudio.resonance.Environment;
import net.aegistudio.resonance.LengthObject;
import net.aegistudio.resonance.NamedHolder;
import net.aegistudio.resonance.channel.ChannelHolder;
import net.aegistudio.resonance.channel.Score;
import net.aegistudio.resonance.mixer.Mixer;
import net.aegistudio.resonance.serial.SerializedObject;

public interface MusicFacade extends SerializedObject, LengthObject
{
	public void reset(Environment environment);
	
	public void tick();
	
	public void setBeatsPerMinute(float bpm);
		
	public float getBeatsPerMinute();
	
	public void setBeatPosition(double position);
	
	public double getBeatPosition();
	
	public NamedHolder<Score> getScoreHolder();

	public ChannelHolder getChannelHolder();
	
	public Mixer getMixer();
	
	public double getLength();
}
