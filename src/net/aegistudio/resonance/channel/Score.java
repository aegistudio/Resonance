package net.aegistudio.resonance.channel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import net.aegistudio.resonance.KeywordArray;
import net.aegistudio.resonance.KeywordArray.DefaultKeywordEntry;
import net.aegistudio.resonance.KeywordArray.KeywordEntry;
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
	
	public Collection<KeywordEntry<Double, Note>> getBeginNotes(double begin, double end)
	{
		return this.notes.between(begin, end);
	}
	
	public Collection<KeywordEntry<Double, Note>> getEndNotes(double begin, double end, 
			Collection<KeywordEntry<Double, Note>> beginNotes)
	{
		Collection<KeywordEntry<Double, Note>> endNotes = new HashSet<KeywordEntry<Double, Note>>();
		Iterator<KeywordEntry<Double, Note>> iterator = beginNotes.iterator();
		while(iterator.hasNext())
		{
			KeywordEntry<Double, Note> entry = iterator.next();
			if(end > entry.getKeyword() + entry.getValue().duration || begin < entry.getKeyword())
			{
				endNotes.add(new DefaultKeywordEntry<Double, Note>(entry.getKeyword() + entry.getValue().duration,
						entry.getValue()));
				beginNotes.remove(entry);
			}
		}
		return endNotes;
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
		for(KeywordEntry<Double, Note> entry : notes.all())
		{
			Structure note = new Structure();
			note.set("offset", Type.DOUBLE, entry.getKeyword());
			entry.getValue().save(note);
			noteList.add(note);
		}
		output.set("score", Type.STRUCTURE_ARRAY, noteList.toArray(new Structure[0]));
	}
}
