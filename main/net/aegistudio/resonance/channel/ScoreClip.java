package net.aegistudio.resonance.channel;

import java.util.ArrayList;

import net.aegistudio.resonance.KeywordArray.KeywordEntry;
import net.aegistudio.resonance.LengthKeywordArray;
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
	protected double innerOffset = 0.0;
	
	public ScoreClip(ScoreHolder scoreHolder)
	{
		this.scoreHolder = scoreHolder;
	}
	
	public void setScore(String scoreName)
	{
		this.scoreName = scoreName;
		this.clipLength = -1;
		this.innerOffset = 0.0;
	}
	
	public void trim(double clipLength, double innerOffset)
	{
		this.clipLength = clipLength;
		this.innerOffset = innerOffset;
	}
	
	@Override
	public void load(Structure input) {
		scoreName = input.get("score", Type.STRING, null);
		clipLength = input.get("clip", Type.DOUBLE, -1.0);
		innerOffset = input.get("offset", Type.DOUBLE, 0.0);
	}

	@Override
	public void save(Structure output) {
		output.set("score", Type.STRING, scoreName);
		output.set("clip", Type.DOUBLE, clipLength);	
		output.set("offset", Type.DOUBLE, innerOffset);
	}

	Score theScore;
	LengthKeywordArray<Note> scoreMonitor;
	
	protected void checkUpdated()
	{
		if(this.scoreHolder.hasUpdated(this))
		{
			this.theScore = this.scoreHolder.getScore(scoreName);
			if(this.theScore != null)
				this.scoreMonitor = this.theScore.getLengthMonitor();
		}
	}
	
	@Override
	public Event[] getEvents(double begin, double end, int samplesPerFrame) {
		this.checkUpdated();
		if(this.theScore == null)
			return new Event[0];
		else
		{
			begin = begin + innerOffset;
			end = end + innerOffset;
			
			double factor = samplesPerFrame / (end - begin);
			ArrayList<Event> eventReturnValue = new ArrayList<Event>();
			
			this.scoreMonitor.betweenTrigger(begin, end);
			
			for(KeywordEntry<Double, Note> beginNote : this.scoreMonitor.begin())
				eventReturnValue.add(new NoteOnEvent(beginNote.getValue().pitch, beginNote.getValue().velocity,
						(int)((beginNote.getKeyword() - begin) * factor)));
			
			for(KeywordEntry<Double, Note> endNote : this.scoreMonitor.end())
				eventReturnValue.add(new NoteOffEvent(endNote.getValue().pitch,
						(int)((endNote.getKeyword() + endNote.getValue().getLength() - begin) * factor)));
			
			return eventReturnValue.toArray(new Event[0]);
		}
	}

	@Override
	public double getLength() {
		this.checkUpdated();
		if(clipLength <= 0)		//Non Positive Means 'Length Depends On Score'
			if(this.scoreHolder.getScore(scoreName) != null)
				return this.theScore.getScoreLength();
			else return 0.0;
		else return this.clipLength;
	}

}