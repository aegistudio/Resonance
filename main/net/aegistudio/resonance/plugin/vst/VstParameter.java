package net.aegistudio.resonance.plugin.vst;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class VstParameter {
	public final String label;
	public final String name;
	
	public final int index;
	public final VstPlugin plugin;

	private float value;
	private String display;
	
	public VstParameter(int index, VstPlugin plugin, InputStream input) throws IOException {
		DataInputStream dataInput = new DataInputStream(input);
		this.index = index;
		this.plugin = plugin;
		
		this.label = dataInput.readUTF();
		this.display = dataInput.readUTF();
		this.name = dataInput.readUTF();
		this.value = dataInput.readFloat();
		
		System.out.println("Label: " + label);
		System.out.println("Name: " + name);
		System.out.println("Display: " + display);
		System.out.println("Value: " + value);
	}
	
	public void setValue(float newValue) {
		plugin.operate((i, o, e) -> {
			EnumOperation.SET_PARAM.write(o);
			DataOutputStream param = new DataOutputStream(o);
			param.writeInt(index);
			param.writeFloat(newValue);
			param.flush();
			
			plugin.ensureInput();
			value = newValue;
			
			DataInputStream result = new DataInputStream(i);
			display = result.readUTF();
		});
	}
	
	public float getValue() {
		return this.value;
	}
	
	public String getDisplay() {
		return this.display;
	}
	
	public void sync() {
		plugin.operate((i, o, e) -> {
			EnumOperation.GET_PARAM.write(o);
			DataOutputStream param = new DataOutputStream(o);
			param.writeInt(index);
			param.flush();
			
			plugin.ensureInput();
			
			DataInputStream result = new DataInputStream(i);
			value = result.readFloat();
			display = result.readUTF();
		});
	}
}