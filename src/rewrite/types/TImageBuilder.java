package rewrite.types;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import rewrite.extensions.arrays;
import rewrite.extensions.strings;
import rewrite.types.TImage.enum_map;

public class TImageBuilder<T> implements Callable<TImage>{

	private TImage object;
	private Map<String, Object> information;
	private List<Class<T>> skippable;
	
	public TImageBuilder(Map<String, Object> object)
	{
		this.information = object;
	}

	public void setSkips(List<Class<T>> classes)
	{
		this.skippable = classes;
	}

	@SuppressWarnings("unchecked")
	private void build()
	{
		object = new TImage();
		Set<String> keys = information.keySet();
		
		for (String key : keys)
		{
			
			if (skippable != null)
				if (arrays.same(skippable, information.get(key)))
					continue;
			
			if (information.get(key) instanceof List)
			{
				System.out.println("Builder - List: " + information.get(key));
				
				
			}
			else if (information.get(key) instanceof Map)
			{
				System.out.println("Builder - Map: " + information.get(key));
				
				Map<String, String> map = (Map<String, String>) information.get(key);
				for (String subkey : map.keySet())
				{
					System.out.println("Builder - Map: " + subkey + ", value: " + map.get(subkey));
					object.setProperty(TImage.enum_map.MAP_PROPERTIES, subkey, map.get(subkey));
				}
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
	}

	
	@Override
	public TImage call() throws Exception {
		build();

		return this.object;
	}
}
