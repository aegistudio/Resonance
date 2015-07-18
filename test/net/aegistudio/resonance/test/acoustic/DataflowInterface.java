package net.aegistudio.resonance.test.acoustic;

import javax.sound.sampled.AudioSystem;

import net.aegistudio.resonance.Encoding;
import net.aegistudio.resonance.Environment;
import net.aegistudio.resonance.Frame;
import net.aegistudio.resonance.Resonance;
import net.aegistudio.resonance.dataflow.DataflowFacade;
import net.aegistudio.resonance.dataflow.DataflowNode;
import net.aegistudio.resonance.device.MixerDevice;
import net.aegistudio.resonance.music.MusicFacade;
import net.aegistudio.resonance.output.OutputController;
import net.aegistudio.resonance.output.OutputFacade;
import net.aegistudio.resonance.serial.Structure;

/**
 * ============================= Test Description =========================
 * <br><b>This test could not be automatically operated!</b><br>
 * <br>
 * This test is intended to test the reset() and render() method of the 
 * DataflowFacade. When testing, the test method will simply pass the frame 
 * to the facade and the facade will maintain a counter in it. Counting and
 * changes its tone every second.<br>
 * <br>
 * The test method will last for 18 seconds (not exactly), and 2 octaves 
 * arpeggios will be rendered after 18 seconds.
 * <br>
 * <b>Proper work of dataflow interface determines whether the abstraction
 * of dataflow layer will be effective.</b><br>
 * ========================================================================
 * @author aegistudio
 */

public class DataflowInterface
{
	public static void main(String[] arguments) throws Exception
	{
		MusicFacade musicDecoy = new MusicDecoy();
		
		DataflowFacade dataflowFacade = new DataflowFacade()
		{

			@Override
			public void load(Structure input) {			}

			@Override
			public void save(Structure output) {		}

			long ticks = 0;
			
			@Override
			public void render(Frame frame)
			{
				double[] left = frame.getSamples(0);
				double[] right = frame.getSamples(1);
				
				
				for(int i = 0; i < frame.getSamplesPerFrame(); i ++)
				{
					float cycle = sampleRate / (386 * pitches[(int) ((ticks / sampleRate) % pitches.length)]);
					left[i] = right[i] = Math.sin(2 * Math.PI * ticks / cycle);
					ticks ++;
				}
			}
			
			float[] pitches = new float[]{1.0f, 9.0f/8, 5.0f/4, 4.0f/3, 3.0f/2, 10.0f/6, 30.0f/16, 2.0f};
			
			float sampleRate;
			@Override
			public void reset(Environment environment)
			{
				this.sampleRate = environment.sampleRate;
			}

			@Override
			public DataflowNode getSuperSource() {
				return null;
			}

			@Override
			public DataflowNode getSuperDrain() {
				return null;
			}
			
		};
		
		
		OutputFacade outputFacade = new OutputController();
		
		Resonance res = new Resonance(outputFacade, dataflowFacade, musicDecoy);
		
		res.setEnvironment(new Environment(44100.0f, 2, new Encoding(Encoding.BITDEPTH_BIT32 | Encoding.WORDTYPE_INT | Encoding.ENDIAN_BIG), 128, 2), new MixerDevice(AudioSystem.getMixerInfo()[0]));
		res.play();
		Thread.sleep(16000L);
		res.stop();
	}
}
