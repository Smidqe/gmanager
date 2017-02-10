package application.types.custom.gallery;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;

import application.types.TCacheManager;
import application.types.TGrabber;
import application.types.TGrabber.Status;
import application.types.factories.FThreadFactory;
import application.types.sites.TSite;
//import application.types.TThumbnailRefresher;
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
	private TViewport __viewport;
	private TGrabber __grabber;
	//private TThumbnailRefresher __refresher;
	private TCacheManager __cache;
	private TSite __site;
	private int __current_page = 1;
	
	private ExecutorService __threads;
	private volatile BlockingDeque<Action> __action_deque;
	
	//meant for the background threads
	private List<Future<?>> __futures;
	
	public TGallery(TilePane tiles, ScrollPane container) throws MalformedURLException, InterruptedException 
	{
		//create the deques
		this.__action_deque = new LinkedBlockingDeque<Action>();
		this.__futures = new ArrayList<Future<?>>();
		
		
		
		//initialize some other necessary variables
		this.__cache = TCacheManager.instance();
		this.__tiles = tiles;
		this.__container = container;
		
		this.__viewport = new TViewport(this.__tiles, this.__container);
		this.__manager = new TTileManager(this.__viewport, this.__tiles);
		this.__grabber = new TGrabber(this.__manager, this.__action_deque, this.__viewport); 
		//this.__refresher = new TThumbnailRefresher(this, this.__action_deque);		
		

		//create a threadpool for the subthreads
		this.__threads = Executors.newCachedThreadPool(new FThreadFactory("TGallery", "Subthreads", true));
		
		//TODO: For testing! Remove once finished;
		this.__site = new TSite();
		this.__site.setURL("images", new URL("https://derpibooru.org/images.json"));
		this.__grabber.setURL(this.__site.getURL("images", "?page=", __current_page), false);
		
		//add the listeners for resizing and scrolling
		__container.vvalueProperty().addListener(createScrollListener());
		__container.widthProperty().addListener(createResizeListener());
		__container.heightProperty().addListener(createResizeListener());
		
		//Add the refresher.
		//__futures.add(this.__threads.submit(this.__refresher));
		__futures.add(this.__threads.submit(this.__grabber));
		__futures.add(this.__threads.submit(this.__cache));
		__futures.add(this.__threads.submit(this.__manager));
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
//						try 
//						{
//							if ((__refresher.getStatus() == TThumbnailRefresher.Status.IDLE) && __allow_refresh)
//								__action_deque.put(Action.REFRESHER);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
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
//					//prevent from refreshing when TTilemanager is doing something
//					__allow_refresh = (__manager.getStatus() != TTileManager.Status.RUNNING);
//
//					//make sure that we don't overwhelm the refresher
//					if ((__refresher.getStatus() == TThumbnailRefresher.Status.IDLE) && __allow_refresh)
//						__action_deque.put(Action.REFRESHER);

					
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
	
	public BoundingBox getViewportLocation()
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
			case PAGES: break; //return this.__manager.getPages()
			case IMAGES: return this.__manager.amount();

		}
		
		return -1;
	}

//	public TThumbnailRefresher getRefresher()
//	{
//		return this.__refresher;
//	}

	public void stop() throws Exception 
	{
		__grabber.stop();
		//__refresher.stop();
		__cache.stop();
		__manager.stop();
		__action_deque.put(Action.SHUTDOWN);

		for (Future<?> future: __futures)
			future.get();

		__threads.shutdown();
		
	}
}
