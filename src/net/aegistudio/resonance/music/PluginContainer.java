package net.aegistudio.resonance.music;

import net.aegistudio.resonance.dataflow.DataflowNode;
import net.aegistudio.resonance.plugin.Plugin;
import net.aegistudio.resonance.serial.SerializedObject;
import net.aegistudio.resonance.serial.Structure;
import net.aegistudio.resonance.serial.Type;

public class PluginContainer<T extends DataflowNode> implements SerializedObject
{	
	/** The generator Node for this channel **/
	protected final T pluginNode;
	private Plugin plugin;
	private Structure parameters = new Structure();

	public PluginContainer(T pluginNode){
		this.pluginNode = pluginNode;
	}
	
	/** Only Used When Changing Instruments In The Channel **/
	public void setPlugin(Plugin plugin){
		if(this.plugin != null)
			this.plugin.destroy();
		this.parameters = new Structure();
		this.pluginNode.setPlugin(plugin);
		this.plugin = plugin;
		this.plugin.create(parameters);
	}
	
	public Structure getParameters()
	{
		return this.parameters;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void load(Structure input) {
		Class pluginClazz = input.get("plugin", Type.CLASS, null);
		if(pluginClazz != null) try
		{
			this.plugin = (Plugin) pluginClazz.newInstance();
			this.parameters = input.get("parameters", Type.STRUCTURE, new Structure());
			this.plugin.create(parameters);
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException("Cannot initialize plugin " + pluginClazz.getName());
		}
	}

	@Override
	public void save(Structure output) {
		output.set("plugin", Type.CLASS, this.plugin == null? null : this.plugin.getClass());
		output.set("parameters", Type.STRUCTURE, this.parameters);
	}
}
