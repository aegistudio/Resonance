package net.aegistudio.resonance.channel;

import net.aegistudio.resonance.plugin.Event;
import net.aegistudio.resonance.serial.SerializedObject;

public interface Clip extends SerializedObject
{
	/**
	 * Returns the clip length.
	 * @return clip length in beats.
	 */
	public double getClipLength();
	
	/**
	 * Returns the events between ticks in beats.
	 * @param begin the begin tick.
	 * @param ends the end tick.
	 * @param samplesPerFrame how many frame does the event owns.
	 * @return the events happend between beats.
	 */
	public Event[] getEvents(double begin, double ends, int samplesPerSize);
}
