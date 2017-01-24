package application.types;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import application.extensions.strings;
import application.types.TImage.Maps;

public class TImageBuilder implements Callable<TImage>{

	private Map<String, Object> information;
	private List<String> skippable;
	
	public TImageBuilder(Map<String, Object> object)
	{
		this.information = object;
	}

	public void setSkips(List<String> skip)
	{
		this.skippable = skip;
	}

	@SuppressWarnings("unchecked")
	private TImage build()
	{
		TImage object = new TImage();
		Set<String> keys = information.keySet();
		
		for (String key : keys)
		{
			if (skippable != null)
				if (strings.contains(skippable, key, false)) 
					continue;
			
			if (information.get(key) instanceof List)
				object.setProperty(Maps.MAP_PROPERTIES, key, ((List<String>) information.get(key)).toString());
			else if (information.get(key) instanceof Map)
			{
				Map<String, String> map = (Map<String, String>) information.get(key);
				
				for (String subkey : map.keySet())
					object.setProperty(Maps.MAP_PROPERTIES, subkey, map.get(subkey));
			}
			else
			{	
				String value;
				if (information.get(key) == null)
					value = "";
				else
					value = information.get(key).toString();
					
				object.setProperty(Maps.MAP_PROPERTIES, key, value);
			}
		}
		
		keys = object.getProperties(Maps.MAP_PROPERTIES).keySet();
		
		//fix the urls, for example derpibooru doesn't have http/https in thumbnail/image links
		for (String key : keys)
		{
			String value = object.getProperty(Maps.MAP_PROPERTIES, key);
			
			if (!strings.contains(Arrays.asList("https", "http", "//"), value, false))
				continue;
			
			if (value.contains("//") && !strings.contains(Arrays.asList("https", "http"), value, false));
				value = "https:" + value;
			
			object.setProperty(Maps.MAP_IMAGES, key, value);
		}
		
		return object;
	}

	
	@Override
	public TImage call() throws Exception {
		return build();
	}
}
