package net.aegistudio.resonance.io.midi;

import net.aegistudio.resonance.music.NoteOnEvent;

public class NoteOnProxy implements MidiEventProxy<NoteOnEvent>
{
	@Override
	public NoteOnEvent decapsulate(byte[] midiMessage) {
		byte pitch = midiMessage[1];
		byte velocity = midiMessage[2];
		return new NoteOnEvent(pitch, velocity, 0);
	}

	@Override
	public byte[] encapsulate(NoteOnEvent internalEvent) {
		byte[] noteOnEvent = new byte[3];
		noteOnEvent[0] = getEventCode();
		noteOnEvent[1] = internalEvent.note;
		noteOnEvent[2] = internalEvent.velocity;
		return noteOnEvent;
	}

	@Override
	public byte getEventCode() {
		return (byte) 0x90;
	}
}
