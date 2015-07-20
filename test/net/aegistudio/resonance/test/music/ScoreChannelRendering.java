package net.aegistudio.resonance.test.music;

import javax.sound.sampled.AudioSystem;

import net.aegistudio.resonance.Encoding;
import net.aegistudio.resonance.Environment;
import net.aegistudio.resonance.KeywordArray;
import net.aegistudio.resonance.Resonance;
import net.aegistudio.resonance.KeywordArray.KeywordEntry;
import net.aegistudio.resonance.NamedHolder;
import net.aegistudio.resonance.channel.Channel;
import net.aegistudio.resonance.channel.MidiChannel;
import net.aegistudio.resonance.channel.Note;
import net.aegistudio.resonance.channel.Score;
import net.aegistudio.resonance.channel.ScoreClip;
import net.aegistudio.resonance.dataflow.DataflowController;
import net.aegistudio.resonance.device.MixerDevice;
import net.aegistudio.resonance.music.MusicFacade;
import net.aegistudio.resonance.music.ScaleNote;
import net.aegistudio.resonance.output.OutputController;
import net.aegistudio.resonance.output.OutputFacade;
import net.aegistudio.resonance.plugin.Plugin;
import net.aegistudio.resonance.test.acoustic.MusicDecoy;
import net.aegistudio.resonance.test.util.SineOscillator;

public class ScoreChannelRendering {
	public static void main(String[] arguments)
	{	
		Plugin plugin = new SineOscillator();
		
		NamedHolder<Score> scoreHolder = new NamedHolder<Score>("score", false)
		{
			@Override
			protected Score newObject(Class<?> clazz) {
				return new Score();
			}
		};
		
		Score theScore = scoreHolder.create("score1");
		theScore.addNote(0.0, new Note(ScaleNote.C, 2, (byte)127, 1.0));
		theScore.addNote(0.0, new Note(ScaleNote.G, 1, (byte)127, 1.0));
		
		theScore.addNote(1.0, new Note(ScaleNote.D, 2, (byte)127, 1.0));
		theScore.addNote(1.0, new Note(ScaleNote.A, 1, (byte)127, 1.0));
		
		theScore.addNote(2.0, new Note(ScaleNote.E, 2, (byte)127, 1.0));
		theScore.addNote(2.0, new Note(ScaleNote.B, 1, (byte)127, 1.0));
		
		theScore.addNote(3.0, new Note(ScaleNote.F, 2, (byte)127, 1.0));
		theScore.addNote(3.0, new Note(ScaleNote.C, 2, (byte)127, 1.0));
		
		theScore.addNote(0.0, new Note(ScaleNote.C, 1, (byte)127, 4.0));
		theScore.addNote(0.0, new Note(ScaleNote.E, 1, (byte)127, 4.0));
		theScore.addNote(0.0, new Note(ScaleNote.G, 1, (byte)127, 4.0));
		
		theScore.addNote(4.0, new Note(ScaleNote.G, 2, (byte)127, 1.0));
		theScore.addNote(4.0, new Note(ScaleNote.D, 2, (byte)127, 1.0));
		
		theScore.addNote(5.0, new Note(ScaleNote.A, 2, (byte)127, 1.0));
		theScore.addNote(5.0, new Note(ScaleNote.E, 2, (byte)127, 1.0));
		
		theScore.addNote(6.0, new Note(ScaleNote.B, 2, (byte)127, 1.0));
		theScore.addNote(6.0, new Note(ScaleNote.E, 2, (byte)127, 1.0));
		
		theScore.addNote(7.0, new Note(ScaleNote.C, 3, (byte)127, 1.0));
		theScore.addNote(7.0, new Note(ScaleNote.G, 2, (byte)127, 1.0));
		
		theScore.addNote(4.0, new Note(ScaleNote.B, 0, (byte)127, 3.0));
		theScore.addNote(4.0, new Note(ScaleNote.D, 1, (byte)127, 3.0));
		theScore.addNote(4.0, new Note(ScaleNote.G, 1, (byte)127, 3.0));
		
		theScore.addNote(7.0, new Note(ScaleNote.C, 1, (byte)127, 1.0));
		theScore.addNote(7.0, new Note(ScaleNote.E, 1, (byte)127, 1.0));
		theScore.addNote(7.0, new Note(ScaleNote.G, 1, (byte)127, 1.0));
		
		theScore.addNote(9.0, new Note(ScaleNote.G, 2, (byte)127, 1.0));
		theScore.addNote(9.0, new Note(ScaleNote.D, 2, (byte)127, 1.0));
		
		theScore.addNote(9.0, new Note(ScaleNote.B, 0, (byte)127, 1.0));
		theScore.addNote(9.0, new Note(ScaleNote.D, 1, (byte)127, 1.0));
		theScore.addNote(9.0, new Note(ScaleNote.G, 1, (byte)127, 1.0));
		
		theScore.addNote(11.0, new Note(ScaleNote.C, 3, (byte)127, 2.0));
		theScore.addNote(11.0, new Note(ScaleNote.C, 2, (byte)127, 2.0));
		
		theScore.addNote(11.0, new Note(ScaleNote.C, 1, (byte)127, 2.0));
		theScore.addNote(11.0, new Note(ScaleNote.E, 1, (byte)127, 2.0));
		theScore.addNote(11.0, new Note(ScaleNote.G, 1, (byte)127, 2.0));
		
		ScoreClip theClip = new ScoreClip(scoreHolder); 
		theClip.setScore("score1");
		
		final Channel theChannel = new MidiChannel(scoreHolder);
		KeywordEntry<Double, ScoreClip> theClipEntry
			= new KeywordArray.DefaultKeywordEntry<Double, ScoreClip>(0.0, theClip);
		theChannel.getClips(ScoreClip.class).add(theClipEntry);
		((MidiChannel)theChannel).setPlugin(plugin);
		//((MidiChannel)theChannel).setMuted(true);
	
		DataflowController flowController = new DataflowController(((MidiChannel)theChannel).sourceNode);
		((MidiChannel)theChannel).drainNode.addOutputNode(flowController);
		
		MusicFacade musicDecoy = new MusicDecoy()
		{
			float bpm = 120.f;
			float currentTick = 0.0f;
			
			float sampleRate;
			int samplesPerFrame;
			float increment;
			
			public void reset(Environment reset)
			{
				sampleRate = reset.sampleRate;
				samplesPerFrame = reset.samplesPerFrame;
				increment = samplesPerFrame / sampleRate * (bpm / 60);
			}
			
			public void tick()
			{
				theChannel.doTick(currentTick, currentTick + increment, samplesPerFrame);
				currentTick = currentTick + increment;
			}
		};
		
		OutputFacade outputFacade = new OutputController();
	
		Resonance res = new Resonance(outputFacade, flowController, musicDecoy);
		
		res.setEnvironment(new Environment(44100.0f, 2, new Encoding(Encoding.BITDEPTH_BIT32 | Encoding.WORDTYPE_INT | Encoding.ENDIAN_BIG), 128, 16), new MixerDevice(AudioSystem.getMixerInfo()[0]));
		res.play();
	}
}
