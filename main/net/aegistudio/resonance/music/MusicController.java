package net.aegistudio.resonance.music;

import net.aegistudio.resonance.Environment;
import net.aegistudio.resonance.NamedHolder;
import net.aegistudio.resonance.channel.Channel;
import net.aegistudio.resonance.channel.ChannelHolder;
import net.aegistudio.resonance.channel.Score;
import net.aegistudio.resonance.dataflow.DataflowController;
import net.aegistudio.resonance.mixer.Mixer;
import net.aegistudio.resonance.mixer.Track;
import net.aegistudio.resonance.serial.Structure;
import net.aegistudio.resonance.serial.Type;

public class MusicController implements MusicFacade
{
	protected float beatsPerMinute = 120.0f;
	protected float sampleRate = 0;
	protected int samplesPerFrame = 0;
	protected float increment = 0;
	
	protected double beatPosition = 0;
	
	public final NamedHolder<Score> scoreHolder;
	public final NamedHolder<Channel> channelHolder;
	public final NamedHolder<Track> mixer;
	public final DataflowController dataflowController;
	
	public MusicController()
	{

		this.scoreHolder = new NamedHolder<Score>("score", false)
		{
			@Override
			protected Score newObject(Class<?> clazz) {
				return new Score();
			}	
		};
		
		this.mixer = new Mixer();
		this.channelHolder = new ChannelHolder(mixer, scoreHolder);
		this.dataflowController = new DataflowController(((ChannelHolder)this.channelHolder).superSource);
		((Mixer)this.mixer).master.trackSourceNode.addOutputNode(this.dataflowController.getSuperDrain());
		this.dataflowController.getSuperDrain().addInputNode(((Mixer)this.mixer).master.trackSourceNode);
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
	}

	@Override
	public void tick() {
		double newBeatPosition = this.beatPosition + increment;
		for(Channel channel : this.channelHolder.all())
			channel.doTick(beatPosition, newBeatPosition, samplesPerFrame);
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

}