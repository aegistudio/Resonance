package net.aegistudio.resonance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RoundedKeywordArray<T> implements KeywordArray<Double, T>
{
	private final Map<Long, List<KeywordEntry<Double, T>>> entries;
	
	public RoundedKeywordArray()
	{
		entries = new TreeMap<Long, List<KeywordEntry<Double, T>>>();
	}

	@Override
	public void add(KeywordEntry<Double, T> adding) {
		long keyword = (long)((double)adding.getKeyword());
		List<KeywordEntry<Double, T>> entry;
		if((entry = entries.get(keyword)) == null)
			entries.put(keyword, entry = new ArrayList<KeywordEntry<Double, T>>());
		entry.add(adding);
		synchronized(this.updated){	updated.clear();	}
	}

	@Override
	public void remove(KeywordEntry<Double, T> removing) {
		long keyword = (long)((double)removing.getKeyword());
		List<KeywordEntry<Double, T>> entry;
		if((entry = entries.get(keyword)) != null)
			entry.remove(removing);
		synchronized(this.updated){	updated.clear();	}
	}

	@Override
	public Collection<KeywordEntry<Double, T>> between(Double lowerBound, Double upperBound)
	{
		Collection<KeywordEntry<Double, T>> buffer = new ArrayList<KeywordEntry<Double, T>>();
		long lowerValue = (long)(double)lowerBound;
		long upperValue = (long)(double)upperBound;
		for(long pointer = lowerValue; pointer <= upperValue; pointer ++)
		{
			List<KeywordEntry<Double, T>> entry = entries.get(pointer);
			if(entry == null) continue;
			if(pointer == lowerValue || pointer == upperValue)
			{
				for(KeywordEntry<Double, T> element : entry)
					if(element.getKeyword() >= lowerBound && element.getKeyword() < upperBound)
						buffer.add(element);
			}
			else buffer.addAll(entry);
		}
		return buffer;
	}

	@Override
	public Collection<KeywordEntry<Double, T>> all() {

		Collection<KeywordEntry<Double, T>> buffer = new ArrayList<KeywordEntry<Double, T>>();
		for(List<KeywordEntry<Double, T>> entry : this.entries.values())
			if(entry != null) buffer.addAll(entry);
		return buffer;
	}

	protected final HashSet<Object> updated = new HashSet<Object>();
	
	@Override
	public boolean hasUpdated(Object questioner) {
		synchronized(this.updated)
		{
			if(!updated.contains(questioner))
			{
				updated.add(questioner);
				return true;
			}
			else return false;
		}
	}
	
	
}
