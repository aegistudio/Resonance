package net.aegistudio.resonance.serial;

import java.lang.reflect.Array;
import java.util.Set;
import java.util.TreeMap;

/**
 * The structure provides a common interface for serialization.
 * Structure itself is a key to value map, allowing setting single key or value
 * to certain type. (But before it was used, its type should be declared.)
 * @author aegistudio
 */

public class Structure
{	
	public final TreeMap<String, Type<?>> typeMap = new TreeMap<String, Type<?>>();
	public final TreeMap<String, Object> valueMap = new TreeMap<String, Object>();
	
	public void declare(String key, Type<?> type)
	{
		if(type == null)
		{
			typeMap.remove(key);
			valueMap.remove(key);
		}
		else
		{
			if(typeMap.containsKey(key))
				valueMap.remove(key);
			typeMap.put(key, type);
		}
	}
	
	public Set<String> keySet()
	{
		return this.typeMap.keySet();
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(String key, Type<T> defaultType, T defaultValue)
	{
		Type<?> type = typeMap.get(key);
		if(type == null)
			this.declare(key, defaultType);
		else if(type != defaultType)
			return defaultValue;
		
		T value = (T) valueMap.get(key);
		return value == null? defaultValue : value;
	}
	
	public <T> void set(String key, Type<T> type, T value)
	{
		if(!typeMap.containsKey(key)) this.declare(key, type);
		else if(type != typeMap.get(key)) return;
		if(value == null) valueMap.remove(key);
		else valueMap.put(key, value);
	}
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append('{');
		boolean isFirst = true;
		for(String key : typeMap.keySet())
		{
			if(isFirst) isFirst = false;
			else builder.append(", ");
			builder.append(typeMap.get(key).name().toLowerCase());
			builder.append(' ');
			builder.append(key);
			builder.append(" : ");
			Object value = valueMap.get(key);
			if(value == null) builder.append("null");
			else
			{
				if(value.getClass().isArray())
				{
					builder.append('[');
					int length = Array.getLength(value);
					for(int i = 0; i < length ; i ++)
					{
						if(i > 0) builder.append(", ");
						builder.append(Array.get(value, i));
					}
					builder.append(']');
				}
				else if(value instanceof String)
				{
					builder.append('\"');
					builder.append(value);
					builder.append('\"');
				}
				else builder.append(value);
			}
		}
		builder.append('}');
		return new String(builder);
	}
}
