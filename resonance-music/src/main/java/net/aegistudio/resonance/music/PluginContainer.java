package net.aegistudio.resonance.music;

import net.aegistudio.resonance.dataflow.DataflowNode;
import net.aegistudio.resonance.plugin.Plugin;
import net.aegistudio.resonance.serial.SerializedObject;
import net.aegistudio.resonance.serial.Structure;
import net.aegistudio.resonance.serial.Type;

import java.util.concurrent.Callable;

public class PluginContainer<T extends DataflowNode> implements SerializedObject
{	
	/** The generator Node for this channel **/
	protected final T pluginNode;
	protected Plugin plugin;
	protected Structure parameters = new Structure();

	public PluginContainer(T pluginNode) {
		this.pluginNode = pluginNode;
	}
	
	/** Only Used When Changing Instruments In The Channel **/
	public void setPlugin(Plugin plugin) {
		if(this.plugin != null)
			this.plugin.destroy();
		this.parameters = new Structure();
		this.pluginNode.setPlugin(plugin);
		this.plugin = plugin;
		if(this.plugin != null)
			this.plugin.create(parameters);
	}
	
	public Plugin getPlugin() {
		return this.plugin;
	}
	
	public Structure getParameters() {
		return this.parameters;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void load(Structure input) {
		Class nullClazz = null;
		Class pluginClazz = input.get("plugin", Type.CLASS, nullClazz);
		if(pluginClazz != null) try {
			this.plugin = (Plugin) pluginClazz.newInstance();
			Callable<Structure> creationOnNull = () -> new Structure();
			this.parameters = input.get("parameters", Type.STRUCTURE, creationOnNull);
			this.plugin.create(parameters);
		}
		catch(Exception e) {
			throw new IllegalArgumentException("Cannot initialize plugin " + pluginClazz.getName());
		}
	}

	@Override
	public void save(Structure output) {
		output.set("plugin", Type.CLASS, this.plugin == null? null : this.plugin.getClass());
		output.set("parameters", Type.STRUCTURE, this.parameters);
	}
}
