package net.aegistudio.resonance.io.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import net.aegistudio.resonance.music.NoteOnEvent;

public class NoteOnProxy implements MidiEventProxy<NoteOnEvent, ShortMessage>
{
	@Override
	public NoteOnEvent decapsulate(ShortMessage midiMessage) {
		byte pitch = (byte) midiMessage.getData1();
		byte velocity = (byte) midiMessage.getData2();
		return new NoteOnEvent(pitch, velocity, 0);
	}

	@Override
	public ShortMessage encapsulate(NoteOnEvent internalEvent) {
		try {
			ShortMessage noteOnEvent = new ShortMessage();
			noteOnEvent.setMessage(getEventCode(), internalEvent.note, internalEvent.velocity);
			return noteOnEvent;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int getEventCode() {
		return ShortMessage.NOTE_ON;
	}
}
