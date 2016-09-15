package rewrite.extensions;

import java.util.ArrayList;
import java.util.List;

public class arrays {
	public static <T> List<T> add(List<T> list, T object, boolean append)
	{
		if (list == null || object == null)
			throw new NullPointerException();
		
		if (append)
			list.add(object);
			
		return list;
	}
	
	public static <T> List<T> add(List<T> list, T object)
	{
		return add(list, object, true);
	}
	
	public static <T> List<T> add(List<T> list, List<T> append) {
		for (T object : append)
			list = add(list, object, true);
		
		return list;
	}
	
	public static <T> List<T> clearDoubles(List<T> list)
	{
		if (list == null)
			throw new NullPointerException();
		
		List<T> filtered = new ArrayList<T>();
		
		for (T object : list)
			add(filtered, object, filtered.indexOf(object) == -1);
		
		return filtered;
	}
	
	public static <T> boolean same(Object object, Class<T> compare)
	{
		return (compare.isInstance(object));
	}
	
	public static <T> boolean same(List<Class<T>> list, Object object)
	{
		for (int i = 0; i < list.size(); i++)
			if (same(object, list.get(i)))
				return true;
		
		return false;
	}
	
	public static <T> List<T> remove(List<T> list, int from, int to)
	{
		if (from < 0 || to > list.size())
			throw new IndexOutOfBoundsException();
		
		for (int i = to; i > from ; i--)
			list.remove(i);
		
		return list;
	}
	
	public static <T> List<T> remove(List<T> list, List<Integer> indexes)
	{
		indexes.sort((a, b) -> Integer.compare(a, b)); //a simple quicksort (or whatever the java uses), 
		
		for (int i = indexes.size(); i == 0;)
			list.remove(i);
		
		return list;
	}
}
