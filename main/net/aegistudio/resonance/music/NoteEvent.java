package net.aegistudio.resonance.music;

import net.aegistudio.resonance.plugin.MidiEvent;

public abstract class NoteEvent implements MidiEvent{
	public final byte note;
	public final int frameOffset;
	
	protected NoteEvent(byte note, int frameOffset)
	{
		this.note = note;
		this.frameOffset = frameOffset;
	}
	
	public ScaleNote getScaleNote()
	{
		return ScaleNote.scaleNotes[this.note % 12];
	}
	
	public int getScale()
	{
		return note / 12;
	}
	
	public int getNote()
	{
		return this.note;
	}
	
	public int getFrameOffset()
	{
		return this.frameOffset;
	}
}
