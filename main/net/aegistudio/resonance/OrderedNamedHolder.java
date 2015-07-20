package net.aegistudio.resonance;

import java.util.ArrayList;
import java.util.Collection;

import net.aegistudio.resonance.KeywordArray.KeywordEntry;
import net.aegistudio.resonance.serial.SerializedObject;
import net.aegistudio.resonance.serial.Structure;
import net.aegistudio.resonance.serial.Type;

public abstract class OrderedNamedHolder<T extends SerializedObject> extends NamedHolder<T>
{
	public ArrayList<String> orderList;
	public OrderedNamedHolder(String className, boolean shouldStoreClass) {
		super(className, shouldStoreClass);
		orderList = new ArrayList<String>();
	}
	
	public synchronized T create(String name, Class<? extends T> clazz)
	{
		T creation = super.create(name, clazz);
		orderList.add(name);
		return creation;
	}

	public synchronized void remove(String name)
	{
		super.remove(name);
		orderList.remove(name);
	}
	
	public synchronized void rename(String name, String newName)
	{
		super.rename(name, newName);
		int index = orderList.indexOf(name);
		orderList.set(index, newName);
	}
	
	public Collection<KeywordEntry<String, T>> allEntries()
	{
		ArrayList<KeywordEntry<String, T>> ordered = new ArrayList<KeywordEntry<String, T>>(orderList.size());
		for(String entry : orderList)
			ordered.add(new KeywordArray.DefaultKeywordEntry<String, T>(entry, super.entries.get(entry)));
		return ordered;
	}
	
	@Override
	public void save(Structure output)
	{
		Structure[] structures = new Structure[orderList.size()];
		for(int i = 0; i < orderList.size(); i ++)
		{
			String name = orderList.get(i);
			T t = entries.get(name);
			Structure obj = new Structure();
			t.save(obj);
			obj.set("name", Type.STRING, name);
			obj.set("class", Type.CLASS, t.getClass());
			structures[i] = obj;
		}
		output.set("orderedholder", Type.STRUCTURE_ARRAY, structures);
	}
	
	public void load(Structure input)
	{
		Structure[] structures = input.get("orderedholder", Type.STRUCTURE_ARRAY, new Structure[0]);
		for(Structure subStructure : structures)
		{
			Class<?> clazz = null;
			if(this.shouldStoreClass) clazz = subStructure.get("class", Type.CLASS, null);
			T t = this.newObject(clazz);
			t.load(subStructure);
			String name = subStructure.get("name", Type.STRING, null);
			entries.put(name, t);
			orderList.add(name);
		}
	}
}
