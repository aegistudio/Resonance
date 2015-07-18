package net.aegistudio.resonance.channel;

import java.util.ArrayList;
import java.util.Collection;

import net.aegistudio.resonance.KeywordArray.KeywordEntry;
import net.aegistudio.resonance.music.NoteOffEvent;
import net.aegistudio.resonance.music.NoteOnEvent;
import net.aegistudio.resonance.plugin.Event;
import net.aegistudio.resonance.serial.Structure;
import net.aegistudio.resonance.serial.Type;

public class ScoreClip implements Clip
{
	protected final ScoreHolder scoreHolder;
	protected String scoreName;
	protected double clipLength = -1;
	
	public ScoreClip(ScoreHolder scoreHolder)
	{
		this.scoreHolder = scoreHolder;
	}
	
	public void setScore(String scoreName)
	{
		this.scoreName = scoreName;
		this.clipLength = -1;
	}
	
	@Override
	public void load(Structure input) {
		input.get("score", Type.STRING, null);
		input.get("clip", Type.DOUBLE, -1.0);
	}

	@Override
	public void save(Structure output) {
		output.set("score", Type.STRING, scoreName);
		output.set("clip", Type.DOUBLE, clipLength);	
	}

	Score theScore;
	
	protected void checkUpdated()
	{
		if(this.scoreHolder.hasUpdated(this))
			this.theScore = this.scoreHolder.getScore(scoreName);
	}
	
	@Override
	public double getClipLength() {
		this.checkUpdated();
		if(clipLength <= 0)		//Non Positive Means 'Length Depends On Score'
			if(this.scoreHolder.getScore(scoreName) != null)
				return this.theScore.getScoreLength();
			else return 0.0;
		else return this.clipLength;
	}
	
	Collection<KeywordEntry<Double, Note>> beginNotes = new ArrayList<KeywordEntry<Double, Note>>();
	
	@Override
	public Event[] getEvents(double begin, double end, int samplesPerFrame) {
		this.checkUpdated();
		if(this.theScore == null)
			return new Event[0];
		else
		{
			double factor = samplesPerFrame / (end - begin);
			ArrayList<Event> eventReturnValue = new ArrayList<Event>();
			Collection<KeywordEntry<Double, Note>> beginNotes = this.theScore.getBeginNotes(begin, end);
			this.beginNotes.addAll(beginNotes);
			for(KeywordEntry<Double, Note> beginNote : beginNotes)
				eventReturnValue.add(new NoteOnEvent(beginNote.getValue().pitch, beginNote.getValue().velocity,
						(int)((beginNote.getKeyword() - begin) * factor)));
			
			Collection<KeywordEntry<Double, Note>> endNotes = this.theScore.getEndNotes(begin, end, this.beginNotes);
			for(KeywordEntry<Double, Note> endNote : endNotes)
				eventReturnValue.add(new NoteOffEvent(endNote.getValue().pitch,
						(int)((endNote.getKeyword() - begin) * factor)));
			
			return eventReturnValue.toArray(new Event[0]);
		}
	}

}
