package net.aegistudio.resonance.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import net.aegistudio.resonance.common.Event;

public class NoteOnEvent extends NoteEvent implements Event {
	public final byte velocity;
	
	public NoteOnEvent(byte note, byte velocity, int frameOffset) {
		super(note, frameOffset);
		this.velocity = velocity;
	}
	
	public int getVelocity() {
		return this.velocity;
	}

	public static void loadFactory() {
		new MidiEventFactory(ShortMessage.NOTE_ON, m -> {	
			ShortMessage midiMessage = (ShortMessage) m;
			byte pitch = (byte) midiMessage.getData1();
			byte velocity = (byte) midiMessage.getData2();
			return new NoteOnEvent(pitch, velocity, 0);
		});
	}
	
	@Override
	public MidiMessage toMidiMessage() {
		try {
			ShortMessage noteOnEvent = new ShortMessage();
			noteOnEvent.setMessage(ShortMessage.NOTE_ON, note, velocity);
			return noteOnEvent;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
			return null;
		}
	}
}
