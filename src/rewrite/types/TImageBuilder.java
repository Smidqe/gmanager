package rewrite.types;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import rewrite.extensions.strings;
import rewrite.types.TImage.enum_map;

public class TImageBuilder<T> implements Callable<TImage>{

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
				object.setProperty(TImage.enum_map.MAP_PROPERTIES, key, ((List<String>) information.get(key)).toString());
			else if (information.get(key) instanceof Map)
			{
				Map<String, String> map = (Map<String, String>) information.get(key);
				
				for (String subkey : map.keySet())
					object.setProperty(TImage.enum_map.MAP_PROPERTIES, subkey, map.get(subkey));
			}
			else
			{	
				String value;
				if (information.get(key) == null)
					value = "";
				else
					value = information.get(key).toString();
					
				object.setProperty(enum_map.MAP_PROPERTIES, key, value);
			}
		}
		
		keys = object.getProperties(enum_map.MAP_PROPERTIES).keySet();
		
		for (String key : keys)
		{
			String value = object.getProperty(enum_map.MAP_PROPERTIES, key);
			
			if (!strings.contains(Arrays.asList("https", "http", "//"), value, false))
				continue;
			
			if (value.contains("//"))
				value = "https:" + value;
			
			object.setProperty(enum_map.MAP_IMAGES, key, value);
		}
		
		return object;
	}

	
	@Override
	public TImage call() throws Exception {
		return build();
	}
}
