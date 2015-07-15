package net.aegistudio.resonance;

import javax.sound.sampled.AudioSystem;

import net.aegistudio.resonance.dataflow.DataflowFacade;
import net.aegistudio.resonance.device.MixerDevice;
import net.aegistudio.resonance.device.OutputDevice;
import net.aegistudio.resonance.music.MusicFacade;
import net.aegistudio.resonance.output.OutputController;
import net.aegistudio.resonance.output.OutputFacade;
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
	
	private long tapingTick = 0L;
	private long renderingTick = 0L;
	
	public synchronized void play()
	{
		if(hasEnvironmentChanged || !isPaused)
		{
			this.idleQueue.clear();
			this.outputQueue.clear();
			for(int i = 0; i < environment.prerenderFactor; i ++)
				idleQueue.push(new Frame(environment.channels, environment.samplesPerFrame));
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
					musicFacade.tick(renderingTick);;
					dataflowFacade.render(frame);
					outputQueue.push(frame);
					renderingTick += frame.getSamplesPerFrame();
				}
				if(!isPaused) tapingTick = 0L;
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
					idleQueue.push(frame);
					tapingTick += frame.getSamplesPerFrame();
				}
				if(!isPaused)
				{
					outputFacade.stop();
					renderingTick = 0L;
				}
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
	
	public long getTapingTick()
	{
		return this.tapingTick;
	}
	
	public static void main(String[] arguments)
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
			public void tick(long tick) {
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
		};
		res.dataflowFacade = new DataflowFacade()
		{

			@Override
			public void load(Structure output) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void save(Structure output) {
				// TODO Auto-generated method stub
				
			}

			float sampleRate;
			long tick = 0;
			@Override
			public void render(Frame frame) {
				float cycle_base = sampleRate /386.0f;
				float[] pitches = new float[]{1.0f, 9.0f/8, 5.0f/4, 4.0f/3, 3.0f/2, 10.0f/6, 30.0f/16, 2.0f};
				double[] left = frame.getSamples(0);
				double[] right = frame.getSamples(1);
				for(int i = 0; i < frame.getSamplesPerFrame(); i ++)
				{
					float cycle = cycle_base / pitches[(int)(tick / sampleRate) % pitches.length];
					left[i] = right[i] = Math.sin(2 * Math.PI * tick / cycle);
					tick ++;
				}
			}

			@Override
			public void reset(Environment environment) {
				this.sampleRate = environment.sampleRate;
			}
			
		};
		res.outputFacade = new OutputController();
		res.setEnvironment(new Environment(44100.0f, 2, new Encoding(Encoding.BITDEPTH_BIT32 | Encoding.WORDTYPE_INT | Encoding.ENDIAN_BIG), 128, 2), new MixerDevice(AudioSystem.getMixerInfo()[0]));
		res.play();
	}
}
