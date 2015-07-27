package net.aegistudio.resonance.mixer;

import java.util.ArrayList;
import java.util.Collection;

import net.aegistudio.resonance.KeywordArray.KeywordEntry;
import net.aegistudio.resonance.OrderedNamedHolder;

public class Mixer extends OrderedNamedHolder<Track>{
	public final Track master;
	public final NamedEntry<Track> masterEntry;
	
	public String masterName;
	
	public Mixer()
	{
		super("track", false);
		this.master = new Track();
		this.masterEntry = new NamedEntry<Track>(null, master)
		{
			public String getKeyword()
			{
				return masterName;
			}
		};
	}

	@Override
	protected Track newObject(Class<?> clazz) {
		return new Track();
	}
	
	public void renameMaster(String masterName)
	{
		this.masterName = masterName;
	}
	
	public Track get(String name)
	{
		if(name == null || name.equals(masterName)) return this.master;
		else
		{
			Track target = super.get(name);
			if(target == null)
				throw new IllegalArgumentException(String.format(super.notExists, name));
			return target;
		}
	}
	
	public NamedEntry<Track> getEntry(String name)
	{
		if(name == null || name.equals(masterName)) return this.masterEntry;
		else return super.getEntry(name);
	}
	
	public Collection<? extends KeywordEntry<String, Track>> allEntries()
	{
		Collection<KeywordEntry<String, Track>> entries = new ArrayList<KeywordEntry<String, Track>>();
		entries.add(masterEntry);
		entries.addAll(super.allEntries());
		return entries;
	}

	public Collection<? extends KeywordEntry<String, Track>> allValues()
	{
		Collection<KeywordEntry<String, Track>> entries = new ArrayList<KeywordEntry<String, Track>>();
		entries.add(masterEntry);
		entries.addAll(super.allValues());
		return entries;
	}
}
