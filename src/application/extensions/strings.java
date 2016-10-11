package application.extensions;

import java.util.ArrayList;
import java.util.List;

public class strings {
	//checks if any of the values in list are found in the string, depending on count of those found will return true/false (it has to match or exceed)
	public static boolean contains(List<String> values, String string, int count)
	{
		int matches = 0;
		
		for (int i = 0; i < values.size(); i++)
			matches += string.contains(values.get(i)) ? 1 : 0;
		
		return count != 0 ? matches >= count : matches > 0;
	}
	
	public static boolean contains(List<String> values, String string, boolean all)
	{
		return contains(values, string, all ? values.size() : 0);
	}
	
	public static <T> List<String> toArray(List<T> list)
	{	
		List<String> parsed = new ArrayList<String>();
		
		for (T type : list)
			parsed.add(type.toString());
		
		return parsed;	
	}
	
	
}
