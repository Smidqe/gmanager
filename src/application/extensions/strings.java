package application.extensions;

import java.util.ArrayList;
import java.util.List;
/*
	This is a compilation of string related functions that don't exist or exists in different format

 */
public class strings {
	//checks if any of the values in list are found in the string, depending on count of those found will return true/false (it has to match or exceed)
	public static boolean contains(List<String> values, String string, int count)
	{
		int matches = 0;
		
		for (int i = 0; i < values.size(); i++)
			matches += string.contains(values.get(i)) ? 1 : 0;
		
		return (count != 0) ? matches >= count : matches > 0;
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
	
	
	/*
	 * This parses a given string which is a array in literal string form, to a list of it by given div and bracket chars
	 */
	public static List<String> parse(String string, char div, char[] bracket)
	{
		if (bracket.length > 2 || bracket.length == 0)
			throw new IllegalArgumentException("");
		
		List<String> list = new ArrayList<String>();
		
		int prev = 0;
		for (int i = 0; i < string.length(); i++)
		{
			char c = string.charAt(i);
			
			if (c == bracket[1])
			{
				//get the last one
				list.add(string.substring(prev, i));
				continue;
			}
			
			if (c == bracket[0])
			{
				prev = i;
				continue;
			}
			
			if (c == div)
			{
				list.add(string.substring(prev + 1, i).trim());
				prev = i + 1;
			}
		}
		
		return list;
		
	}
}
