package net.aegistudio.resonance.plugin.vst;

import java.io.DataInputStream;
import java.io.IOException;

public class VstParameter {
	public final String label;
	public final String name;
	
	public final int index;
	public final VstPlugin plugin;

	private float value;
	private String display;
	
	public VstParameter(int index, VstPlugin plugin, DataInputStream input) throws IOException {
		this.index = index;
		this.plugin = plugin;
		
		this.label = input.readUTF();
		this.display = input.readUTF();
		this.name = input.readUTF();
		this.value = input.readFloat();
	}
	
	public void setValue(float newValue) {
		plugin.operate((i, o, e) -> {
			EnumOperation.SET_PARAM.write(o);
			o.writeInt(index);
			o.writeFloat(newValue);
			o.flush();
			
			plugin.ensureInput();
			value = newValue;
			
			display = i.readUTF();
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
			o.writeInt(index);
			o.flush();
			
			plugin.ensureInput();
			
			value = i.readFloat();
			display = i.readUTF();
		});
	}
}