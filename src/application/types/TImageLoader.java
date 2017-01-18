package application.types;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import application.types.TImage.Maps;
import javafx.application.Platform;
import javafx.scene.image.Image;

/*
 	
 */


public class TImageLoader implements Callable<List<Image>>
{
	//change this eventually, so that we can have multiple cache folders at the same time (not useful?)
	private TCacheManager __manager = TCacheManager.instance();
	private List<TImage> images;
	private String version;
	
	public TImageLoader()
	{
		this.images = new ArrayList<TImage>();
	}
	
	public TImageLoader(TImage container, String version)
	{	
		this();
		
		this.images.add(container);
		this.version = version;
	}
	
	public TImageLoader(List<TImage> containers, String version)
	{
		this();
		
		this.images = containers;
		this.version = version;
	}
	
	private Image load(String URL) throws Exception
	{
		Image img = new Image(URL, 150, 150, true, false, false);

		return img;
	}
	
	private Image loadFromCache(String ID, String type) throws ClassNotFoundException, IOException, InterruptedException, ExecutionException
	{
		if (!__manager.exists(ID))
			return null;
			
		__manager.load(ID, type);
		
		Map<String, Future<TResult<Image>>> __loaded = __manager.getCurrentJobs();

		if (__loaded == null)
			return null;
		
		Future<TResult<Image>> __result = null;
		boolean found = false;
		Image img = null;
		while (!found)
		{
			
			for (String __id : __loaded.keySet())
				if (ID.equals(__id))
				{
					
					__result = __loaded.get(ID);
					__loaded.remove(ID);
					break;
				}
			
			if (__result != null)
			{
				System.out.println("Something");
				img = __result.get().get();
				found = true;
			}
		}
		
		return img;
	}
	
	private List<Image> __load() throws Exception
	{
		List<Image> result = new ArrayList<Image>();
		Image img = null;
		String __ID = null;
		
		for (TImage image : images)
		{
			__ID = image.getProperty(Maps.MAP_PROPERTIES, "id");
			
			if (__manager.exists(__ID))
			{
				System.out.println("Image already exists on cache");
				img = loadFromCache(__ID, image.getProperty(Maps.MAP_PROPERTIES, "original_format"));
			}
			else
			{
				img = load(image.getProperty(Maps.MAP_IMAGES, version));
			}
			
			result.add(img);
		}
		
		
		return result;
	}
	
	@SuppressWarnings("unused")
	private List<Image> __images_load() throws Exception
	{
		if (images.size() == 0)
			return null;
		
		if (images.size() == 1)
			if (__manager.exists(images.get(0).getProperty(Maps.MAP_PROPERTIES, "id")))
				return Arrays.asList(loadFromCache(images.get(0).getProperty(Maps.MAP_PROPERTIES, "id"), images.get(0).getProperty(Maps.MAP_PROPERTIES, "original_format")));
			else
				return Arrays.asList(load(images.get(0).getProperty(Maps.MAP_IMAGES, version)));

		List<Image> result = new ArrayList<Image>();
		
		for (TImage image : images)
		{
			if (__manager.exists(image.getProperty(Maps.MAP_PROPERTIES, "id")))
				result.add(loadFromCache(image.getProperty(Maps.MAP_PROPERTIES, "id"), image.getProperty(Maps.MAP_PROPERTIES, "original_format")));
			else
				result.add(load(image.getProperty(Maps.MAP_IMAGES, version)));
		}

		return result;
	}

	@Override
	public List<Image> call() throws Exception 
	{
		if (images.size() == 0)
			return null;

		return __load();
	}
}
