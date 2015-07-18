package net.aegistudio.resonance.channel;

import java.util.HashSet;
import java.util.TreeMap;

import net.aegistudio.resonance.serial.SerializedObject;
import net.aegistudio.resonance.serial.Structure;

/**
 * This class is held by music layer and is utilized for keeping score data
 * @author aegistudio
 *
 */

public class ScoreHolder implements SerializedObject
{
	TreeMap<String, Score> scoreHolder = new TreeMap<String, Score>();
	
	public synchronized Score createScore(String scoreName)
	{
		if(scoreHolder.get(scoreName) != null)
			throw new IllegalArgumentException("The score named " + scoreName + " already exists!");
		Score returnValue = new Score();
		scoreHolder.put(scoreName, returnValue);
		updated.clear();
		return returnValue;
	}
	
	public synchronized void removeScore(String scoreName)
	{
		if(scoreHolder.get(scoreName) == null)
			throw new IllegalArgumentException("The score named" + scoreName + " doesn't exist.");
		scoreHolder.remove(scoreName);
		updated.clear();
	}
	
	public synchronized void renameScore(String scoreName, String newScoreName)
	{
		Score oldScore;
		if((oldScore = scoreHolder.get(scoreName)) == null)
			throw new IllegalArgumentException("The score named" + scoreName + " doesn't exist.");
		if(scoreHolder.get(newScoreName) != null)
			throw new IllegalArgumentException("The score named " + newScoreName + " already exists!");
		scoreHolder.remove(scoreName);
		scoreHolder.put(newScoreName, oldScore);
		updated.clear();
	}
	
	public synchronized Score getScore(String scoreName)
	{
		if(scoreName == null) return null;
		return scoreHolder.get(scoreName);
	}
	
	HashSet<Object> updated = new HashSet<Object>();
	
	public synchronized boolean hasUpdated(Object asker)
	{
		if(this.updated.contains(asker))
			return false;
		else
		{
			this.updated.add(asker);
			return true;
		}
	}
	
	@Override
	public void load(Structure input)
	{
		
	}

	@Override
	public void save(Structure output)
	{
		
	}

}
