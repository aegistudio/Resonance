package net.aegistudio.resonance.channel;

import net.aegistudio.resonance.dataflow.RouterDrainNode;
import net.aegistudio.resonance.dataflow.RouterSourceNode;
import net.aegistudio.resonance.dataflow.StripNode;
import net.aegistudio.resonance.music.PluginContainer;
import net.aegistudio.resonance.serial.Structure;
import net.aegistudio.resonance.serial.Type;

public class MidiChannel extends PluginContainer<StripNode> implements Channel
{
	/** This Two Nodes Are Useful When The Channel Was Muted. **/
	private final RouterSourceNode sourceNode;
	private final RouterDrainNode drainNode;
	
	public MidiChannel(){
		super(new StripNode());
		this.sourceNode = new RouterSourceNode();
		this.drainNode = new RouterDrainNode();
		
		/** Connect Router Node With The Channel Node **/
		this.sourceNode.addOutputNode(pluginNode);
		this.pluginNode.addInputNode(sourceNode);
		
		/** Connect Drain Node With The Channel Node **/
		this.drainNode.addInputNode(pluginNode);
		this.pluginNode.addOutputNode(drainNode);
	}
	
	public void setMuted(boolean muted){
		this.sourceNode.setMuted(muted);
	}

	public boolean isMuted(){
		return this.sourceNode.isMuted();
	}
	
	@Override
	public void load(Structure input) {
		super.load(input);
		this.sourceNode.setMuted(input.get("mute", Type.BOOLEAN, false));
	}

	@Override
	public void save(Structure output) {
		super.save(output);
		output.set("mute", Type.BOOLEAN, this.isMuted());
	}
	
	/**
	 * Notify The Channel That The Current Tick To Trigger.
	 */
	public void doTick(double begin, double end, int samplesPerFrame)
	{
	}
}
