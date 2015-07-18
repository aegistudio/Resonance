package net.aegistudio.resonance;

import java.util.Collection;

/**
 * Caution: Duplication in key is allowed. So we need K, V as a entry to identify it.
 * @author aegistudio
 *
 * @param <K>
 * @param <V>
 */

public interface KeywordArray<K extends Comparable<K>, V>
{
	public class DefaultKeywordEntry<K extends Comparable<K>, V> implements KeywordEntry<K, V>
	{
		public final K keyword;
		public final V value;
		public DefaultKeywordEntry(K keyword, V value)
		{
			this.keyword = keyword;
			this.value = value;
		}
		
		public K getKeyword()
		{
			return this.keyword;
		}
		
		public V getValue()
		{
			return this.value;
		}
	}
	
	public interface KeywordEntry<K extends Comparable<K>, V>
	{
		public K getKeyword();
		public V getValue();
	}
	
	public void add(KeywordEntry<K, V> entry);
	
	public void remove(KeywordEntry<K, V> entry);

	public Collection<KeywordEntry<K, V>> between(K lowerBound, K upperBound);
	
	public Collection<KeywordEntry<K, V>> all();
}
