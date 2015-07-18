package net.aegistudio.resonance.channel;

import java.util.ArrayList;

import net.aegistudio.resonance.KeywordArray;
import net.aegistudio.resonance.KeywordArray.KeywordEntry;
import net.aegistudio.resonance.LengthKeywordArray;
import net.aegistudio.resonance.RoundedKeywordArray;
import net.aegistudio.resonance.dataflow.RouterDrainNode;
import net.aegistudio.resonance.dataflow.RouterSourceNode;
import net.aegistudio.resonance.dataflow.StripNode;
import net.aegistudio.resonance.music.PluginContainer;
import net.aegistudio.resonance.plugin.Event;
import net.aegistudio.resonance.serial.Structure;
import net.aegistudio.resonance.serial.Type;

public class MidiChannel extends PluginContainer<StripNode> implements Channel
{
	/** This Two Nodes Are Useful When The Channel Was Muted. **/
	private final RouterSourceNode sourceNode;
	private final RouterDrainNode drainNode;
	
	private final LengthKeywordArray<ScoreClip> scoreClips;
	private final ScoreHolder scoreHolder;
	
	public MidiChannel(ScoreHolder scoreHolder){
		super(new StripNode());
		
		KeywordArray<Double, ScoreClip> innerArray = new RoundedKeywordArray<ScoreClip>();
		this.scoreHolder = scoreHolder;
		this.scoreClips = new LengthKeywordArray<ScoreClip>(innerArray);
		
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
		Structure[] scoreClips = input.get("scoreclips", Type.STRUCTURE_ARRAY, new Structure[0]);
		for(Structure entry : scoreClips)
		{
			double offset = entry.get("offset", Type.DOUBLE, 0.0);
			ScoreClip scoreClip = new ScoreClip(this.scoreHolder);
			scoreClip.load(entry);
			this.scoreClips.add(new KeywordArray.DefaultKeywordEntry<Double, ScoreClip>(offset, scoreClip));
		}
	}

	@Override
	public void save(Structure output) {
		super.save(output);
		output.set("mute", Type.BOOLEAN, this.isMuted());
		
		ArrayList<Structure> scoreClips = new ArrayList<Structure>();
		for(KeywordEntry<Double, ScoreClip> entry : this.scoreClips.all())
		{
			Structure entryStructure = new Structure();
			entryStructure.set("offset", Type.DOUBLE, entry.getKeyword());
			entry.getValue().save(entryStructure);
			scoreClips.add(entryStructure);
		}
		
		output.set("scoreclips", Type.STRUCTURE_ARRAY, scoreClips.toArray(new Structure[0]));
	}
	
	/**
	 * Notify The Channel That The Current Tick To Trigger.
	 */
	public void doTick(double begin, double end, int samplesPerFrame)
	{
		this.scoreClips.betweenTrigger(begin, end);
		for(KeywordEntry<Double, ScoreClip> clip : this.scoreClips.activated())
			for(Event event : clip.getValue().getEvents(begin - clip.getKeyword(), end - clip.getKeyword(), samplesPerFrame))
				super.plugin.trigger(event);
	}
}