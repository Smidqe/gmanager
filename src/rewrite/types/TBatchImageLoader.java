package rewrite.types;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.application.Platform;

@SuppressWarnings("unused")
public class TBatchImageLoader implements Runnable
{
	private List<Map<String, Object>> object;
	private TTileManager manager = TTileManager.instance();
	
	public TBatchImageLoader(List<Map<String, Object>> object) 
	{
		this.object = object;
	}

	public void load()
	{
		ExecutorService __executor = Executors.newCachedThreadPool();

		//load the necessary id's from the settings eventually, for now I will keep it as constant
		for (Map<String, Object> map : object)
			__executor.submit(new TImageBuilder<>(map));	
		
		__executor.shutdown();
	}
	
	@Override
	public void run() {

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
			}});
	}
}
