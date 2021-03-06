package application.types.custom.gallery;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;

import application.types.TGrabber;
import application.types.TGrabber.Status;
import application.types.factories.FThreadFactory;
import application.types.sites.TSite;
import application.types.sites.TSite.MAPS;
import application.types.custom.gallery.cache.TCacheManager;
import application.types.custom.gallery.tiles.TTileManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.BoundingBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;


/*
	TODO:
		- 
 */
@SuppressWarnings("unused")
public class TGallery
{
	public enum Count {IMAGES, PAGES};
	public enum Action {REFRESHER, GRABBER, SHUTDOWN};
	
	private TilePane __tiles;
	private ScrollPane __container;
	private TTileManager __manager;
	private TViewport __viewport;
	private TGrabber __grabber;
	private TCacheManager __cache;
	private TSite __site;
	private int __current_page = 1;
	
	private boolean __continuous; // for the future if user wants paged or continuous scrolling. also causes
	
	private ExecutorService __threads;
	private volatile BlockingDeque<Action> __action_deque;
	
	//meant for the background threads, not sure anymore, probably don't need it.
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

		//create a threadpool for the subthreads
		this.__threads = Executors.newCachedThreadPool(new FThreadFactory("TGallery", "Subthreads", true));
		
		//TODO: For testing! Remove once finished;
		this.__site = new TSite();
		this.__site.put(MAPS.VARIABLE, "prefix", "?page=");
		this.__site.putURL("images", new URL("https://derpibooru.org/images.json"));
		this.__grabber.setURL(this.__site.getURL("images", __current_page), false);
		
		//add a listener for checking if we have reached the bottom of the scroll
		__container.vvalueProperty().addListener(createScrollListener());
		
		//Add the classes to 
		__futures.add(this.__threads.submit(this.__grabber));
		__futures.add(this.__threads.submit(this.__cache));
		__futures.add(this.__threads.submit(this.__manager));
	}

	
	private ChangeListener<Number> createScrollListener()
	{
		return new ChangeListener<Number>(){

			@Override
			public synchronized void changed(ObservableValue<? extends Number> arg0, Number arg1, Number valueNew) 
			{
				try {					
					if ((valueNew.doubleValue() == 1.0) && (__grabber.getStatus() == Status.IDLE))
					{
						__grabber.setURL(__site.getURL("images", ++__current_page), false);
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

	public void stop() throws Exception 
	{
		__grabber.stop();
		__cache.stop();
		__manager.stop();
		__action_deque.put(Action.SHUTDOWN);

		for (Future<?> future: __futures)
			future.get();

		__threads.shutdown();
		
	}
}
