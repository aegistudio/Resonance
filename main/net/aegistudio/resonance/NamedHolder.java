package net.aegistudio.resonance;

import java.util.Collection;
import java.util.HashSet;
import java.util.TreeMap;

import net.aegistudio.resonance.serial.SerializedObject;
import net.aegistudio.resonance.serial.Structure;
import net.aegistudio.resonance.serial.Type;

/** In order to implements retrieve by name **/

public abstract class NamedHolder<T extends SerializedObject> implements SerializedObject
{
	protected final String alreadyExists;
	protected final String notExists;
	protected final TreeMap<String, T> entries = new TreeMap<String, T>();
	protected final boolean shouldStoreClass;
	
	public NamedHolder(String className, boolean shouldStoreClass)
	{
		this.alreadyExists = "The " + className + " named %s already exists.";
		this.notExists = "The " + className + " doesn't exists.";
		this.shouldStoreClass = shouldStoreClass;
	}
	@Override
	public void load(Structure input) {
		for(String name : input.keySet())
		{
			Structure subStructure = input.get(name, Type.STRUCTURE, new Structure());
			Class<?> clazz = subStructure.get("class", Type.CLASS, null);
			T t = this.newObject(clazz);
			t.load(subStructure);
			entries.put(name, t);
		}

	}
	
	protected abstract T newObject(Class<?> clazz);
	
	@Override
	public void save(Structure output)
	{
		for(String name : entries.keySet())
		{
			T t = entries.get(name);
			Structure obj = new Structure();
			t.save(obj);
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
		entries.put(name, returnValue);
		updated.clear();
		return returnValue;
	}
	
	public synchronized void remove(String name)
	{
		if(!this.doesExists(name))
			throw new IllegalArgumentException(String.format(notExists, name));
		entries.remove(name);
		updated.clear();
	}
	
	public synchronized void rename(String name, String newName)
	{
		T old;
		if((old = entries.get(name)) == null)
			throw new IllegalArgumentException(String.format(notExists, name));
		
		if(this.doesExists(name))
			throw new IllegalArgumentException(String.format(alreadyExists, name));	

		entries.remove(name);
		entries.put(newName, old);
		updated.clear();
	}
	
	public synchronized T get(String name)
	{
		if(name == null) return null;
		return entries.get(name);
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
	
	public Collection<T> all()
	{
		return this.entries.values();
	}
}
