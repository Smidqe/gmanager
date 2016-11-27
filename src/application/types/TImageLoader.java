package application.types;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;

import application.types.TCacheManager.Methods;
import application.types.TImage.Maps;
import javafx.scene.image.Image;

/*
 	
 */


public class TImageLoader implements Callable<List<Image>>
{
	
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
	
	private Image load(String URL, String ID) throws Exception
	{
		Image img = new Image(URL, 150, 150, true, false, false);
		
		__manager.add(img, ID);
		__manager.start(Methods.METHOD_SAVE);
		return img;
	}
	
	private Image loadFromCache(String ID) throws ClassNotFoundException, IOException, InterruptedException
	{
		if (!__manager.exists(ID))
			return null;
			
		__manager.load(ID);
		__manager.start(Methods.METHOD_LOAD);
		
		BlockingDeque<Map<String, Image>> __loaded = __manager.getLoaded();

		if (__loaded == null)
			return null;
		
		boolean __found = false;
		Image __image = null;
		while (!__found)
		{
			if (__loaded.peek().containsKey(ID))
			{
				__image = __loaded.takeFirst().get(ID);
				__found = true;
			}
			else
				Thread.sleep(1);
		}
		
		return __image;
	}
	
	private List<Image> __images_load() throws Exception
	{
		if (images.size() == 0)
			return null;
		
		if (images.size() == 1)
			if (__manager.exists(images.get(0).getProperty(Maps.MAP_PROPERTIES, "id")))
				return Arrays.asList(loadFromCache(images.get(0).getProperty(Maps.MAP_PROPERTIES, "id")));
			else
				return Arrays.asList(load(images.get(0).getProperty(Maps.MAP_IMAGES, version), images.get(0).getProperty(Maps.MAP_PROPERTIES, "id")));

		List<Image> result = new ArrayList<Image>();
		
		for (TImage image : images)
		{
			if (__manager.exists(image.getProperty(Maps.MAP_PROPERTIES, "id")))
				result.add(loadFromCache(image.getProperty(Maps.MAP_PROPERTIES, "id")));
			else
				result.add(load(image.getProperty(Maps.MAP_IMAGES, version), image.getProperty(Maps.MAP_PROPERTIES, "id")));
		}

		return result;
	}

	@Override
	public List<Image> call() throws Exception 
	{
		if (images.size() != 0)
			return __images_load();
			
		return null;
	}
}
