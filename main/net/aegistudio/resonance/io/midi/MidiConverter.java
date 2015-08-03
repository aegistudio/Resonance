package net.aegistudio.resonance.io.midi;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.sound.midi.MidiEvent;
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
	
	public final TreeMap<Byte, MidiEventProxy<?>> midiEventProxy
		= new TreeMap<Byte, MidiEventProxy<?>>();
	
	public MidiConverter()
	{
		NoteOnProxy noteOn = new NoteOnProxy();
		midiEventProxy.put(noteOn.getEventCode(), noteOn);
		NoteOffProxy noteOff = new NoteOffProxy();
		midiEventProxy.put(noteOff.getEventCode(), noteOff);
	}
	
	public static byte MIDI_EVENT_MASK = (byte) 0x0f0;
	public static byte MIDI_CHANNEL_MASK = (byte)0x00f;
	
	public void decapsulateTrack(Score targetScore, Track sourceTrack, int tickPerQuarterNote)
	{
		ArrayList<KeywordEntry<Double, Event>> events = new	ArrayList<KeywordEntry<Double, Event>>();
		for(int i = 0; i < sourceTrack.size(); i ++)
		{
			MidiEvent event = sourceTrack.get(i);
			byte[] midiMessage = event.getMessage().getMessage();
			double time = 1.0 * event.getTick() / tickPerQuarterNote;
			MidiEventProxy<?> proxy = midiEventProxy.get((byte)(midiMessage[0] & MIDI_EVENT_MASK));
			if(proxy == null) continue;
			events.add(new KeywordArray.DefaultKeywordEntry<Double, Event>(time, proxy.decapsulate(midiMessage)));
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
	
	public void encapsulateTrack(Track targetTrack, Score sourceScore, int tickPerQuarterNote)
	{
		
	}
}
