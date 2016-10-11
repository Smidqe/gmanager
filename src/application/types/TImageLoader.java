package application.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import application.types.TImage.enum_map;
import javafx.scene.image.Image;

/*
 	
 */


public class TImageLoader implements Callable<List<Image>>
{
	public enum Method {URL, CACHE};
	
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
	
	private Image load(String URL)
	{
		return new Image(URL, 150, 150, true, false, true);
	}
	
	private List<Image> __images_load()
	{
		if (images.size() == 0)
			return null;
		
		if (images.size() == 1)
			return Arrays.asList(load(images.get(0).getProperty(enum_map.MAP_IMAGES, version)));
		
		List<Image> result = new ArrayList<Image>();
		for (TImage image : images)
			result.add(load(image.getProperty(enum_map.MAP_IMAGES, version)));
		
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
