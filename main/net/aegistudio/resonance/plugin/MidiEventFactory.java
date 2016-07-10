package net.aegistudio.resonance.plugin;

import java.util.HashMap;
import java.util.function.Function;

import javax.sound.midi.MidiMessage;

import net.aegistudio.resonance.music.NoteOffEvent;
import net.aegistudio.resonance.music.NoteOnEvent;

public class MidiEventFactory {
	public static final HashMap<Integer, MidiEventFactory> EVENT_MAP = new HashMap<>();
	
	public final int code;
	public final Function<MidiMessage, MidiEvent> converter;
	public MidiEventFactory(int code, Function<MidiMessage, MidiEvent> converter) {
		this.code = code;
		this.converter = converter;
		EVENT_MAP.put(code, this);
	}
	
	public int getEventCode() {
		return this.code;
	}
	
	public MidiEvent convert(MidiMessage event) {
		return converter.apply(event);
	}
	
	static {
		NoteOnEvent.loadFactory();
		NoteOffEvent.loadFactory();
	}
}
