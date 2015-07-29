package net.aegistudio.resonance;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

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
	Collection<KeywordEntry<Double, T>> removalSet;
	
	public LengthKeywordArray(KeywordArray<Double, T> innerArray)
	{
		this.innerArray = innerArray;
		this.monitorSet = new HashSet<KeywordEntry<Double, T>>();
		this.endContainer = new HashSet<KeywordEntry<Double, T>>();
		this.removalSet = new HashSet<KeywordEntry<Double, T>>();
	}
	
	Collection<KeywordEntry<Double, T>> beginContainer;
	
	public synchronized void betweenTrigger(double begin, double end)
	{
		this.beginContainer = innerArray.between(begin, end);
	
		endContainer.clear();
		
		for(KeywordEntry<Double, T> entry : beginContainer)
			if(monitorSet.contains(entry))
				endContainer.add(entry);
		
		monitorSet.addAll(beginContainer);
		
		Iterator<KeywordEntry<Double, T>> iterator = monitorSet.iterator();
		while(iterator.hasNext())
		{
			KeywordEntry<Double, T> entry = iterator.next();
			if(entry.getKeyword() + entry.getValue().getLength() < end 
					|| entry.getKeyword() >= end)
			{
				endContainer.add(entry);
				iterator.remove();
			}
		}
		
		for(KeywordEntry<Double, T> entry : removalSet)
			if(monitorSet.contains(entry))
			{
				monitorSet.remove(entry);
				endContainer.add(entry);
			}
		removalSet.clear();
	}
	
	Collection<KeywordEntry<Double, T>> endContainer;
	
	public synchronized Collection<KeywordEntry<Double, T>> begin()
	{
		return this.beginContainer;
	}
	
	public synchronized Collection<KeywordEntry<Double, T>> activated()
	{
		return this.monitorSet;
	}
	
	public synchronized Collection<KeywordEntry<Double, T>> end()
	{
		return this.endContainer;
	}

	double maximum = 0.0;
	@Override
	public synchronized void add(net.aegistudio.resonance.KeywordArray.KeywordEntry<Double, T> entry) {
		innerArray.add(entry);
		recalculateMaximum();
	}
	
	protected void recalculateMaximum()
	{
		maximum = 0;
		for(KeywordEntry<Double, T> calc : all())
			maximum = Math.max(calc.getKeyword() + calc.getValue().getLength(), maximum);
	}

	@Override
	public synchronized void remove(net.aegistudio.resonance.KeywordArray.KeywordEntry<Double, T> entry) {
		innerArray.remove(entry);
		removalSet.add(entry);
		recalculateMaximum();
	}

	@Override
	public synchronized Collection<net.aegistudio.resonance.KeywordArray.KeywordEntry<Double, T>> between(Double lowerBound,
			Double upperBound) {
		this.betweenTrigger(lowerBound, upperBound);
		return this.begin();
	}

	public void reset(){
		this.monitorSet.clear();
		this.endContainer.clear();
	}
	
	@Override
	public Collection<net.aegistudio.resonance.KeywordArray.KeywordEntry<Double, T>> all() {
		return this.innerArray.all();
	}
	
	public double getMaximun(){
		return maximum;
	}
}
