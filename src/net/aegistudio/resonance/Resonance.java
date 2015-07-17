package net.aegistudio.resonance;

import javax.sound.sampled.AudioSystem;

import net.aegistudio.resonance.dataflow.DataflowController;
import net.aegistudio.resonance.dataflow.DataflowFacade;
import net.aegistudio.resonance.dataflow.ResetEvent;
import net.aegistudio.resonance.dataflow.StripNode;
import net.aegistudio.resonance.device.MixerDevice;
import net.aegistudio.resonance.device.OutputDevice;
import net.aegistudio.resonance.music.MusicFacade;
import net.aegistudio.resonance.music.NoteOnEvent;
import net.aegistudio.resonance.output.OutputController;
import net.aegistudio.resonance.output.OutputFacade;
import net.aegistudio.resonance.plugin.Event;
import net.aegistudio.resonance.plugin.Plugin;
import net.aegistudio.resonance.serial.Structure;

/**
 * This class serves as the main entry and master controller of 
 * the whole software.
 * @author aegistudio
 */

public class Resonance
{
	private OutputFacade outputFacade;
	private DataflowFacade dataflowFacade;
	private MusicFacade musicFacade;
	
	private Environment environment;
	private boolean hasEnvironmentChanged = true;
	
	private OutputDevice outputDevice;
	
	private MonitorQueue<Frame> idleQueue = new MonitorQueue<Frame>();
	private MonitorQueue<Frame> outputQueue = new MonitorQueue<Frame>();
	
	private boolean isPlaying = false;
	private boolean isPaused = false;
	
	public synchronized void play()
	{
		if(hasEnvironmentChanged || !isPaused)
		{
			this.idleQueue.clear();
			this.outputQueue.clear();
			for(int i = 0; i < environment.prerenderFactor; i ++)
				idleQueue.push(new Frame(environment.channels, environment.samplesPerFrame));
			musicFacade.reset(environment);
			outputFacade.reset(environment, outputDevice);
			dataflowFacade.reset(environment);
			hasEnvironmentChanged = false;
		}
		isPlaying = true;
		
		//Rendering Thread
		Thread threadRendering = new Thread()
		{
			@Override
			public void run()
			{
				while(isPlaying)
				{
					Frame frame = idleQueue.next();
					musicFacade.tick();
					dataflowFacade.render(frame);
					outputQueue.push(frame);
				}
			}
		};
		threadRendering.setPriority(Thread.MAX_PRIORITY);
		threadRendering.start();
		
		//Taping Thread
		Thread threadTaping = new Thread()
		{
			@Override
			public void run()
			{
				while(isPlaying)
				{
					Frame frame = outputQueue.next();
					outputFacade.output(frame);
					frame.zero();
					idleQueue.push(frame);
				}
				if(!isPaused)
					outputFacade.stop();
			}
		};
		threadTaping.setPriority(Thread.MAX_PRIORITY);
		threadTaping.start();
	}
	
	public synchronized void stop()
	{
		isPlaying = false;
		isPaused = false;
	}
	
	public synchronized void pause()
	{
		isPaused = true;
		isPlaying = false;
	}
	
	public synchronized void setEnvironment(Environment environment, OutputDevice device)
	{
		this.environment = environment;
		this.outputDevice = device;
		this.hasEnvironmentChanged = true;
	}
	
	public static void main(String[] arguments) throws Exception
	{
		Resonance res = new Resonance();
		res.musicFacade = new MusicFacade(){

			@Override
			public void load(Structure output) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void save(Structure output) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void tick() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setBeatsPerMinute(float bpm) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setBeatsPerMeasure(int beats) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public float getBeatsPerMinute() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getBeatsPerMeasure() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void reset(Environment environment) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setBeatPosition(double position) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public double getBeatPosition() {
				// TODO Auto-generated method stub
				return 0;
			}
		};
		res.dataflowFacade = new DataflowController(new StripNode());
		res.dataflowFacade.getSuperSource().addOutputNode(res.dataflowFacade.getSuperDrain());
		
		Plugin plugin = new Plugin()
		{

			float sampleRate;
			double phase = 0.0;

			@Override
			public void create(Structure parameter) {
				// TODO Auto-generated method stub
				
			}

			float[] pitches = new float[]{1.0f, 9.0f/8, 5.0f/4, 4.0f/3, 3.0f/2, 10.0f/6, 30.0f/16, 2.0f};
			float cycle;
			
			@Override
			public void trigger(Event event) {
				if(event instanceof ResetEvent)
				{
					this.sampleRate = ((ResetEvent) event).environment.sampleRate;
					this.phase = 0.0;
					this.cycle = sampleRate / 386.0f;
				}
				else if(event instanceof NoteOnEvent)
				{
					float cycle_base = sampleRate / 386.0f;
					cycle = cycle_base / pitches[((NoteOnEvent) event).getNote() % pitches.length];
				}
			}

			@Override
			public void process(Frame input, Frame output) {
				double[] left = output.getSamples(0);
				double[] right = output.getSamples(1);
				for(int i = 0; i < output.getSamplesPerFrame(); i ++)
				{
					left[i] = right[i] = Math.sin(phase);
					phase += 2 * Math.PI / cycle;
				}
			}

			@Override
			public void destroy() {
				// TODO Auto-generated method stub
				
			}
			
		};
		res.dataflowFacade.getSuperSource().setPlugin(plugin);
		
		res.outputFacade = new OutputController();
		res.setEnvironment(new Environment(44100.0f, 2, new Encoding(Encoding.BITDEPTH_BIT32 | Encoding.WORDTYPE_INT | Encoding.ENDIAN_BIG), 128, 2), new MixerDevice(AudioSystem.getMixerInfo()[0]));
		res.play();
		Thread.sleep(1000L);
		res.stop();
		res.setEnvironment(new Environment(88200.0f, 2, new Encoding(Encoding.BITDEPTH_BIT8 | Encoding.WORDTYPE_INT | Encoding.ENDIAN_BIG), 128, 2), new MixerDevice(AudioSystem.getMixerInfo()[1]));
		Thread.sleep(1000L);
		res.play();
		byte i = 0;
		while(true)
		{
			plugin.trigger(new NoteOnEvent(i, (byte)127, 0));
			i ++;
			Thread.sleep(1000L);
		}
	}
}
