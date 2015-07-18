package net.aegistudio.resonance;

import java.util.Collection;
import java.util.HashSet;

/**
 * A length object is denoted by [a, b]. <br>
 * the action after calling between(l, h) is defined as the following:<br>
 * 1. if l < a < h then the object will appear in the begin set, adding to monitor set. <br>
 * 2. if object already in the begin set, and b < h or h < a, then the object 
 * will appear in the end set, and removed from monitor set. <br>
 * 3. if object already in the begin set and l < a < h, and then it should also be
 * added to the end set.
 * @author aegistudio
 *
 * @param <T> the object should be a length object who has getLength():double method.
 */

public class LengthKeywordArray<T extends LengthObject> implements KeywordArray<Double, T>
{
	KeywordArray<Double, T> innerArray;
	Collection<KeywordEntry<Double, T>> monitorSet;
	
	public LengthKeywordArray(KeywordArray<Double, T> innerArray)
	{
		this.innerArray = innerArray;
		this.monitorSet = new HashSet<KeywordEntry<Double, T>>();
		this.endContainer = new HashSet<KeywordEntry<Double, T>>();
	}
	
	Collection<KeywordEntry<Double, T>> beginContainer;
	
	public void betweenTrigger(double begin, double end)
	{
		this.beginContainer = innerArray.between(begin, end);
	
		endContainer.clear();
		
		for(KeywordEntry<Double, T> entry : beginContainer)
			if(monitorSet.contains(entry))
				endContainer.add(entry);
				
		monitorSet.addAll(beginContainer);
		
		for(KeywordEntry<Double, T> entry : monitorSet)
		{
			if(entry.getKeyword() + entry.getValue().getLength() < end 
					|| entry.getKeyword() >= end)
			{
				endContainer.add(entry);
				monitorSet.remove(entry);
			}
		}
				
	}
	
	Collection<KeywordEntry<Double, T>> endContainer;
	
	public Collection<KeywordEntry<Double, T>> begin()
	{
		return this.beginContainer;
	}
	
	public Collection<KeywordEntry<Double, T>> activated()
	{
		return this.monitorSet;
	}
	
	public Collection<KeywordEntry<Double, T>> end()
	{
		return this.endContainer;
	}

	@Override
	public void add(net.aegistudio.resonance.KeywordArray.KeywordEntry<Double, T> entry) {
		innerArray.add(entry);
	}

	@Override
	public void remove(net.aegistudio.resonance.KeywordArray.KeywordEntry<Double, T> entry) {
		innerArray.remove(entry);
	}

	@Override
	public Collection<net.aegistudio.resonance.KeywordArray.KeywordEntry<Double, T>> between(Double lowerBound,
			Double upperBound) {
		this.betweenTrigger(lowerBound, upperBound);
		return this.begin();
	}

	@Override
	public Collection<net.aegistudio.resonance.KeywordArray.KeywordEntry<Double, T>> all() {
		return this.innerArray.all();
	}
}
