package application.types.custom;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import application.types.TGrabber;
import application.types.TGrabber.Status;
import application.types.TSite;
import application.types.TThumbnailRefresher;
import application.types.TTileManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.BoundingBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;


/*
	TODO:
		- Combine deques to a single one
		- 
 */

public class TGallery
{
	public enum Count {IMAGES, PAGES};
	
	private TilePane __tiles;
	private ScrollPane __container;
	private TTileManager __manager;
	private TGrabber __grabber;
	private TThumbnailRefresher __refresher;
	private TSite __site;
	private int __current_page = 1;
	
	private ExecutorService __threads;
	private BlockingDeque<String> __action_deque, __grabber_deque;
	
	public TGallery(TilePane tiles, ScrollPane container) throws MalformedURLException, InterruptedException 
	{
		this.__action_deque = new LinkedBlockingDeque<String>();
		this.__grabber_deque = new LinkedBlockingDeque<String>();
		
		this.__tiles = tiles;
		this.__container = container;
		this.__manager = new TTileManager(this.__tiles, this.__action_deque);
		this.__grabber = TGrabber.instance(); //TODO: Switch this to a non-singleton form.
		this.__refresher = new TThumbnailRefresher(this, this.__action_deque);
		//
		this.__grabber.bind(__manager, __grabber_deque);
		
		//bind the dimensions of the tilepane to the parent
		this.__tiles.prefHeightProperty().bind(this.__container.heightProperty());
		this.__tiles.prefWidthProperty().bind(this.__container.widthProperty());
		
		//create thread for the refresher
		this.__threads = Executors.newCachedThreadPool();
		
		//TODO: For testing! Remove once finished;
		this.__site = new TSite();
		this.__site.setURL("images", new URL("https://derpibooru.org/images.json"));
		this.__grabber.setURL(this.__site.getURL("images", "?page=", ++__current_page), false);
		
		//add the listeners for resizing and scrolling
		__container.vvalueProperty().addListener(createScrollListener());
		__container.widthProperty().addListener(createResizeListener());
		__container.heightProperty().addListener(createResizeListener());
		
		//Add the refresher.
		this.__threads.submit(this.__refresher);
		this.__threads.submit(this.__grabber);
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
					public void run() 
					{
						try {
							__action_deque.put("");
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
				System.out.println("Changed");
				
				try {
					__action_deque.put("");
				
					if ((valueNew.doubleValue() == 1.0) && (__grabber.getStatus() == Status.IDLE))
					{
						__grabber.setURL(__site.getURL("images", "?page=", ++__current_page), false);
						__grabber_deque.put("ss");
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
		//double __value = pane.getHeight() * viewport.getVvalue() - viewport.getViewportBounds().getHeight() * viewport.getVvalue();
		double __value = (__tiles.getHeight() - __container.getViewportBounds().getHeight()) * __container.getVvalue();
		
		return new BoundingBox(0, __value, __container.getWidth(), __container.getViewportBounds().getHeight());
	}
	
	public TilePane getTilePane()
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
	
	public int getAmount(Count id)
	{
		switch (id)
		{
			case PAGES: break;
			case IMAGES: return this.__manager.getImages().size();

		}
		
		return -1;
	}

	public void stop() throws InterruptedException 
	{
		__grabber.stop();
		__refresher.stop();
		
		__action_deque.put("");
		__threads.shutdown();
	}
}
