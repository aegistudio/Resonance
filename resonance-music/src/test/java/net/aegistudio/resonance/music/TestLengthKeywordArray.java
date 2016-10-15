package net.aegistudio.resonance.music;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import net.aegistudio.resonance.music.KeywordArray;
import net.aegistudio.resonance.music.LengthKeywordArray;
import net.aegistudio.resonance.music.LengthObject;
import net.aegistudio.resonance.music.RoundedKeywordArray;
import net.aegistudio.resonance.music.KeywordArray.DefaultKeywordEntry;

public class TestLengthKeywordArray {
	LengthKeywordArray<DefaultLengthObject<String>> set;
	
	@Before
	public void setup() {
		KeywordArray<Double, DefaultLengthObject<String>> innerSet = new RoundedKeywordArray<>();
		set = new LengthKeywordArray<>(innerSet);
		
		innerSet.add(new DefaultKeywordEntry<>(1.0, new DefaultLengthObject<>(2.0, "first")));
		innerSet.add(new DefaultKeywordEntry<>(1.5, new DefaultLengthObject<>(1.0, "second")));
	}
	
	private void assertSet(Collection<?> collection, String toString) {
		assertEquals(Arrays.deepToString(collection.toArray()), toString);
	}
	
	@Test
	public void testPrintAllElements() {
		assertSet(set.all(), "[<1.0, 2.0, first>, <1.5, 1.0, second>]");
	}
	
	@Test
	public void testCommon1() {
		set.betweenTrigger(1.0, 1.2);
		assertSet(set.begin(), "[<1.0, 2.0, first>]");
		assertSet(set.end(), "[]");
		
		set.betweenTrigger(1.2, 1.4);		
		assertSet(set.begin(), "[]");
		assertSet(set.end(), "[]");
		
		set.betweenTrigger(1.4, 1.6);
		assertSet(set.begin(), "[<1.5, 1.0, second>]");
		assertSet(set.end(), "[]");
		
		set.betweenTrigger(2.4, 2.6);
		assertSet(set.begin(), "[]");
		assertSet(set.end(), "[<1.5, 1.0, second>]");
		
		/** Test whether the removed item will be monitored again! **/
		set.betweenTrigger(2.4, 2.6);
		assertSet(set.begin(), "[]");
		assertSet(set.end(), "[]");
		
		/** The begin item should be ended before begin again. **/
		set.betweenTrigger(1.0, 1.2);
		assertSet(set.begin(), "[<1.0, 2.0, first>]");
		assertSet(set.end(), "[<1.0, 2.0, first>]");
	}
}

class DefaultLengthObject<T> implements LengthObject {
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
	
	
	public String toString() {
		return this.length + ", " + value.toString();
	}
}
