package net.aegistudio.resonance.plugin;

import javax.sound.midi.MidiMessage;

public interface MidiEvent extends Event {
	public int getFrameOffset();
	
	public MidiMessage toMidiMessage();
}
