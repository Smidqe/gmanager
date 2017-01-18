package application.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TImage
{
	public enum Maps {MAP_PROPERTIES, MAP_IMAGES};;
	
	private Map<String, String> properties;
	private Map<String, String> images;

	public TImage()
	{
		this.properties = new HashMap<String, String>();
		this.images = new HashMap<String, String>();
	}
	
	public Map<String, String> getMap(Maps map)
	{
		if (map == Maps.MAP_PROPERTIES)
			return properties;
		else
			return images;
	}
	
	public Map<String, String> getProperties(Maps map)
	{
		return getMap(map);
	}
	
	public synchronized String getProperty(Maps map, String key)
	{
		return getMap(map).get(key);
	}
	
	public synchronized void setProperty(Maps map, String key, String value)
	{
		if (value == null)
			value = "";
		
		if (map == Maps.MAP_PROPERTIES)
			this.properties.put(key, value);
		else
			this.images.put(key, value);
	}
	
	public synchronized void setProperties(Maps map, List<String> keys, List<String> values)
	{
		if (!(keys.size() == values.size()))
			throw new IndexOutOfBoundsException("Amount of keys doesn't match the amount of values, no null values this time!");
		
		for (int i = 0; i < keys.size(); i++)
			setProperty(map, keys.get(i), values.get(i));
	}

	public synchronized boolean compare(TImage image)
	{
		return (this.properties.get("sha512_hash").equals(image.properties.get("sha512_hash")));
	}
	
	public synchronized Map<String, String> getImageURLs()
	{
		return this.images;
	}
}
