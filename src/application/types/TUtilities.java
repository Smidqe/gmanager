package application.types;

import java.util.ArrayList;
import java.util.List;

public class TUtilities {

	public static <T> List<T> clearDuplicates(List<T> array)
	{
		ArrayList<T> filtered = new ArrayList<T>();
		
		for (T object : array)
			if (filtered.indexOf(object) == -1)
				filtered.add(object);
		
		return filtered;
	}

}
