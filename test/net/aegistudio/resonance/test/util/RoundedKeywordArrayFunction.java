package net.aegistudio.resonance.test.util;

import java.util.Arrays;
import java.util.Collection;

import net.aegistudio.resonance.KeywordArray;
import net.aegistudio.resonance.KeywordArray.*;
import net.aegistudio.resonance.RoundedKeywordArray;

public class RoundedKeywordArrayFunction
{
	public static void main(String[] arguments)
	{
		Collection<KeywordEntry<Double, String>> buffer;
		KeywordArray<Double, String> set = new RoundedKeywordArray<String>();
		
		KeywordEntry<Double, String> entryHaha = new DefaultKeywordEntry<Double, String>(1.02, "Haha");
		set.add(entryHaha);
		
		String bufferedString;
		
		buffer = set.between(1.00, 1.02);
		if(!(bufferedString = Arrays.deepToString(buffer.toArray(new KeywordEntry[0]))).equals("[]"))
			throw new AssertionError("between(1.00, 1.02) failed: " + bufferedString);
		
		buffer = set.between(1.00, 1.03);
		if(!(bufferedString = Arrays.deepToString(buffer.toArray(new KeywordEntry[0])))
				.equals("[" + entryHaha + "]"))
			throw new AssertionError("between(1.00, 1.03) failed: " + bufferedString);
		
		buffer = set.between(1.00, 3.00);
		if(!(bufferedString = Arrays.deepToString(buffer.toArray(new KeywordEntry[0])))
				.equals("[" + entryHaha + "]"))
			throw new AssertionError("between(1.00, 3.00) failed: " + bufferedString);
		
		buffer = set.between(2.00, 3.00);
		if(!(bufferedString = Arrays.deepToString(buffer.toArray(new KeywordEntry[0])))
				.equals("[]"))
			throw new AssertionError("between(2.00, 3.00) failed: " + bufferedString);
		
		KeywordEntry<Double, String> entryHehe = new DefaultKeywordEntry<Double, String>(2.03, "Hehe");
		set.add(entryHehe);
		
		buffer = set.between(2.00, 3.00);
		if(!(bufferedString = Arrays.deepToString(buffer.toArray(new KeywordEntry[0])))
				.equals("[" + entryHehe + "]"))
			throw new AssertionError("between(2.00, 3.00) after adding <2.03, Hehe> failed: " + bufferedString);
		
		buffer = set.between(1.00, 3.00);
		if(!(bufferedString = Arrays.deepToString(buffer.toArray(new KeywordEntry[0])))
				.equals("[" + entryHaha + ", " + entryHehe + "]"))
			throw new AssertionError("between(1.00, 3.00) after clear failed: " + bufferedString);
		
		set.remove(entryHehe);
		buffer = set.between(1.00, 3.00);
		if(!(bufferedString = Arrays.deepToString(buffer.toArray(new KeywordEntry[0])))
				.equals("[" + entryHaha + "]"))
			throw new AssertionError("between(1.00, 3.00) after removing <2.03, Hehe> failed: " + bufferedString);
		
	}
}
