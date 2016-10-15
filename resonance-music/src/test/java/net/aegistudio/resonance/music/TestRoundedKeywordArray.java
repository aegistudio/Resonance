package net.aegistudio.resonance.music;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import net.aegistudio.resonance.music.KeywordArray;
import net.aegistudio.resonance.music.RoundedKeywordArray;
import net.aegistudio.resonance.music.KeywordArray.*;

public class TestRoundedKeywordArray {
	KeywordArray<Double, String> set;
	final KeywordEntry<Double, String> entryHaha = new DefaultKeywordEntry<>(1.02, "Haha");
	final KeywordEntry<Double, String> entryHehe = new DefaultKeywordEntry<>(2.03, "Hehe");
	
	@Before
	public void setup() {
		set = new RoundedKeywordArray<String>();
	}
	
	private void assertSet(Collection<?> collection, String toString) {
		assertEquals(Arrays.deepToString(collection.toArray()), toString);
	}
	
	@Test
	public void test() {
		set.add(entryHaha);
		assertSet(set.between(1.00, 1.02), "[]");
		assertSet(set.between(1.00, 1.03), "[" + entryHaha + "]");
		assertSet(set.between(1.00, 3.00), "[" + entryHaha + "]");
		assertSet(set.between(2.00, 3.00), "[]");
		
		set.add(entryHehe);
		assertSet(set.between(2.00, 3.00), "[" + entryHehe + "]");
		assertSet(set.between(1.00, 3.00), "[" + entryHaha + ", " + entryHehe + "]");
		
		set.remove(entryHehe);
		assertSet(set.between(1.00, 3.00), "[" + entryHaha + "]");
	}
}
