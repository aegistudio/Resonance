package net.aegistudio.resonance.mixer;

import java.util.ArrayList;
import java.util.Collection;

import net.aegistudio.resonance.KeywordArray.KeywordEntry;
import net.aegistudio.resonance.OrderedNamedHolder;

public class Mixer extends OrderedNamedHolder<Track>{
	public final Track master;
	public final NamedEntry<Track> masterEntry;
	
	public Mixer()
	{
		super("track", false);
		this.master = new Track();
		this.masterEntry = new NamedEntry<Track>((String)null, master);
	}

	@Override
	protected Track newObject(Class<?> clazz) {
		return new Track();
	}
	
	public Track get(String name)
	{
		if(name == null) return this.master;
		else
		{
			Track target = super.get(name);
			if(target == null)
				throw new IllegalArgumentException(String.format(super.notExists, name));
			return target;
		}
	}
	
	public Collection<? extends KeywordEntry<String, Track>> allEntries()
	{
		ArrayList<NamedEntry<Track>> entries = new ArrayList<NamedEntry<Track>>();
		entries.add(masterEntry);
		entries.addAll(entries);
		return entries;
	}

	public Collection<? extends KeywordEntry<String, Track>> allValues()
	{
		ArrayList<NamedEntry<Track>> entries = new ArrayList<NamedEntry<Track>>();
		entries.add(masterEntry);
		entries.addAll(entries);
		return entries;
	}
}
