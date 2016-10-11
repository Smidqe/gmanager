package application.extensions;

import java.util.List;

public class sorters 
{
	public static <T> List<T> quickSort(List<T> list, boolean ascending)
	{
		if (list == null || list.size() <= 1)
			return list;

		T object = list.get(0);
		
		//cannot be compared, 
		if (!(object instanceof Comparable))
			return list;
			
			
		return null; 
	}
	
}
