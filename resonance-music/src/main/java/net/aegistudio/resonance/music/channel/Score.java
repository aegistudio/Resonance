package net.aegistudio.resonance.music.channel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

import net.aegistudio.resonance.music.KeywordArray;
import net.aegistudio.resonance.music.LengthKeywordArray;
import net.aegistudio.resonance.music.RoundedKeywordArray;
import net.aegistudio.resonance.music.KeywordArray.DefaultKeywordEntry;
import net.aegistudio.resonance.music.KeywordArray.KeywordEntry;
import net.aegistudio.resonance.serial.SerializedObject;
import net.aegistudio.resonance.serial.Structure;
import net.aegistudio.resonance.serial.Type;

public class Score implements SerializedObject
{
	private KeywordArray<Double, Note> notes;
	private double scoreLength = 0;
	
	public Score() {
		//XXX The instancializing method should be changed. 
		this.notes = new RoundedKeywordArray<Note>();
	}
	
	public double getScoreLength() {
		return this.scoreLength;
	}
	
	public KeywordEntry<Double, Note> addNote(double offset, Note note) {
		KeywordEntry<Double, Note> returnValue;
		this.notes.add(returnValue = new DefaultKeywordEntry<Double, Note>(offset, note));
		if(offset + note.duration > scoreLength) scoreLength = offset + note.duration;
		return returnValue;
	}
	
	public void removeNote(KeywordEntry<Double, Note> entry) {
		this.notes.remove(entry);
		scoreLength = 0;
		double newScoreLength;
		for(KeywordEntry<Double, Note> note : notes.all())
			if((newScoreLength = note.getKeyword() + note.getValue().getLength()) > scoreLength)
				scoreLength = newScoreLength;
	}
	
	public KeywordArray<Double, Note> getAllNotes() {
		return this.notes;
	}
	
	public LengthKeywordArray<Note> getLengthMonitor() {
		return new LengthKeywordArray<Note>(notes);
	}
	
	@Override
	public void load(Structure input) {
		Callable<Structure[]> creationOnNull = () -> new Structure[0];
		Structure[] noteList = input.get("score", Type.STRUCTURE_ARRAY, creationOnNull);
		for(Structure note : noteList) {
			double offset = note.get("offset", Type.DOUBLE, 0.0);
			Note value = new Note((byte)0, (byte)127, 0); value.load(note);
			this.addNote(offset, value);
		}
	}

	@Override
	public void save(Structure output) {
		ArrayList<Structure> noteList = new ArrayList<Structure>();
		Collection<KeywordEntry<Double, Note>> allNotes = notes.all();
		
		for(KeywordEntry<Double, Note> entry : allNotes) {
			Structure note = new Structure();
			note.set("offset", Type.DOUBLE, entry.getKeyword());
			entry.getValue().save(note);
			noteList.add(note);
		}
		output.set("score", Type.STRUCTURE_ARRAY, noteList.toArray(new Structure[0]));
	}
}
