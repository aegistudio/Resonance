package net.aegistudio.resonance.channel;

import net.aegistudio.resonance.serial.SerializedObject;
import net.aegistudio.resonance.serial.Structure;
import net.aegistudio.resonance.serial.Type;

public class Note implements SerializedObject
{
	public byte pitch;
	public byte velocity;
	public double duration;
	
	public Note(byte pitch, byte velocity, double duration)
	{
		this.pitch = pitch;
		this.velocity = velocity;
		this.duration = duration;
	}

	@Override
	public void load(Structure input) {
		this.pitch = input.get("note", Type.BYTE, (byte)0);
		this.velocity = input.get("velocity", Type.BYTE, (byte)127);
		this.duration = input.get("duration", Type.DOUBLE, 1.0);
	}

	@Override
	public void save(Structure output) {
		output.set("note", Type.BYTE, pitch);
		output.set("velocity", Type.BYTE, (byte)127);
		output.set("duration", Type.DOUBLE, 1.0);
	}
}
