package rewrite.types;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import application.types.TIDCreator;

public class TImage implements Serializable
{
	public enum enum_map {MAP_PROPERTIES, MAP_IMAGES};
	
	private static final long serialVersionUID = 1L;
	
	private Map<String, String> properties;
	private Map<String, String> images;

	public TImage()
	{
		this.properties = new HashMap<String, String>();
		this.images = new HashMap<String, String>();
	}
	
	public Map<String, String> getMap(enum_map map)
	{
		if (map == enum_map.MAP_PROPERTIES)
			return properties;
		else
			return images;
	}
	
	public Map<String, String> getProperties(enum_map map)
	{
		return getMap(map);
	}
	
	public synchronized String getProperty(enum_map map, String key)
	{
		return getMap(map).get(key);
	}
	
	public synchronized void setProperty(enum_map map, String key, String value)
	{
		if (value == null)
			value = "";
		
		if (map == enum_map.MAP_PROPERTIES)
			this.properties.put(key, value);
		else
			this.images.put(key, value);
	}
	
	public synchronized void setProperties(enum_map map, List<String> keys, List<String> values)
	{
		if (!(keys.size() == values.size()))
			throw new IndexOutOfBoundsException("Amount of keys doesn't match the amount of values, no null values this time!");
		
		for (int i = 0; i < keys.size(); i++)
			setProperty(map, keys.get(i), values.get(i));
	}

	public synchronized boolean compare(String image)
	{
		return (this.images.get("thumbnail").equals(image) || this.images.get("full").equals(image));
	}
	
	public synchronized boolean compare(TImage image)
	{
		return (image.images.get("thumbnail").equals(this.images.get("thumbnail")) && image.images.get("full").equals(this.images.get("full")));
	}
	
	public synchronized Map<String, String> getImageURLs()
	{
		return this.images;
	}
	
	public synchronized void createID() throws Exception
	{		
		ExecutorService service = Executors.newSingleThreadExecutor();

		this.properties.put("ID", service.submit(new TIDCreator<>(this)).get());
		service.shutdown();
	}
}
