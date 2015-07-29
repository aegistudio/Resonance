package net.aegistudio.resonance.channel;

import java.util.ArrayList;

import net.aegistudio.resonance.KeywordArray.KeywordEntry;
import net.aegistudio.resonance.LengthKeywordArray;
import net.aegistudio.resonance.NamedHolder;
import net.aegistudio.resonance.music.NoteOffEvent;
import net.aegistudio.resonance.music.NoteOnEvent;
import net.aegistudio.resonance.plugin.Event;
import net.aegistudio.resonance.serial.Structure;
import net.aegistudio.resonance.serial.Type;

public class ScoreClip implements Clip
{
	protected final NamedHolder<Score> scoreHolder;
	
	protected KeywordEntry<String, Score> scoreEntry;
	
	protected double clipLength = -1;
	protected double innerOffset = 0.0;
	
	public ScoreClip(NamedHolder<Score> scoreHolder)
	{
		this.scoreHolder = scoreHolder;
	}
	
	public void setScore(String scoreName)
	{
		if(scoreName == null) return;
		KeywordEntry<String, Score> entry = scoreHolder.getEntry(scoreName);
		if(entry != scoreEntry)
		{
			scoreEntry = entry;
			if(scoreEntry != null)
			{
				this.clipLength = -1;
				this.innerOffset = 0.0;
				scoreMonitor = scoreEntry.getValue().getLengthMonitor();
			}
			else scoreMonitor = null;
		}
	}
	
	public KeywordEntry<String, Score> getScore()
	{
		return this.scoreEntry;
	}
	
	public ScoreClip clone()
	{
		ScoreClip scoreClip = new ScoreClip(this.scoreHolder);
		scoreClip.setScore(scoreEntry.getKeyword());
		scoreClip.trim(clipLength, innerOffset);
		return scoreClip;
	}
	
	public void trim(double clipLength, double innerOffset)
	{
		this.clipLength = clipLength;
		this.innerOffset = innerOffset;
	}
	
	@Override
	public void load(Structure input) {
		setScore(input.get("score", Type.STRING, null));
		clipLength = input.get("clip", Type.DOUBLE, -1.0);
		innerOffset = input.get("offset", Type.DOUBLE, 0.0);
	}

	@Override
	public void save(Structure output) {
		output.set("score", Type.STRING, scoreEntry.getKeyword());
		output.set("clip", Type.DOUBLE, clipLength);	
		output.set("offset", Type.DOUBLE, innerOffset);
	}

	LengthKeywordArray<Note> scoreMonitor;
	
	@Override
	public Event[] getEvents(double begin, double end, int samplesPerFrame) {
		if(scoreEntry == null || scoreEntry.getKeyword() == null)
		{
			scoreEntry = null;
			return new Event[0];
		}
		else
		{
			begin = begin - innerOffset;
			end = end - innerOffset;
			
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
		if(clipLength <= 0)
		{
			//Non Positive Means 'Length Depends On Score'
			double length = 0.0;
			if(this.scoreEntry != null)
				length = this.scoreEntry.getValue().getScoreLength();
			return Math.max(length, 1.0);
		}
		else return this.clipLength;
	}

	public double getOffset()
	{
		return this.innerOffset;
	}
	
	@Override
	public Event[] offload(int samplesPerFrame) {
		ArrayList<Event> eventReturnValue = new ArrayList<Event>();
		for(KeywordEntry<Double, Note> endNote : this.scoreMonitor.activated())
			eventReturnValue.add(new NoteOffEvent(endNote.getValue().pitch, samplesPerFrame - 1));

		return eventReturnValue.toArray(new Event[0]);
	}

	@Override
	public void reset() {
		if(this.scoreMonitor != null)
			this.scoreMonitor.reset();
	}

}
