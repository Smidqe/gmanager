package application.types.interfaces;

import java.util.Arrays;
import java.util.List;

import application.extensions.integers;

public class IWebCodes {

	public enum Codes 
	{
		INFORMATIONAL (100, 102),
		SUCCESS (200, 208),
		REDIRECTION (300, 308),
		CLIENT_ERROR (400, 451),
		SERVER_ERROR (500, 511);
		
		int vMin;
		int vMax;
		
		private Codes(int min, int max)
		{
			this.vMax = min;
			this.vMax = max;
		}
		
		public List<Integer> getRange()
		{
			return Arrays.asList(vMin, vMax);
		}
	}
	
	public static boolean inRange(int code, Codes range) throws Exception
	{
		return integers.inRange(code, range.getRange());
	}

	public static boolean inRange(String ping, Codes range) throws NumberFormatException, Exception {
		return inRange(Integer.parseInt(ping), range);
	}
}
