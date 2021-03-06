package net.aegistudio.resonance.music.channel;

import net.aegistudio.resonance.common.Event;
import net.aegistudio.resonance.music.LengthObject;
import net.aegistudio.resonance.serial.SerializedObject;

public interface Clip extends SerializedObject, LengthObject, Cloneable
{
	/**
	 * Returns the length of the clip in beats.
	 * For midi clip, the length will be static, but the length
	 * will be variant for the audio clips.
	 */
	public double getLength();
	
	/**
	 * Returns the inner offset of the clip in beats/
	 */
	public double getOffset();
	
	/**
	 * Returns the events between ticks in beats.
	 * @param begin the begin tick.
	 * @param ends the end tick.
	 * @param samplesPerFrame how many frame does the event owns.
	 * @return the events happened between beats.
	 */
	public Event[] getEvents(double begin, double ends, int samplesPerFrame);
	
	/**
	 * Tell the clip to off load and shut all running events.
	 * @param samplesPerFrame how many frame does the event owns.
	 * @return the events happened between beats.
	 */
	public Event[] offload(int samplesPerFrame);
	
	/**
	 * Call when the music layer reset itself.
	 */
	public void reset();
}
