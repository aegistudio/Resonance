package net.aegistudio.resonance.plugin;

import net.aegistudio.resonance.plugin.vst.VstFactory;
import net.aegistudio.resonance.plugin.vst.VstPlugin;

public class VstPluginLoading {
	public static void main(String[] arguments) throws Exception{
		VstPlugin plugin = new VstFactory().create("/home/luohaoran/vst.plugin/vRAM-epiano.dll");
		plugin.create(null);
		
		if(plugin.hasEditor) {
			plugin.openEditor();
			plugin.openEditor();
			plugin.openEditor();
			plugin.openEditor();
			plugin.openEditor();
			System.out.println(plugin.isEditorOpen());
		}
		Thread.sleep(10000L);
		new Thread(() -> plugin.readError(e -> System.out.println(e))).start();
		
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
