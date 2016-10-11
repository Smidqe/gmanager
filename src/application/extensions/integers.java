package application.extensions;

import java.util.List;

public class integers {
	public static boolean inRange(int value, int min, int max) throws Exception
	{
		//check if we don't have mixed values
		if (min > max)
			throw new Exception("integers::inRange - Min is larger than max");
		
		return (min <= value && value <= max);
	}
	
	public static boolean inRange(int value, List<Integer> range) throws Exception
	{
		//check if the range is valid
		if (range.size() != 2)
			throw new Exception("integers::inRange - Range should only have 2 values");
		
		//swap the values if the first index is higher than the second (reversed order)
		if (range.get(0) > range.get(1))
			range = arrays.swap(range, 0, 1);

		return inRange(value, range.get(0), range.get(1));
	}

}
