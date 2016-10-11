package application.extensions;

import java.util.List;
import java.util.Map;

public class maps 
{
	public static <K, V> Map<K, V> remove(Map<K, V> map, List<K> keys)
	{
		for (K key : keys)
			map.remove(key);
		
		return map;
	}
	
	public static <K, V> Map<K, V> add(Map<K, V> map, K key, V value, boolean condition)
	{
		if (condition)
			map.put(key, value);
		
		return map;
	}
	
	public static <K, V> Map<K, V> move(Map<K, V> from, Map<K, V> to, K key)
	{
		to.put(key, from.get(key));
		
		return to;
	}
	
	
}
