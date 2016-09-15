package rewrite.extensions;

import java.util.ArrayList;
import java.util.List;

public class strings {
	public static boolean contains(List<String> values, String string, boolean all)
	{
		int matches = 0;
		
		for (int i = 0; i < values.size(); i++)
			matches += string.contains(values.get(i)) ? 1 : 0;
		
		return all ? (values.size() == matches) : matches > 0;
	}
	
	public static <T> List<String> toArray(List<T> list)
	{	
		List<String> parsed = new ArrayList<String>();
		
		for (T type : list)
			parsed.add(type.toString());
		
		return parsed;	
	}
	
	
}
