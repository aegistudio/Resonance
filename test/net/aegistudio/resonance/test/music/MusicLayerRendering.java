package net.aegistudio.resonance.test.music;

import javax.sound.sampled.AudioSystem;

import net.aegistudio.resonance.Encoding;
import net.aegistudio.resonance.Environment;
import net.aegistudio.resonance.KeywordArray;
import net.aegistudio.resonance.Resonance;
import net.aegistudio.resonance.KeywordArray.KeywordEntry;
import net.aegistudio.resonance.channel.Channel;
import net.aegistudio.resonance.channel.MidiChannel;
import net.aegistudio.resonance.channel.Note;
import net.aegistudio.resonance.channel.Score;
import net.aegistudio.resonance.channel.ScoreClip;
import net.aegistudio.resonance.device.MixerDevice;
import net.aegistudio.resonance.io.OutputController;
import net.aegistudio.resonance.io.OutputFacade;
import net.aegistudio.resonance.music.MusicController;
import net.aegistudio.resonance.music.ScaleNote;
import net.aegistudio.resonance.plugin.Plugin;
import net.aegistudio.resonance.test.util.SineOscillator;

public class MusicLayerRendering {
	public static void main(String[] arguments)
	{	
		MusicController musicLayer = new MusicController();
		
		Plugin plugin = new SineOscillator();
		
		Score theScore = musicLayer.scoreHolder.create("score1");
		
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
		theScore.addNote(9.0, new Note(ScaleNote.F, 1, (byte)127, 1.0));
		
		theScore.addNote(11.0, new Note(ScaleNote.C, 3, (byte)127, 2.0));
		theScore.addNote(11.0, new Note(ScaleNote.C, 2, (byte)127, 2.0));
		
		theScore.addNote(11.0, new Note(ScaleNote.C, 1, (byte)127, 2.0));
		theScore.addNote(11.0, new Note(ScaleNote.E, 1, (byte)127, 2.0));
		theScore.addNote(11.0, new Note(ScaleNote.G, 1, (byte)127, 2.0));
		
		ScoreClip theClip = new ScoreClip(musicLayer.scoreHolder); 
		theClip.setScore("score1");
		theClip.trim(4.0, 0);
		
		ScoreClip theClip2 = new ScoreClip(musicLayer.scoreHolder); 
		theClip2.setScore("score1");
		
		final Channel theChannel = musicLayer.channelHolder.create("channel1", MidiChannel.class);
		KeywordEntry<Double, ScoreClip> theClipEntry
			= new KeywordArray.DefaultKeywordEntry<Double, ScoreClip>(0.0, theClip);
		theChannel.getClips(ScoreClip.class).add(theClipEntry);
		
		KeywordEntry<Double, ScoreClip> theClipEntry2
			= new KeywordArray.DefaultKeywordEntry<Double, ScoreClip>(4.0, theClip2);
		theChannel.getClips(ScoreClip.class).add(theClipEntry2);
		
		((MidiChannel)theChannel).setPlugin(plugin);
		//((MidiChannel)theChannel).setMuted(true);
	
		OutputFacade outputFacade = new OutputController();
	
		Resonance res = new Resonance(outputFacade, musicLayer.dataflowController, musicLayer);
		
		res.setEnvironment(new Environment(44100.0f, 2, new Encoding(Encoding.BITDEPTH_BIT32 | Encoding.WORDTYPE_INT | Encoding.ENDIAN_BIG), 128, 16), new MixerDevice(AudioSystem.getMixerInfo()[0]));
		res.play();
	}
}
