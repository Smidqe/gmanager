package rewrite.types;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import javafx.scene.image.ImageView;
import rewrite.extensions.connections;

public class TGrabber implements Callable<List<TImageContainer>>
{
	private static TGrabber __self = new TGrabber();
	private URL url;
	
	private TGrabber() 
	{
		this.url = null;
	}

	public void setURL(URL url)
	{
		this.url = url;
	}

	public static TGrabber instance()
	{
		return __self;
	}

	@Override
	public List<TImageContainer> call() throws Exception {
		ExecutorService __executor = Executors.newCachedThreadPool();
		
		try {
			if (!connections.ping(this.url).equals("200"))
				return null;
			
			JSONObject object = connections.getJSON(this.url.toString());

			Future<List<Map<String, Object>>> result = __executor.submit(new TParser(object));
			List<Map<String, Object>> list = result.get();
			
			List<Future<TImage>> images = new ArrayList<Future<TImage>>();
			List<TImage> finals = new ArrayList<TImage>();
			
			for (Map<String, Object> map : list)
				images.add(__executor.submit(new TImageBuilder<>(map)));
			
			for (Future<TImage> image : images)
				finals.add(image.get());
			
			List<TImageContainer> containers = new ArrayList<TImageContainer>();
			for (TImage image : finals)
				containers.add(new TImageContainer(image, new ImageView()));
			
			images.clear();
			finals.clear();
			list.clear();

			return containers;
			
			
			
		} catch (IOException | ParseException | InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			__executor.shutdown();
		}
		
		return null;
	}

}