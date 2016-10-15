package net.aegistudio.resonance.music;

import javax.sound.sampled.AudioSystem;

import net.aegistudio.resonance.Resonance;
import net.aegistudio.resonance.common.Encoding;
import net.aegistudio.resonance.common.Environment;
import net.aegistudio.resonance.common.OutputFacade;
import net.aegistudio.resonance.device.MixerDevice;
import net.aegistudio.resonance.format.OutputController;
import net.aegistudio.resonance.midi.ScaleNote;
import net.aegistudio.resonance.music.KeywordArray;
import net.aegistudio.resonance.music.MusicController;
import net.aegistudio.resonance.music.KeywordArray.KeywordEntry;
import net.aegistudio.resonance.music.channel.Channel;
import net.aegistudio.resonance.music.channel.MidiChannel;
import net.aegistudio.resonance.music.channel.Note;
import net.aegistudio.resonance.music.channel.Score;
import net.aegistudio.resonance.music.channel.ScoreClip;
import net.aegistudio.resonance.plugin.Plugin;
import net.aegistudio.resonance.SineOscillator;

public class MusicLayerRendering {
	public static void test(int offset, Plugin plugin) {
		MusicController musicLayer = new MusicController();
		
		Score theScore = musicLayer.scoreHolder.create("score1");
		
		theScore.addNote(0.0, new Note(ScaleNote.C, 2 + offset, (byte)127, 1.0));
		theScore.addNote(0.0, new Note(ScaleNote.G, 1 + offset, (byte)127, 1.0));
		
		theScore.addNote(1.0, new Note(ScaleNote.D, 2 + offset, (byte)127, 1.0));
		theScore.addNote(1.0, new Note(ScaleNote.A, 1 + offset, (byte)127, 1.0));
		
		theScore.addNote(2.0, new Note(ScaleNote.E, 2 + offset, (byte)127, 1.0));
		theScore.addNote(2.0, new Note(ScaleNote.B, 1 + offset, (byte)127, 1.0));
		
		theScore.addNote(3.0, new Note(ScaleNote.F, 2 + offset, (byte)127, 1.0));
		theScore.addNote(3.0, new Note(ScaleNote.C, 2 + offset, (byte)127, 1.0));
		
		theScore.addNote(0.0, new Note(ScaleNote.C, 1 + offset, (byte)127, 3.9));
		theScore.addNote(0.0, new Note(ScaleNote.E, 1 + offset, (byte)127, 3.9));
		theScore.addNote(0.0, new Note(ScaleNote.G, 1 + offset, (byte)127, 3.9));
		
		theScore.addNote(4.0, new Note(ScaleNote.G, 2 + offset, (byte)127, 1.0));
		theScore.addNote(4.0, new Note(ScaleNote.D, 2 + offset, (byte)127, 1.0));
		
		theScore.addNote(5.0, new Note(ScaleNote.A, 2 + offset, (byte)127, 1.0));
		theScore.addNote(5.0, new Note(ScaleNote.E, 2 + offset, (byte)127, 1.0));
		
		theScore.addNote(6.0, new Note(ScaleNote.B, 2 + offset, (byte)127, 1.0));
		theScore.addNote(6.0, new Note(ScaleNote.E, 2 + offset, (byte)127, 1.0));
		
		theScore.addNote(7.0, new Note(ScaleNote.C, 3 + offset, (byte)127, 1.0));
		theScore.addNote(7.0, new Note(ScaleNote.G, 2 + offset, (byte)127, 1.0));
		
		theScore.addNote(4.0, new Note(ScaleNote.B, 0 + offset, (byte)127, 3.0));
		theScore.addNote(4.0, new Note(ScaleNote.D, 1 + offset, (byte)127, 3.0));
		theScore.addNote(4.0, new Note(ScaleNote.G, 1 + offset, (byte)127, 3.0));
		
		theScore.addNote(7.0, new Note(ScaleNote.C, 1 + offset, (byte)127, 1.0));
		theScore.addNote(7.0, new Note(ScaleNote.E, 1 + offset, (byte)127, 1.0));
		theScore.addNote(7.0, new Note(ScaleNote.G, 1 + offset, (byte)127, 1.0));
		
		theScore.addNote(9.0, new Note(ScaleNote.G, 2 + offset, (byte)127, 1.0));
		theScore.addNote(9.0, new Note(ScaleNote.D, 2 + offset, (byte)127, 1.0));
		
		theScore.addNote(9.0, new Note(ScaleNote.B, 0 + offset, (byte)127, 1.0));
		theScore.addNote(9.0, new Note(ScaleNote.D, 1 + offset, (byte)127, 1.0));
		theScore.addNote(9.0, new Note(ScaleNote.F, 1 + offset, (byte)127, 1.0));
		
		theScore.addNote(11.0, new Note(ScaleNote.C, 3 + offset, (byte)127, 2.0));
		theScore.addNote(11.0, new Note(ScaleNote.C, 2 + offset, (byte)127, 2.0));
		
		theScore.addNote(11.0, new Note(ScaleNote.C, 1 + offset, (byte)127, 2.0));
		theScore.addNote(11.0, new Note(ScaleNote.E, 1 + offset, (byte)127, 2.0));
		theScore.addNote(11.0, new Note(ScaleNote.G, 1 + offset, (byte)127, 2.0));
		
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
		
		res.setEnvironment(new Environment(44100.0f, 2, new Encoding(Encoding.BITDEPTH_BIT32 
				| Encoding.WORDTYPE_INT | Encoding.ENDIAN_BIG), 128, 16), new MixerDevice(AudioSystem.getMixerInfo()[0]));
		res.play();
	}
	
	public static void main(String[] arguments) {	
		test(0, new SineOscillator());
	}
}
