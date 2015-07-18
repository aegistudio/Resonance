package net.aegistudio.resonance.channel;

import java.util.ArrayList;
import java.util.Collection;

import net.aegistudio.resonance.KeywordArray;
import net.aegistudio.resonance.KeywordArray.DefaultKeywordEntry;
import net.aegistudio.resonance.KeywordArray.KeywordEntry;
import net.aegistudio.resonance.LengthKeywordArray;
import net.aegistudio.resonance.serial.SerializedObject;
import net.aegistudio.resonance.serial.Structure;
import net.aegistudio.resonance.serial.Type;

public class Score implements SerializedObject
{
	private KeywordArray<Double, Note> notes;
	private double scoreLength = 0;
	
	public Score()
	{
		// Initialzing The Keyword Array Notes.
	}
	
	public double getScoreLength()
	{
		return this.scoreLength;
	}
	
	public void addNote(double offset, Note note)
	{
		this.notes.add(new DefaultKeywordEntry<Double, Note>(offset, note));
		if(offset + note.duration > scoreLength) scoreLength = offset + note.duration; 
	}
	
	public void removeNote(double offset, Note note)
	{
		this.notes.remove(new DefaultKeywordEntry<Double, Note>(offset, note));
	}
	
	public LengthKeywordArray<Note> getLengthMonitor()
	{
		return new LengthKeywordArray<Note>(notes);
	}
	
	@Override
	public void load(Structure input) {
		Structure[] noteList = input.get("score", Type.STRUCTURE_ARRAY, new Structure[0]);
		for(Structure note : noteList)
		{
			double offset = note.get("offset", Type.DOUBLE, 0.0);
			Note value = new Note((byte)0, (byte)127, 0); value.load(note);
			this.addNote(offset, value);
		}
	}

	@Override
	public void save(Structure output) {
		ArrayList<Structure> noteList = new ArrayList<Structure>();
		Collection<KeywordEntry<Double, Note>> allNotes = notes.all();
		
		for(KeywordEntry<Double, Note> entry : allNotes)
		{
			Structure note = new Structure();
			note.set("offset", Type.DOUBLE, entry.getKeyword());
			entry.getValue().save(note);
			noteList.add(note);
		}
		output.set("score", Type.STRUCTURE_ARRAY, noteList.toArray(new Structure[0]));
	}
}
