package net.aegistudio.resonance.io.midi;

import javax.sound.midi.MidiMessage;

import net.aegistudio.resonance.plugin.Event;

public interface MidiEventProxy<E extends Event, M extends MidiMessage> {
	public E decapsulate(M midiMessage);
	
	public M encapsulate(E internalEvent);
	
	public int getEventCode();
}
