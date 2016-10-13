package net.aegistudio.resonance.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

public class NoteOffEvent extends NoteEvent {
	public NoteOffEvent(byte note, int frameOffset) {
		super(note, frameOffset);
	}

	public static void loadFactory() {
		new MidiEventFactory(ShortMessage.NOTE_OFF, m -> {	
			ShortMessage midiMessage = (ShortMessage) m;
			byte pitch = (byte) midiMessage.getData1();
			return new NoteOffEvent(pitch, 0);
		});
	}
	
	@Override
	public MidiMessage toMidiMessage() {
		try {
			ShortMessage result = new ShortMessage();
			result.setMessage(ShortMessage.NOTE_OFF, 0, note, 63);
			return result;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
			return null;
		}
	}
}
