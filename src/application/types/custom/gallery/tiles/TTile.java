package application.types.custom.gallery.tiles;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import application.types.TCacheManager;
import application.types.TImage;
import application.types.TImage.Maps;
import application.types.TResult;
import application.types.custom.gallery.TViewport;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/*
	TODO:
		- Figure out a method to calculate the position
			- Eventually we can use simple id numbers,
				- For this we need to know how many tiles there are in one row otherwise
				this won't work at all.
 */

public class TTile implements Runnable
{
	public enum Status {NULL, NONE, LOADED}
	public enum Action {NULL, SHOW, HIDE, STOP}

	private ImageView __node;
	private TImage __data;
	private TViewport __viewport;
	
	private Status __status;
	private Action __action;

	private boolean __stop;
	private boolean __running;
	
	private ChangeListener<Number> __listener;
	
	//this only meant for adding a image to node if it doesn't exist
	private Image __temp;
	
	public TTile(TViewport viewport) 
	{
		// TODO Auto-generated constructor stub
		this.__node = null;
		this.__data = null;
		
		this.__status = Status.NULL;
		this.__action = Action.NULL;
		
		this.__viewport = viewport;

		//listens when the node is not visible on viewport location (not bounds which are static)
		//and automatically sets the action to corresponding value to trigger another cycle.
		__listener = new ChangeListener<Number>(){

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) 
			{
				if (!__running)
				{
					boolean clips = __viewport.intersects(__node);
				
					if (!clips && __status == Status.LOADED)
						__action = Action.HIDE;
				
				//check if we have 
					if (clips && __status == Status.NONE)
						__action = Action.SHOW;
					
					//System.out.println("Tile: " + __data.getProperty(Maps.DATA, "id") + " visible in viewport: " + clips);
				}
				
				
			}};
			
		//attach a listener to the tile
		if (__viewport != null)
			this.__viewport.getScrollPane().vvalueProperty().addListener(__listener);
	}
	
	public TTile(TViewport viewport, TImage data, ImageView node)
	{
		this(viewport);
		
		this.__node = node;
		this.__data = data;
	}
	
	public void setStatus(Status status)
	{
		this.__status = status;
	}
	
	public void setAction(Action action)
	{
		this.__action = action;
	}
	
	public Status getStatus()
	{
		return this.__status;
	}
	
	public void setData(TImage data)
	{
		this.__data = data;	
	}
	
	public TImage getData()
	{
		return this.__data;
	}
	
	public void setNode(ImageView n)
	{
		this.__node = n;
		
		if (n == null)
			return;
		
		this.__status = (n.getImage() == null) ? Status.NONE : Status.LOADED;
	}
	
	public ImageView getNode()
	{
		return this.__node;
	}

	public boolean load() throws InterruptedException, ExecutionException
	{
		this.__running = true;
		
		if (this.__data == null)
			throw new NullPointerException("__data is null, no image data was given, can't load image");
		
		if (this.__node == null)
			throw new NullPointerException("__node is null, no imageview was given, can't load image");
		
		//don't load an image that is already been loaded
		if (this.__node.getImage() != null)
		{
			this.__status = Status.LOADED;
			this.__running = false;
			
			return true;
		}

		String id = this.__data.getProperty(Maps.DATA, "id");
		String size = this.__data.getProperty(Maps.LINKS, "thumb_small"); //thumb small is placeholder, once we have 
		TCacheManager __manager = TCacheManager.instance();
		Future<TResult<Image>> __job = null;
		
		if (__manager.exists(id))
		{
			//System.out.println("TTile: " + id + " exists in cache, loading it from there.");
			
			//add a job to cache manager
			__manager.load(id, size);
			
			//wait while it is in the queue
			while (__manager.inQueue(id))
				Thread.sleep(1);
			
			//we can then get it from the manager
			if ((__job = __manager.getStartedJobs().get(id)) == null)
				return false;
			
			//it'll wait even more once it has been got (possible)
			__temp = __job.get().get();
			
			//delete the job from the map
			__manager.getStartedJobs().remove(id);
		}
		
		if (__temp == null)
		{
			//check if url is valid
			
			//load the image from internet
			__temp = new Image(size, 150, 150, true, false, false);
			
			while (__temp.getProgress() != 1)
				Thread.sleep(1);
			
			__manager.save(__temp, id, this.__data.getProperty(Maps.DATA, "original_format"));
			
			//System.out.println("TTile: " + this.__data.getProperty(Maps.DATA, "id") + " loaded (hopefully): " + __temp);
		}
		
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				__node.setImage(__temp);
			}});

		while (__node.getImage() == null)
			Thread.sleep(1);
		
		//System.out.println("__node.getImage(): " + __node.getImage() + ", progress: " + __node.getImage().getProgress());
		
		if (__node.getImage() != null)
			this.__status = Status.LOADED;
		
		this.__running = false;
		
		return __node.getImage() != null;
	}
	
	public boolean release() throws InterruptedException
	{
		if (this.__node == null)
			throw new NullPointerException("__node is null, can't release an image");
		
		this.__running = true;
		
		if (this.__node.getImage() == null)
		{
			this.__status = Status.NONE;
			this.__running = false;
			
			return true;
		}
		
		this.__status = Status.NONE;
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				__node.setImage(null);
			}});

		while (__node.getImage() != null)
			Thread.sleep(1);
		
		this.__running = false;
		
		return this.__node.getImage() == null;		
	}

	public void stop()
	{
		this.__stop = true;
		this.__action = Action.STOP;
	}
	
	@Override
	public void run() 
	{
		
		System.out.println("TTile: " + this.__data.getProperty(Maps.DATA, "id") + " started");
		while (!this.__stop)
		{
			try 
			{
				//wait until we have something to do
				while (this.__action == Action.NULL)
					Thread.sleep(1);
				
				switch (this.__action) {
				case SHOW:
				{
					if (!load())
						System.out.println(this.__data.getProperty(Maps.DATA, "id") + " failed to load. Investigate why.");

					break;
				}
				
				case HIDE:
				{
					release();
					break;
				}
				
				case STOP: continue;
				
				default:
					break;
				}
				
			
				this.__action = Action.NULL;
			} 
			catch (Exception e) 
			{
				// TODO: handle exception
				e.printStackTrace();
				
				//set the __aciton to null so that it doesn't go into infinite loop
				this.__action = Action.NULL;
			}
			
		}
		
		System.out.println("TTile: " + this.__data.getProperty(Maps.DATA, "id") + " stopped");
	}
}

