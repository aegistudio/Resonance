package net.aegistudio.resonance.midi;

import javax.sound.midi.MidiMessage;

import net.aegistudio.resonance.common.Event;

public interface MidiEvent extends Event {
	public int getFrameOffset();
	
	public MidiMessage toMidiMessage();
}
