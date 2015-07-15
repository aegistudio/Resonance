package net.aegistudio.resonance.music;

import net.aegistudio.resonance.plugin.Event;

public class NoteOnEvent extends NoteEvent implements Event{

	public final byte velocity;
	
	public NoteOnEvent(byte note, byte velocity, int frameOffset)
	{
		super(note, frameOffset);
		this.velocity = velocity;
	}
	
	public int getVelocity()
	{
		return this.velocity;
	}
}
