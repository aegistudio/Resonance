package net.aegistudio.resonance.io.midi;

import net.aegistudio.resonance.plugin.Event;

public interface MidiEventProxy<E extends Event> {
	public E decapsulate(byte[] midiMessage);
	
	public byte[] encapsulate(E internalEvent);
	
	public byte getEventCode();
}
