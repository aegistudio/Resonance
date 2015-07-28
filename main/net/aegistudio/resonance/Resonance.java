package net.aegistudio.resonance;

import net.aegistudio.resonance.dataflow.DataflowFacade;
import net.aegistudio.resonance.device.OutputDevice;
import net.aegistudio.resonance.music.MusicFacade;
import net.aegistudio.resonance.output.OutputFacade;
/**
 * This class serves as the main entry and master controller of 
 * the whole software.
 * @author aegistudio
 */

public class Resonance
{
	public final OutputFacade outputFacade;
	public final DataflowFacade dataflowFacade;
	public final MusicFacade musicFacade;
	
	private Environment environment;
	private boolean hasEnvironmentChanged = true;
	
	private OutputDevice outputDevice;
	
	private MonitorQueue<Frame> idleQueue = new MonitorQueue<Frame>();
	private MonitorQueue<Frame> outputQueue = new MonitorQueue<Frame>();
	
	private boolean isPlaying = false;
	private boolean isPaused = false;
	
	public Resonance(OutputFacade outputFacade, DataflowFacade dataFlowFacade, MusicFacade musicFacade)
	{
		this.outputFacade = outputFacade;
		this.dataflowFacade = dataFlowFacade;
		this.musicFacade = musicFacade;
	}
	
	public synchronized void play()
	{
		if(isPlaying) return;
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
					frame.zero();
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
}
