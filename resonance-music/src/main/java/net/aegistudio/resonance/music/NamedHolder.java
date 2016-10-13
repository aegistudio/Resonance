package net.aegistudio.resonance.music;

import java.util.Collection;
import java.util.HashSet;
import java.util.TreeMap;

import net.aegistudio.resonance.music.KeywordArray.KeywordEntry;
import net.aegistudio.resonance.serial.SerializedObject;
import net.aegistudio.resonance.serial.Structure;
import net.aegistudio.resonance.serial.Type;

/** In order to implements retrieve by name **/

public abstract class NamedHolder<T extends SerializedObject> implements SerializedObject, Updatable
{
	protected final String alreadyExists;
	protected final String notExists;
	protected final TreeMap<String, NamedEntry<T>> entries = new TreeMap<String, NamedEntry<T>>();
	protected final boolean shouldStoreClass;
	
	public NamedHolder(String className, boolean shouldStoreClass)
	{
		this.alreadyExists = "The " + className + " named %s already exists.";
		this.notExists = "The " + className + " doesn't exists.";
		this.shouldStoreClass = shouldStoreClass;
	}
	
	@Override
	public void load(Structure input) {
		for(String name : input.keySet()) {
			Structure subStructure = input.get(name, Type.STRUCTURE, new Structure());
			Class<?> clazz = null;
			if(this.shouldStoreClass) clazz = subStructure.get("class", Type.CLASS, (Class<?>)null);
			T t = this.newObject(clazz);
			t.load(subStructure);
			NamedEntry<T> entry = new NamedEntry<T>(name, t);
			entries.put(name, entry);
		}
	}
	
	protected abstract T newObject(Class<?> clazz);
	
	@Override
	public void save(Structure output)
	{
		for(String name : entries.keySet())
		{
			T t = entries.get(name).getValue();
			Structure obj = new Structure();
			t.save(obj);
			if(this.shouldStoreClass)
				obj.set("class", Type.CLASS, t.getClass());
			output.set(name, Type.STRUCTURE, obj);
		}
	}
	
	public synchronized boolean doesExists(String name)
	{
		return entries.containsKey(name);
	}
	
	public synchronized T create(String name)
	{
		if(this.shouldStoreClass)
			throw new IllegalArgumentException("The class should be assigned!");
		else return this.create(name, null);
	}
	
	public synchronized T create(String name, Class<? extends T> clazz)
	{
		if(this.doesExists(name))
			throw new IllegalArgumentException(String.format(alreadyExists, name));
		T returnValue = this.newObject(clazz);
		entries.put(name, new NamedEntry<T>(name, returnValue));
		updated.clear();
		return returnValue;
	}
	
	public synchronized void remove(String name)
	{
		if(!this.doesExists(name))
			throw new IllegalArgumentException(String.format(notExists, name));
		entries.get(name).name = null;
		entries.remove(name);
		updated.clear();
	}
	
	public synchronized void rename(String name, String newName)
	{
		NamedEntry<T> old;
		if((old = entries.get(name)) == null)
			throw new IllegalArgumentException(String.format(notExists, name));
		
		if(this.doesExists(newName))
			throw new IllegalArgumentException(String.format(alreadyExists, newName));	

		entries.remove(name);
		entries.put(newName, old);
		old.name = newName;
		updated.clear();
	}
	
	public synchronized T get(String name)
	{
		NamedEntry<T> entry = this.getEntry(name);
		if(entry == null) return null;
		else return entry.value;
	}
	
	public synchronized NamedEntry<T> getEntry(String name)
	{
		if(name == null) return null;
		else return entries.get(name);
	}
	
	HashSet<Object> updated = new HashSet<Object>();
	
	public synchronized boolean hasUpdated(Object asker)
	{
		if(this.updated.contains(asker))
			return false;
		else
		{
			this.updated.add(asker);
			return true;
		}
	}
	
	public Collection<? extends KeywordEntry<String, T>> allValues()
	{
		return this.entries.values();
	}
	
	public Collection<? extends KeywordEntry<String, T>> allEntries()
	{
		return this.entries.values();
	}
	
	/**
	 * This class is intended to keep value identified when its
	 * keyword changes.
	 * 
	 * @author aegistudio
	 * @param <T>
	 */
	public static class NamedEntry<T> implements KeywordEntry<String, T>
	{
		protected String name;
		protected T value;
		
		public NamedEntry(String key, T value)
		{
			this.name = key;
			this.value = value;
		}
		
		@Override
		public String getKeyword() {
			return name;
		}

		@Override
		public T getValue() {
			return value;
		}
		
		@Override
		public String toString()
		{
			return getKeyword();
		}
	}
}
