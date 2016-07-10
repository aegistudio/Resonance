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
			//	"C:\\Program Files\\Image-Line\\FL Studio 12.1\\Plugins\\VST\\Fruity Reeverb.dll");
				"Z:\\home\\luohaoran\\vst.plugin\\TruePianos.dll");
		plugin.create(null);
		
		if(plugin.hasEditor) {
			plugin.openEditor();
			plugin.openEditor();
			plugin.openEditor();
			plugin.openEditor();
			plugin.openEditor();
			System.out.println(plugin.isEditorOpen());
		}
		Thread.sleep(1000L);
		

		if(plugin.hasEditor)
			plugin.closeEditor();
		
		Thread.sleep(1000L);
		if(plugin.hasEditor) {
			plugin.openEditor();
			plugin.openEditor();
			plugin.openEditor();
			plugin.openEditor();
			plugin.openEditor();
			System.out.println(plugin.isEditorOpen());
		}
		Thread.sleep(1000L);
		
		long mills;
		for(int i = 0; i < 10; i ++) {
			mills = System.nanoTime();
			plugin.paramList[0].setValue(i * .1f);
			System.out.println(plugin.paramList[0].getValue());
			System.out.println(plugin.paramList[0].getDisplay());
			System.out.println("Mills: " + (System.nanoTime() - mills) / 1.e6);
			
			Thread.sleep(100L);
		}

		if(plugin.hasEditor)
			plugin.closeEditor();
		
		plugin.destroy();
	}
}
