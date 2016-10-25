package application.types;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import application.types.TImage.Maps;
import javafx.scene.image.Image;

/*
 	
 */


public class TImageLoader implements Callable<List<Image>>
{
	private List<TImage> images;
	private String version;
	private TCacheManager __manager = TCacheManager.instance();
	
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
	
	private Image load(String URL)
	{
		return new Image(URL, 150, 150, true, false, true);
	}
	
	private Image loadFromCache(String ID) throws ClassNotFoundException, IOException
	{
		if (!__manager.exists(ID))
			return null;
			
		return (Image) __manager.load(ID);
	}
	
	private List<Image> __images_load() throws ClassNotFoundException, IOException
	{
		if (images.size() == 0)
			return null;
		
		if (images.size() == 1)
			if (__manager.exists(images.get(0).getProperty(Maps.MAP_PROPERTIES, "ID")))
				return Arrays.asList(loadFromCache(images.get(0).getProperty(Maps.MAP_PROPERTIES, "ID")));
			else
				return Arrays.asList(load(images.get(0).getProperty(Maps.MAP_IMAGES, version)));

		List<Image> result = new ArrayList<Image>();
		for (TImage image : images)
		{
			if (__manager.exists(image.getProperty(Maps.MAP_PROPERTIES, "ID")))
				result.add(loadFromCache(image.getProperty(Maps.MAP_PROPERTIES, "ID")));
			else
				result.add(load(image.getProperty(Maps.MAP_IMAGES, version)));
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
