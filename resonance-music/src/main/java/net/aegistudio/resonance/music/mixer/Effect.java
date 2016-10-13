package net.aegistudio.resonance.music.mixer;

import net.aegistudio.resonance.dataflow.BlendStripNode;
import net.aegistudio.resonance.dataflow.RouterDrainNode;
import net.aegistudio.resonance.dataflow.RouterSourceNode;
import net.aegistudio.resonance.music.PluginContainer;
import net.aegistudio.resonance.serial.Structure;
import net.aegistudio.resonance.serial.Type;

public class Effect extends PluginContainer<BlendStripNode>{
	
	/** This Two Nodes Are Useful When The Effect Was Muted. **/
	// If A Effect Were Passed, The Stream Flow Into Will Flow Out As Usual.
	final RouterSourceNode sourceNode;
	final RouterDrainNode drainNode;
	
	public Effect(){
		super(new BlendStripNode());
		this.sourceNode = new RouterSourceNode();
		this.drainNode = new RouterDrainNode();
		
		/** Connect Source Node With The Effect Node **/
		this.sourceNode.addOutputNode(pluginNode);
		this.pluginNode.addInputNode(sourceNode);
		
		/** Connect Drain Node With The Effect Node **/
		this.drainNode.addInputNode(pluginNode);
		this.pluginNode.addOutputNode(drainNode);
	}
	
	public void setMuted(boolean muted){
		this.sourceNode.setMuted(muted);
	}

	public double dryWetRatio = 0.0;
	
	public void setDryWetRatio(double value){
		if(value >= 100.0) value = 100.0;
		if(value <= 0.0) value = 0.0;
		this.dryWetRatio = value;
		super.pluginNode.setRatio(1 - value, value);
	}
	
	public void load(Structure input){
		super.load(input);
		this.dryWetRatio = input.get("dryWetRatio", Type.DOUBLE, 0.0);
		this.setMuted(input.get("mute", Type.BOOLEAN, true));
	}
}
