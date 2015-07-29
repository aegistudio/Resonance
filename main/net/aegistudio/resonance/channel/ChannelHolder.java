package net.aegistudio.resonance.channel;

import java.util.TreeMap;

import net.aegistudio.resonance.KeywordArray.KeywordEntry;
import net.aegistudio.resonance.LengthObject;
import net.aegistudio.resonance.NamedHolder;
import net.aegistudio.resonance.dataflow.DataflowNode;
import net.aegistudio.resonance.dataflow.SourceNode;
import net.aegistudio.resonance.mixer.Track;
import net.aegistudio.resonance.serial.Structure;
import net.aegistudio.resonance.serial.Type;

public class ChannelHolder extends NamedHolder<Channel> implements LengthObject
{
	protected final NamedHolder<Track> mixer;
	protected final NamedHolder<Score> scoreHolder;
	public final DataflowNode superSource;
	
	public final TreeMap<String, NamedEntry<Track>> channelToTrackMap;
	
	public ChannelHolder(NamedHolder<Track> mixer, NamedHolder<Score> scoreHolder) {
		super("channel", true);
		this.scoreHolder = scoreHolder;
		this.mixer = mixer;
		this.superSource = new SourceNode();
		this.channelToTrackMap = new TreeMap<String, NamedEntry<Track>>();
	}

	@Override
	protected Channel newObject(Class<?> clazz) {
		if(clazz == MidiChannel.class)
		{
			MidiChannel midiChannel = new MidiChannel(scoreHolder);
			superSource.addOutputNode(midiChannel.sourceNode);
			midiChannel.sourceNode.addInputNode(superSource);
			
			midiChannel.drainNode.addOutputNode(mixer.get(null).trackSourceNode);
			mixer.get(null).trackSourceNode.addInputNode(midiChannel.drainNode);
			
			return midiChannel;
		}
		else return null;
	}
	
	/** if targetTrack == null, then target at master **/
	public synchronized void setTargetTrack(String channelName, String targetTrack)
	{
		Channel channel = this.get(channelName);
		if(channel == null) throw new IllegalArgumentException(String.format(super.notExists, channelName));
		DataflowNode channelOutputNode;
		if(channel instanceof MidiChannel) channelOutputNode = ((MidiChannel) channel).drainNode;
		else return;
		
		DataflowNode trackSource = channelToTrackMap.get(channelName)
				.getValue().trackSourceNode;
		trackSource.removeInputNode(channelOutputNode);
		channelOutputNode.removeOutputNode(trackSource);
		channelToTrackMap.remove(channelName);
		
		NamedEntry<Track> target = mixer.getEntry(targetTrack);
		channelOutputNode.addOutputNode(target.getValue().trackSourceNode);
		target.getValue().trackSourceNode.addInputNode(channelOutputNode);
		if(targetTrack != null)
			channelToTrackMap.put(channelName, target);
	}
	
	public synchronized void save(Structure output)
	{
		super.save(output);
		Structure channelMap = new Structure();
		output.set("channelmap", Type.STRUCTURE, channelMap);
		for(String key : channelToTrackMap.keySet())
			channelMap.set(key, Type.STRING, channelToTrackMap.get(key).getKeyword());
	}
	
	public synchronized void load(Structure input)
	{
		super.load(input);
		Structure channelMap = input.get("channelmap", Type.STRUCTURE, new Structure());
		for(String key : channelMap.keySet())
			this.channelToTrackMap.put(key, mixer.getEntry(channelMap.get(key, Type.STRING, null)));
	}
	
	public NamedEntry<Track> getTargetTrack(String name)
	{
		NamedEntry<Track> entry = this.channelToTrackMap.get(name);
		if(entry == null) return this.mixer.getEntry(null);
		else return entry;
	}
	
	double length = 0.0;
	public double getLength()
	{
		if(super.hasUpdated(super.hasUpdated(this))){
			for(KeywordEntry<String, Channel> channelEntry : super.allEntries())
				length = Math.max(length, channelEntry.getValue().getLength());
		}
		return length;
	}
}
