package net.aegistudio.resonance.io.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import net.aegistudio.resonance.music.NoteOffEvent;

public class NoteOffProxy implements MidiEventProxy<NoteOffEvent, ShortMessage>
{
	@Override
	public NoteOffEvent decapsulate(ShortMessage midiMessage) {
		byte pitch = (byte) midiMessage.getData1();
		return new NoteOffEvent(pitch, 0);
	}

	@Override
	public ShortMessage encapsulate(NoteOffEvent internalEvent) {
		try {
			ShortMessage result = new ShortMessage();
			result.setMessage(getEventCode(), 0, internalEvent.note, 63);
			return result;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int getEventCode() {
		return ShortMessage.NOTE_OFF;
	}
}
