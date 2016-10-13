package net.aegistudio.resonance.music;

import net.aegistudio.resonance.common.Environment;
import net.aegistudio.resonance.common.MusicFacade;
import net.aegistudio.resonance.dataflow.DataflowController;
import net.aegistudio.resonance.music.KeywordArray.KeywordEntry;
import net.aegistudio.resonance.music.channel.Channel;
import net.aegistudio.resonance.music.channel.ChannelHolder;
import net.aegistudio.resonance.music.channel.Score;
import net.aegistudio.resonance.music.mixer.Mixer;
import net.aegistudio.resonance.serial.Structure;
import net.aegistudio.resonance.serial.Type;

public class MusicController implements MusicFacade, LengthObject {
	protected float beatsPerMinute = 120.0f;
	protected float sampleRate = 0;
	protected int samplesPerFrame = 0;
	protected float increment = 0;
	
	protected double beatPosition = 0;
	
	public final NamedHolder<Score> scoreHolder;
	public final ChannelHolder channelHolder;
	public final Mixer mixer;
	public final DataflowController dataflowController;
	
	public MusicController() {

		this.scoreHolder = new OrderedNamedHolder<Score>("score", false) {
			@Override
			protected Score newObject(Class<?> clazz) {
				return new Score();
			}	
		};
		
		this.mixer = new Mixer();
		this.channelHolder = new ChannelHolder(mixer, scoreHolder);
		this.dataflowController = new DataflowController(((ChannelHolder)this.channelHolder).superSource);
		
		((Mixer)this.mixer).master.trackDrainNode.addOutputNode(this.dataflowController.getSuperDrain());
		this.dataflowController.getSuperDrain().addInputNode(((Mixer)this.mixer).master.trackDrainNode);
	}

	@Override
	public void load(Structure input) {
		this.beatsPerMinute = input.get("bpm", Type.FLOAT, 120.0f);
	}

	@Override
	public void save(Structure output) {
		output.set("bpm", Type.FLOAT, this.beatsPerMinute);
	}

	@Override
	public void reset(Environment environment) {
		this.sampleRate = environment.sampleRate;
		this.samplesPerFrame = environment.samplesPerFrame;
		this.increment = samplesPerFrame / sampleRate * (beatsPerMinute / 60);
		this.channelHolder.reset();
	}

	@Override
	public void tick() {
		double newBeatPosition = this.beatPosition + increment;
		for(KeywordEntry<String, Channel> channel : this.channelHolder.allValues())
			channel.getValue().doTick(beatPosition, newBeatPosition, samplesPerFrame);
		this.beatPosition = newBeatPosition;
	}

	@Override
	public void setBeatsPerMinute(float bpm) {
		this.beatsPerMinute = bpm;
		this.increment = samplesPerFrame / sampleRate * (beatsPerMinute / 60);
	}

	@Override
	public float getBeatsPerMinute() {
		return this.beatsPerMinute;
	}

	@Override
	public void setBeatPosition(double position) {
		/** some reset should be done! **/
		this.beatPosition = position;
	}

	@Override
	public double getBeatPosition() {
		return this.beatPosition;
	}

	public NamedHolder<Score> getScoreHolder() {
		return scoreHolder;
	}

	public ChannelHolder getChannelHolder() {
		return channelHolder;
	}

	public Mixer getMixer() {
		return mixer;
	}

	@Override
	public double getLength() {
		return channelHolder.getLength();
	}
}
