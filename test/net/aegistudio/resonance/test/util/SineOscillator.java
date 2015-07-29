package net.aegistudio.resonance.test.util;

import java.util.ArrayList;
import java.util.HashMap;

import net.aegistudio.resonance.Frame;
import net.aegistudio.resonance.dataflow.ResetEvent;
import net.aegistudio.resonance.music.NoteOffEvent;
import net.aegistudio.resonance.music.NoteOnEvent;
import net.aegistudio.resonance.plugin.Event;
import net.aegistudio.resonance.plugin.Plugin;
import net.aegistudio.resonance.serial.Structure;

/**
 * A multiple tone sine oscillator. Driven by midi events.
 * @author aegistudio
 */

public class SineOscillator implements Plugin
{
	public final HashMap<Integer, ArrayList<PartialOscillator>> oscillators
		= new HashMap<Integer, ArrayList<PartialOscillator>>();
	
	public final HashMap<Integer, Integer> toRemove	= new HashMap<Integer, Integer>();
	
	@Override
	public void create(Structure parameter) {
		
	}

	float sampleRate;
	float[] pitches = new float[]{1.0f, 16.0f/15, 9.0f/8, 6.0f/5, 5.0f/4, 4.0f/3, 64.0f/45, 3.0f/2, 8.0f/5, 10.0f/6, 10.0f/6 * 16.0f/15, 30.0f/16};
	Frame process;
	int polytones = 0;
	
	@Override
	public void trigger(Event event) {
		if(event instanceof ResetEvent)
		{
			this.sampleRate = ((ResetEvent) event).environment.sampleRate;
			this.process = new Frame(((ResetEvent) event).environment.channels, ((ResetEvent) event).environment.samplesPerFrame);
			oscillators.clear();
			toRemove.clear();
			polytones = 0;
		}
		else if(event instanceof NoteOnEvent)
		{
			int note = ((NoteOnEvent) event).getNote();
			float phaseIncrement = (float) (2.0f * Math.PI * (1 << (note / pitches.length)) * 386.0f / sampleRate * pitches[note % pitches.length]);
			new PartialOscillator(this, note, phaseIncrement);
			polytones ++;
		}
		else if(event instanceof NoteOffEvent)
		{
			int note = ((NoteOffEvent) event).getNote();
			Integer value = toRemove.get(note);
			if(value == null) value = 0;
			toRemove.put(note, value + 1);
		}	
	}

	@Override
	public void process(Frame input, Frame output) {
		for(Integer key : toRemove.keySet())
		{
			Integer count = toRemove.get(key);
			ArrayList<PartialOscillator> list = oscillators.get(key);
			if(list == null) continue;
			for(int i = 0; i < count; i ++)
				if(!list.isEmpty())
				{
					list.remove(0);
					polytones --;
				}
		}
		toRemove.clear();
		
		output.zero(); process.zero();
		for(ArrayList<PartialOscillator> oscillatorList : oscillators.values())
			for(PartialOscillator oscillator : oscillatorList)
			{
				oscillator.process(process);
				output.mix(process, 1, 1.0/polytones);
			}
	}

	@Override
	public void destroy() {
		
	}

	class PartialOscillator
	{
		float phaseIncrement;
		float phase;
		
		public PartialOscillator(SineOscillator oscillator, int tone, float phaseIncrement)
		{
			ArrayList<PartialOscillator> oscillatorList = oscillator.oscillators.get(tone);
			if(oscillatorList == null)
				oscillator.oscillators.put(tone, oscillatorList = new ArrayList<PartialOscillator>());
			oscillatorList.add(this);
			this.phaseIncrement = phaseIncrement;
		}
		
		public void process(Frame output)
		{
			double[] left = output.getSamples(0);
			double[] right = output.getSamples(1);
			
			for(int i = 0; i < output.getSamplesPerFrame(); i ++)
			{
				left[i] = right[i] = Math.sin(phase);
				phase += phaseIncrement;
			}
		}
	}
}
