package net.aegistudio.resonance.plugin.vst;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class VstParameter {
	public final String label;
	public final String display;
	public final String name;
	
	public float value;
	
	public final int index;
	public final VstPlugin plugin;
	
	public VstParameter(int index, VstPlugin plugin, InputStream input) throws IOException {
		this.index = index;
		this.plugin = plugin;
		
		this.label = readString(input);
		this.display = readString(input);
		this.name = readString(input);
		this.value = new DataInputStream(input).readFloat();
		
		System.out.println("Label: " + label);
		System.out.println("Name: " + name);
		System.out.println("Display: " + display);
		System.out.println("Value: " + value);
	}
	
	private static String readString(InputStream input) throws IOException {
		DataInputStream din = new DataInputStream(input);
		byte[] buffer = new byte[din.readInt()];
		input.read(buffer);	return new String(buffer);
	}
}