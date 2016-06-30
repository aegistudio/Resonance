package net.aegistudio.resonance.plugin.vst;

import java.io.File;
import java.io.IOException;

public class VstFactory {
	public final String[] process;
	
	public VstFactory() throws VstException {
		// Check existence of vstloader.
		if(!new File("vstloader.exe").exists())
			throw new VstException("No vstloader presents.");
		
		// Check operating system.
		String os = System.getProperty("os.name").toLowerCase();
		String process[] = null;
		if(os.contains("linux")) {
			// Check existence of wine.
			try {	Runtime.getRuntime().exec("wine"); }
			catch(IOException e) { 	
				throw new VstException("Unable to locate program loader."); 
			};
			process = new String[] { "wine", "vstloader.exe" };
		}
		else if(os.contains("windows"))
			process = new String[] { "vstloader.exe" };
		
		if(process == null) 
			throw new VstException("Unsupported operating system!");
		this.process = process;
	}
	
	public VstPlugin create(String vst) {
		return new VstPlugin(process, vst);
	}
	
	public static void main(String[] arguments) throws Exception {
		VstPlugin plugin = new VstFactory().create(
				"C:\\Program Files\\Image-Line\\FL Studio 12.1\\Plugins\\VST\\Fruity Reeverb.dll");
		plugin.create(null);
		
		VstPlugin plugin2 = new VstFactory().create(
				"C:\\Program Files\\Image-Line\\FL Studio 12.1\\Plugins\\VST\\Fruity Reeverb.dll");
		plugin2.create(null);
		
		plugin.destroy();
		plugin2.destroy();
	}
}
