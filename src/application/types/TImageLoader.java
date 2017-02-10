package application.types;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import application.types.TImage.Maps;
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
	
	private synchronized Image loadFromCache(String ID, String type) throws ClassNotFoundException, IOException, InterruptedException, ExecutionException
	{
		if (!__manager.exists(ID))
			return null;
			
		__manager.load(ID, type);
		
		Map<String, Future<TResult<Image>>> __loaded = __manager.getStartedJobs();

		if (__loaded == null)
			return null;
		
		
		int cycles = 0;
		Future<TResult<Image>> __result = null;
		boolean found = false;
		Image img = null;
		while (!found && cycles < 10)
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
				img = __result.get().get();
				found = true;
			}
			
			cycles++;
		}
		
		if (!(cycles < 10))
			System.out.println("TImageLoader - Image was not found in 10 cycles.");
		
		return img;
	}
	
	private List<Image> __load() throws Exception
	{
		List<Image> result = new ArrayList<Image>();
		Image img = null;
		String __ID = null;
		
		for (TImage image : images)
		{
			__ID = image.getProperty(Maps.DATA, "id");
			
			if (__manager.exists(__ID))
				img = loadFromCache(__ID, image.getProperty(Maps.DATA, "original_format"));
			else
			{
				img = load(image.getProperty(Maps.LINKS, version));
			}
			
			result.add(img);
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
