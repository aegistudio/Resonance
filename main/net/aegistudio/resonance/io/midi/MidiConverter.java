package net.aegistudio.resonance.io.midi;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Track;

import net.aegistudio.resonance.KeywordArray;
import net.aegistudio.resonance.KeywordArray.KeywordEntry;
import net.aegistudio.resonance.channel.Note;
import net.aegistudio.resonance.channel.Score;
import net.aegistudio.resonance.music.NoteOffEvent;
import net.aegistudio.resonance.music.NoteOnEvent;
import net.aegistudio.resonance.plugin.Event;

/**
 * This class serve as a runtime converter that
 * converts a MIDI track into a score, or a score
 * into a midi track.
 * @author aegistudio
 */

public class MidiConverter {
	
	public final HashMap<Integer, MidiEventProxy<?, ?>> statusMap
		= new HashMap<Integer, MidiEventProxy<?, ?>>();	
	@SuppressWarnings("rawtypes")
	public final HashMap<Class<? extends Event>, MidiEventProxy> classMap
		= new HashMap<Class<? extends Event>, MidiEventProxy>();
	
	public MidiConverter()
	{
		NoteOnProxy noteOn = new NoteOnProxy();
		statusMap.put(noteOn.getEventCode(), noteOn);
		classMap.put(NoteOnEvent.class, noteOn);
		
		NoteOffProxy noteOff = new NoteOffProxy();
		statusMap.put(noteOff.getEventCode(), noteOff);
		classMap.put(NoteOffEvent.class, noteOff);
	}
	
	public static byte MIDI_EVENT_MASK = (byte) 0x0f0;
	public static byte MIDI_CHANNEL_MASK = (byte)0x00f;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void decapsulateTrack(Score targetScore, Track sourceTrack, int tickPerQuarterNote)
	{
		ArrayList<KeywordEntry<Double, Event>> events = new	ArrayList<KeywordEntry<Double, Event>>();
		for(int i = 0; i < sourceTrack.size(); i ++)
		{
			MidiEvent event = sourceTrack.get(i);
			double time = 1.0 * event.getTick() / tickPerQuarterNote;
			MidiEventProxy proxy = statusMap.get(event.getMessage().getStatus() & MIDI_EVENT_MASK);
			if(proxy == null) continue;
			events.add(new KeywordArray.DefaultKeywordEntry<Double, Event>(time, proxy.decapsulate(event.getMessage())));
		}
		
		TreeMap<Integer, ArrayDeque<KeywordEntry<Double, Event>>> notes
			= new TreeMap<Integer, ArrayDeque<KeywordEntry<Double, Event>>>();
		for(KeywordEntry<Double, Event> eventEntry : events)
		{
			Event event = eventEntry.getValue();
			if(event instanceof NoteOnEvent)
			{
				int note = ((NoteOnEvent) event).getNote();
				ArrayDeque<KeywordEntry<Double, Event>> target;
				if((target = notes.get(note)) == null)
					notes.put(note, target = new ArrayDeque<KeywordEntry<Double, Event>>());
				target.addLast(eventEntry);
			}
			else if(event instanceof NoteOffEvent)
			{
				int note = ((NoteOffEvent) event).getNote();
				ArrayDeque<KeywordEntry<Double, Event>> target = notes.get(note);
				if(target == null) continue;
				KeywordEntry<Double, Event> noteOnEntry = target.removeFirst();
				NoteOnEvent noteOnEvent = (NoteOnEvent)noteOnEntry.getValue();
				targetScore.addNote(noteOnEntry.getKeyword(), new Note(noteOnEvent.note,
						noteOnEvent.velocity, eventEntry.getKeyword() - noteOnEntry.getKeyword()));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void encapsulateTrack(Track targetTrack, Score sourceScore, int tickPerQuarterNote)
	{
		for(KeywordEntry<Double, Note> note : sourceScore.getAllNotes().all())
		{
			NoteOnEvent noteOnEvent = new NoteOnEvent(note.getValue().pitch, note.getValue().velocity, 0);
			NoteOffEvent noteOffEvent = new NoteOffEvent(note.getValue().pitch, 0);
			
			MidiMessage noteOnMessage = classMap.get(NoteOnEvent.class).encapsulate(noteOnEvent);
			MidiMessage noteOffMessage = classMap.get(NoteOffEvent.class).encapsulate(noteOffEvent);
			
			targetTrack.add(new MidiEvent(noteOnMessage, (int)(note.getKeyword() * tickPerQuarterNote)));
			targetTrack.add(new MidiEvent(noteOffMessage, (int)((note.getKeyword() + note.getValue().duration) * tickPerQuarterNote)));
		}
	}
}
