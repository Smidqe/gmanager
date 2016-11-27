package application.types.custom;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;

import application.types.TCacheManager;
import application.types.TGrabber;
import application.types.TGrabber.Status;
import application.types.TImageSaver;
import application.types.factories.FThreadFactory;
import application.types.sites.TSite;
import application.types.TThumbnailRefresher;
import application.types.TTileManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.BoundingBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;


/*
	TODO:
		- 
 */

public class TGallery
{
	public enum Count {IMAGES, PAGES};
	public enum Action {REFRESHER, GRABBER, SHUTDOWN};
	
	private TilePane __tiles;
	private ScrollPane __container;
	private TTileManager __manager;
	private TGrabber __grabber;
	private TThumbnailRefresher __refresher;
	private TCacheManager __cache;
	private TImageSaver __saver; //TODO: Remove this once I've figured something out
	private TSite __site;
	private int __current_page = 1;
	
	private ExecutorService __threads;
	private volatile BlockingDeque<Action> __action_deque;
	private List<Future<?>> __futures;
	
	private boolean __allow_refresh;
	
	public TGallery(TilePane tiles, ScrollPane container) throws MalformedURLException, InterruptedException 
	{
		this.__allow_refresh = true;
		
		//create the deques
		this.__action_deque = new LinkedBlockingDeque<Action>();
		this.__futures = new ArrayList<Future<?>>();
		
		//initialize some other necessary variables
		this.__tiles = tiles;
		this.__container = container;
		this.__saver = new TImageSaver();
		this.__manager = new TTileManager(this.__tiles, this.__action_deque, this.__saver);
		this.__grabber = new TGrabber(this.__manager, this.__action_deque); 
		this.__refresher = new TThumbnailRefresher(this, this.__action_deque);		
		
		//bind the managers deque to grabbers deque
		
		//bind the dimensions of the tilepane to the parent
		//this.__tiles.prefHeightProperty().bind(this.__container.heightProperty());
		//this.__tiles.prefWidthProperty().bind(this.__container.widthProperty());
		
		//create a threadpool for the subthreads
		this.__threads = Executors.newCachedThreadPool(new FThreadFactory("TGallery", "Subthreads", true));
		
		//TODO: For testing! Remove once finished;
		this.__site = new TSite();
		this.__cache = TCacheManager.instance();
		this.__site.setURL("images", new URL("https://derpibooru.org/images.json"));
		this.__grabber.setURL(this.__site.getURL("images", "?page=", __current_page), false);
		
		//add the listeners for resizing and scrolling
		__container.vvalueProperty().addListener(createScrollListener());
		__container.widthProperty().addListener(createResizeListener());
		__container.heightProperty().addListener(createResizeListener());
		
		//Add the refresher.
		__futures.add(this.__threads.submit(this.__refresher));
		__futures.add(this.__threads.submit(this.__grabber));
		__futures.add(this.__threads.submit(this.__cache));
	}

	private ChangeListener<Number> createResizeListener()
	{
		return new ChangeListener<Number>()
		{
			final Timer timer = new Timer();
			TimerTask task = null;
			final long delay = 200;
			
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				// TODO Auto-generated method stub
				
				if (task != null)
					task.cancel();
				
				task = new TimerTask()
				{
					@Override
					public synchronized void run() 
					{
						try 
						{
							if ((__refresher.getStatus() == TThumbnailRefresher.Status.IDLE) && __allow_refresh)
								__action_deque.put(Action.REFRESHER);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};
				
				timer.schedule(task, delay);
			}
		};
	}
	
	private ChangeListener<Number> createScrollListener()
	{
		return new ChangeListener<Number>(){

			@Override
			public synchronized void changed(ObservableValue<? extends Number> arg0, Number arg1, Number valueNew) 
			{
				try {					
					//make sure that we don't overwhelm the refresher
					
					//System.out.println("TGallery: __allow_refreshing: " + __allow_refresh);
					
					if ((__refresher.getStatus() == TThumbnailRefresher.Status.IDLE) && __allow_refresh)
						__action_deque.put(Action.REFRESHER);
				
					
					if ((valueNew.doubleValue() == 1.0) && (__grabber.getStatus() == Status.IDLE))
					{
						__grabber.setURL(__site.getURL("images", "?page=", ++__current_page), false);
						__action_deque.put(Action.GRABBER);
					}
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}		
			
		};
	}
	
	public synchronized BoundingBox getViewportLocation()
	{
		double __value = (__tiles.getHeight() - __container.getViewportBounds().getHeight()) * __container.getVvalue();
		
		return new BoundingBox(0, __value, __container.getWidth(), __container.getViewportBounds().getHeight());
	}
	
	public synchronized TilePane getTilePane()
	{
		return this.__tiles;
	}
	
	public ScrollPane getScrollPane()
	{
		return this.__container;
	}
	
	public TTileManager getManager()
	{
		return this.__manager;
	}
	
	//will eventually get the amount of pictures/pages loaded depending on argument id
	public int getAmount(Count id)
	{
		switch (id)
		{
			case PAGES: break;
			case IMAGES: return this.__manager.getImages().size();

		}
		
		return -1;
	}

	public TThumbnailRefresher getRefresher()
	{
		return this.__refresher;
	}
	
	public void allowRefreshing(boolean value)
	{
		this.__allow_refresh = value;
	}
	
	public void stop() throws InterruptedException, ExecutionException 
	{
		__grabber.stop();
		__refresher.stop();
		__saver.stop();
		__cache.stop();
		
		__action_deque.put(Action.SHUTDOWN);

		for (Future<?> future: __futures)
			future.get();

		__threads.shutdown();
		
	}
}
