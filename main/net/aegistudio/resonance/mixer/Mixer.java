package net.aegistudio.resonance.mixer;

import net.aegistudio.resonance.NamedHolder;

public class Mixer extends NamedHolder<Track>{
	public final Track master;
	
	public Mixer()
	{
		super("track", false);
		this.master = new Track();
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
	
}
