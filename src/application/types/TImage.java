package application.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TImage
{
	public enum Maps {DATA, LINKS};
	
	//image data
	private Map<String, String> __data;
	private Map<String, String> __links;

	
	public TImage()
	{
		this.__data = new HashMap<String, String>();
		this.__links = new HashMap<String, String>();
	}
	
	public Map<String, String> getMap(Maps map)
	{
		return (map == Maps.DATA) ? this.__data : this.__links;
	}
	
	public Map<String, String> getProperties(Maps map)
	{
		return getMap(map);
	}
	
	public String getProperty(Maps map, String key)
	{
		return ((map == Maps.DATA) ? this.__data : this.__links).get(key);
	}
	
	public void setProperty(Maps map, String key, String value)
	{
		//no null values,
		if (value == null)
			value = "";
		
		//using ternary operator we can squeeze if else case to a single line, much cleaner :P
		((map == Maps.DATA) ? this.__data : this.__links).put(key, value);
	}
	
	public void setProperties(Maps map, List<String> keys, List<String> values)
	{
		if (!(keys.size() == values.size()))
			throw new IndexOutOfBoundsException("Amount of keys doesn't match the amount of values, no null values this time!");
		
		for (int i = 0; i < keys.size(); i++)
			setProperty(map, keys.get(i), values.get(i));
	}
	
	public boolean compare(TImage image)
	{
		return (this.__data.get("sha512_hash").equals(image.getProperty(Maps.DATA, "sha512_hash")));
	}
}
