package rewrite.types;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.application.Platform;

@SuppressWarnings("unused")
public class TBatchImageLoader implements Runnable
{
	private List<Map<String, Object>> object;
	private TTileManager manager = TTileManager.instance();
	private List<TImage> images;
	
	public TBatchImageLoader(List<Map<String, Object>> object) 
	{
		this.object = object;
	}

	
	
	public void load()
	{
		ExecutorService __executor = Executors.newFixedThreadPool(object.size());
		
		for (Map<String, Object> map : object)
			__executor.submit(new TImageBuilder(map));
		
		__executor.shutdown();
	}
	
	@Override
	public void run() 
	{
		
	}
}
