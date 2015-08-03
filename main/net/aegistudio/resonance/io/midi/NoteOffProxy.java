package net.aegistudio.resonance.io.midi;

import net.aegistudio.resonance.music.NoteOffEvent;

public class NoteOffProxy implements MidiEventProxy<NoteOffEvent>
{
	@Override
	public NoteOffEvent decapsulate(byte[] midiMessage) {
		byte pitch = midiMessage[1];
		return new NoteOffEvent(pitch, 0);
	}

	@Override
	public byte[] encapsulate(NoteOffEvent internalEvent) {
		byte[] noteOffEvent = new byte[3];
		noteOffEvent[0] = getEventCode();
		noteOffEvent[1] = internalEvent.note;
		noteOffEvent[2] = 0;
		return noteOffEvent;
	}

	@Override
	public byte getEventCode() {
		return (byte) 0x80;
	}
}
