package application.types;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.simple.JSONObject;

import application.extensions.connections;
import application.types.factories.FThreadFactory;
import application.types.interfaces.IWebCodes;
import application.types.interfaces.IWebCodes.Codes;
import javafx.application.Platform;
import javafx.scene.image.ImageView;


public class TGrabber extends Observable implements Runnable
{
	public enum Status {IDLE, RUNNING};
	
	private static TGrabber __self = new TGrabber();
	private TTileManager __tiles;
	private URL url;
	private boolean stop;
	private BlockingDeque<String> deque;
	private Status __status;
	
	
	private TGrabber() 
	{
		this.url = null;
		this.__status = Status.IDLE;
	}

	public void setURL(URL url, boolean forceGrab) throws InterruptedException
	{
		this.url = url;
		
		if (forceGrab)
			this.deque.put("Changed");
	}

	public void bind(TTileManager manager, BlockingDeque<String> deque)
	{
		this.__tiles = manager;
		this.deque = deque;
	}
	
	public static TGrabber instance()
	{
		return __self;
	}

	public void stop()
	{
		this.stop = true;
	}
	
	public Status getStatus()
	{
		return this.__status;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		ExecutorService __executor = Executors.newCachedThreadPool(new FThreadFactory("TGrabber", "__executor", true));

		while (!this.stop)
		{
			this.__status = Status.RUNNING;
			
			System.out.println("TGrabber - Starting loading, URL: " + this.url);
			
			if (this.url == null)
				break;
			
			try {
				String response = connections.ping(this.url);
				
				if (IWebCodes.inRange(response, Codes.SUCCESS))
				{

					System.out.println("URL: " + this.url);
					
					JSONObject object = connections.getJSON(this.url);
					
					
					Future<List<Map<String, Object>>> parsed = __executor.submit(new TParser(object));
					List<Map<String, Object>> list = parsed.get();
					
					List<Future<TImage>> images = new ArrayList<Future<TImage>>();
					List<TImage> finals = new ArrayList<TImage>();
					
					for (Map<String, Object> map : list)
						images.add(__executor.submit(new TImageBuilder(map)));
				
					for (Future<TImage> image : images)
						finals.add(image.get());
					
					List<TImageContainer> containers = new ArrayList<TImageContainer>();
					for (TImage image : finals)
						containers.add(new TImageContainer(image, new ImageView()));
					
					System.out.println("TGrabber: " + containers);
					
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							System.out.println("Running on FX thread: adding more tiles");
							
							try {
								__tiles.add(containers);
							} catch (InterruptedException | ExecutionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
					
				}
				else
				{
					System.out.println("TGrabber - Ping resulted: " + response + ", grabber will not run.");
				}
				
				this.__status = Status.IDLE;
				
				deque.take();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				break;
			}
		}
		
		__executor.shutdown();
		System.out.println("TGrabber - Shutting down");
	}

	public void setStatus(Status status) 
	{
		this.__status = status;
	}
}