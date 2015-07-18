package net.aegistudio.resonance.test.acoustic;

import javax.sound.sampled.AudioSystem;

import net.aegistudio.resonance.Encoding;
import net.aegistudio.resonance.Environment;
import net.aegistudio.resonance.Frame;
import net.aegistudio.resonance.Resonance;
import net.aegistudio.resonance.dataflow.DataflowController;
import net.aegistudio.resonance.dataflow.ResetEvent;
import net.aegistudio.resonance.dataflow.StripNode;
import net.aegistudio.resonance.device.MixerDevice;
import net.aegistudio.resonance.music.MusicFacade;
import net.aegistudio.resonance.music.NoteOffEvent;
import net.aegistudio.resonance.music.NoteOnEvent;
import net.aegistudio.resonance.output.OutputController;
import net.aegistudio.resonance.output.OutputFacade;
import net.aegistudio.resonance.plugin.Event;
import net.aegistudio.resonance.plugin.Plugin;
import net.aegistudio.resonance.serial.Structure;

/**
 * ============================= Test Description =========================
 * <br><b>This test could not be automatically operated!</b><br>
 * <br>
 * This test is intended to test the trigger() and process() interface 
 * of the Plugin. When testing, the test method will initialize a non-GUI
 * resonance. The super source of data flow layer will be a single strip 
 * node with a intended sine wave generator in it. <br>
 * <br>
 * When testing, the test method will pass a MIDI event into the plugin
 * every 1/2 second (not exactly), and the plugin will change its tone or stop 
 * responding to the MIDI event. <br>
 * <br>
 * <b>Proper work of events determines whether the music layer could work 
 * properly!</b><br>
 * ========================================================================
 * @author aegistudio
 */

public class PluginEvent{
	public static void main(String[] arguments) throws Exception
	{
		MusicFacade musicDecoy = new MusicDecoy();
		
		DataflowController dataflowFacade = new DataflowController(new StripNode());
		dataflowFacade.getSuperSource().addOutputNode(dataflowFacade.getSuperDrain());
		
		Plugin plugin = new Plugin()
		{

			float sampleRate;
			double phase = 0.0;

			@Override
			public void create(Structure parameter) {
				
			}

			float[] pitches = new float[]{1.0f, 9.0f/8, 5.0f/4, 4.0f/3, 3.0f/2, 10.0f/6, 30.0f/16, 2.0f};
			float cycle = 0.0f;
			
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
				else if(event instanceof NoteOffEvent)
					cycle = 0.0f;
			}

			@Override
			public void process(Frame input, Frame output) {
				if(cycle == 0.0f)
				{
					output.copy(input);
					phase = 0.0;
					return;
				}
				
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
				
			}
			
		};
		
		dataflowFacade.getSuperSource().setPlugin(plugin);
		
		OutputFacade outputFacade = new OutputController();
		
		Resonance res = new Resonance(outputFacade, dataflowFacade, musicDecoy);
		
		res.setEnvironment(new Environment(44100.0f, 2, new Encoding(Encoding.BITDEPTH_BIT32 | Encoding.WORDTYPE_INT | Encoding.ENDIAN_BIG), 128, 2), new MixerDevice(AudioSystem.getMixerInfo()[0]));
		res.play();
		
		int i = 0;
		while(true)
		{
			byte[] melody = new byte[]{0, -1, 0, -1, 4, -1, 4, -1, 5, -1, 5, -1, 4, 4, 4, -1,
					3, -1, 3, -1, 2, -1, 2, -1, 1, -1, 1, -1, 0, 0, 0, -1};
			if(melody[i] == -1) plugin.trigger(new NoteOffEvent((byte) 0, 0));
			else plugin.trigger(new NoteOnEvent(melody[i], (byte)127, 0));
			i = ((i + 1) % melody.length);
			
			Thread.sleep(500L);
		}
	}
}
