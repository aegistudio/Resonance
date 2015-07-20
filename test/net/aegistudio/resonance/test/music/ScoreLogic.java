package net.aegistudio.resonance.test.music;

import net.aegistudio.resonance.Frame;
import net.aegistudio.resonance.KeywordArray;
import net.aegistudio.resonance.KeywordArray.KeywordEntry;
import net.aegistudio.resonance.NamedHolder;
import net.aegistudio.resonance.channel.Channel;
import net.aegistudio.resonance.channel.MidiChannel;
import net.aegistudio.resonance.channel.Note;
import net.aegistudio.resonance.channel.Score;
import net.aegistudio.resonance.channel.ScoreClip;
import net.aegistudio.resonance.plugin.Event;
import net.aegistudio.resonance.plugin.Plugin;
import net.aegistudio.resonance.serial.Structure;

public class ScoreLogic
{	
	public static void main(String[] arguments)
	{
		NamedHolder<Score> scoreHolder = new NamedHolder<Score>("score", false)
		{
			@Override
			protected Score newObject(Class<?> clazz) {
				return new Score();
			}
		};
		
		Score theScore = scoreHolder.create("score1");
		theScore.addNote(0.0, new Note((byte)0, (byte)127, 5.0));
		theScore.addNote(1.0, new Note((byte)1, (byte)127, 3.0));
		theScore.addNote(2.0, new Note((byte)2, (byte)127, 2.0));
		theScore.addNote(3.0, new Note((byte)3, (byte)127, 1.0));
		
		ScoreClip theClip = new ScoreClip(scoreHolder); 
		theClip.setScore("score1");
		theClip.trim(4, 0.5);
		
		Channel theChannel = new MidiChannel(scoreHolder);
		KeywordEntry<Double, ScoreClip> theClipEntry
			= new KeywordArray.DefaultKeywordEntry<Double, ScoreClip>(0.0, theClip);
		theChannel.getClips(ScoreClip.class).add(theClipEntry);
		
		((MidiChannel)theChannel).setPlugin(new Plugin()
		{

			@Override
			public void create(Structure parameter) {		}

			@Override
			public void trigger(Event event) {
				System.out.println(event);
			}

			@Override
			public void process(Frame input, Frame output) {			}

			@Override
			public void destroy() {				}
			
		});
		
		float currentPosition = 0.0f;
		while(currentPosition < 5.0f)
		{
			System.out.printf("%.1f\n", currentPosition);
			float newPosition = currentPosition + 0.1f;
			theChannel.doTick(currentPosition, newPosition, 128);
			currentPosition = newPosition;
		}
	}
}
