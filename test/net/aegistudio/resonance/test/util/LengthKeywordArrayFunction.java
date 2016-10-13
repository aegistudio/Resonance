package net.aegistudio.resonance.test.util;

import java.util.Arrays;
import java.util.Collection;

import net.aegistudio.resonance.music.KeywordArray;
import net.aegistudio.resonance.music.LengthKeywordArray;
import net.aegistudio.resonance.music.LengthObject;
import net.aegistudio.resonance.music.RoundedKeywordArray;
import net.aegistudio.resonance.music.KeywordArray.DefaultKeywordEntry;
import net.aegistudio.resonance.music.KeywordArray.KeywordEntry;

public class LengthKeywordArrayFunction {
	
	public static void main(String[] arguments)
	{
		Collection<KeywordEntry<Double, DefaultLengthObject<String>>> buffer;
		String result;
		KeywordArray<Double, DefaultLengthObject<String>> innerSet = new RoundedKeywordArray<DefaultLengthObject<String>>();
		LengthKeywordArray<DefaultLengthObject<String>> set = 
				new LengthKeywordArray<DefaultLengthObject<String>>(innerSet);
		innerSet.add(new DefaultKeywordEntry<Double, DefaultLengthObject<String>>(1.0, new DefaultLengthObject<String>(2.0, "first")));
		
		innerSet.add(new DefaultKeywordEntry<Double, DefaultLengthObject<String>>(1.5, new DefaultLengthObject<String>(1.0, "second")));
		
		buffer = set.all();
		if(!(result = Arrays.deepToString(buffer.toArray(new KeywordEntry[0])))
				.equals("[<1.0, 2.0, first>, <1.5, 1.0, second>]"))
			throw new AssertionError("Print all element case failed: " + result);
		
		set.betweenTrigger(1.0, 1.2);
		buffer = set.begin();
		if(!(result = Arrays.deepToString(buffer.toArray(new KeywordEntry[0])))
				.equals("[<1.0, 2.0, first>]"))
			throw new AssertionError("Begin between(1.0, 1.2) failed: " + result);
		buffer = set.end();
		if(!(result = Arrays.deepToString(buffer.toArray(new KeywordEntry[0])))
				.equals("[]"))
			throw new AssertionError("End between(1.0, 1.2) failed: " + result);
		
		set.betweenTrigger(1.2, 1.4);
		buffer = set.begin();
		if(!(result = Arrays.deepToString(buffer.toArray(new KeywordEntry[0])))
				.equals("[]"))
			throw new AssertionError("Begin between(1.2, 1.4) failed: " + result);
		buffer = set.end();
		if(!(result = Arrays.deepToString(buffer.toArray(new KeywordEntry[0])))
				.equals("[]"))
			throw new AssertionError("End between(1.2, 1.4) failed: " + result);
		
		set.betweenTrigger(1.4, 1.6);
		buffer = set.begin();
		if(!(result = Arrays.deepToString(buffer.toArray(new KeywordEntry[0])))
				.equals("[<1.5, 1.0, second>]"))
			throw new AssertionError("Begin between(1.4, 1.6) failed: " + result);
		buffer = set.end();
		if(!(result = Arrays.deepToString(buffer.toArray(new KeywordEntry[0])))
				.equals("[]"))
			throw new AssertionError("End between(1.4, 1.6) failed: " + result);
		
		set.betweenTrigger(2.4, 2.6);
		buffer = set.begin();
		if(!(result = Arrays.deepToString(buffer.toArray(new KeywordEntry[0])))
				.equals("[]"))
			throw new AssertionError("Begin between(2.4, 2.6) failed: " + result);
		buffer = set.end();
		if(!(result = Arrays.deepToString(buffer.toArray(new KeywordEntry[0])))
				.equals("[<1.5, 1.0, second>]"))
			throw new AssertionError("End between(2.4, 2.6) failed: " + result);
		
		/** Test whether the removed item will be monitored again! **/
		set.betweenTrigger(2.4, 2.6);
		buffer = set.begin();
		if(!(result = Arrays.deepToString(buffer.toArray(new KeywordEntry[0])))
				.equals("[]"))
			throw new AssertionError("Begin between(2.4, 2.6) failed: " + result);
		buffer = set.end();
		if(!(result = Arrays.deepToString(buffer.toArray(new KeywordEntry[0])))
				.equals("[]"))
			throw new AssertionError("End between(2.4, 2.6) failed: " + result);
		
		/** The begin item should be ended before begin again. **/
		set.betweenTrigger(1.0, 1.2);
		buffer = set.begin();
		if(!(result = Arrays.deepToString(buffer.toArray(new KeywordEntry[0])))
				.equals("[<1.0, 2.0, first>]"))
			throw new AssertionError("Begin between(1.0, 1.2) failed: " + result);
		buffer = set.end();
		if(!(result = Arrays.deepToString(buffer.toArray(new KeywordEntry[0])))
				.equals("[<1.0, 2.0, first>]"))
			throw new AssertionError("End between(1.0, 1.2) failed: " + result);
	}
}

class DefaultLengthObject<T> implements LengthObject
{
	double length;
	T value;
	public DefaultLengthObject(double length, T value) {
		this.value = value;
		this.length = length;
	}

	@Override
	public double getLength() {
		return this.length;
	}
	
	
	public String toString()
	{
		return this.length + ", " + value.toString();
	}
}
