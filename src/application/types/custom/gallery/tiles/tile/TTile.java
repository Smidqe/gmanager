package application.types.custom.gallery.tiles.tile;

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
import javafx.geometry.BoundingBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/*
	TODO:
		- Figure out a method to calculate the position. DONE

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
	
	private int __id;
	
	private ChangeListener<Number> __listener;
	
	//this only meant for adding a image to node if it doesn't exist
	private Image __temp;
	
	public TTile() 
	{
		// TODO Auto-generated constructor stub
		this.__node = null;
		this.__data = null;
		
		this.__status = Status.NULL;
		this.__action = Action.NULL;
		
		this.__viewport = null;


	}
	
	public void bindToViewport(TViewport viewport)
	{
		this.__viewport = viewport;
		
		//listens when the node is not visible on viewport location (not bounds which are static)
		//and automatically sets the action to corresponding value to trigger another cycle.
		__listener = new ChangeListener<Number>(){

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) 
			{
				if (!__running)
				{
					boolean clips = __viewport.intersects(getLocation());

					if (!clips && __status == Status.LOADED)
						__action = Action.HIDE;
				
					if (clips && (__status == Status.NONE || __status == Status.NULL))
						__action = Action.SHOW;
				}
			}};
			
		//attach a listener to the tile
		if (__viewport != null)
			this.__viewport.getScrollPane().vvalueProperty().addListener(__listener);
	}
	
	public void setID(int id)
	{
		this.__id = id;
	}
	
	public int getID()
	{
		return this.__id;
	}
	
	public BoundingBox getLocation()
	{
		int amount = __viewport.getTilesInRow();
		int row, column = 0;
		
		//get the row and column using math
		row = (int) Math.floor(this.__id / amount);
		column = __id - (row * amount);

		//TODO: Remove the 150px constants once I've managed to do TSettings
		//will probably be TSettings.getSetting("iNodeSize") or similar
		return new BoundingBox(column * __viewport.getTilePane().getHgap() + (column * 150), row * __viewport.getTilePane().getVgap() + (row * 150), 150, 150);
	}
	
	public TTile(TImage data, ImageView node)
	{
		this();
		
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
		
		if (__manager.exists(id)) // && !__manager.inUse(id)
		{
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
			
			//enable once the cache manager has been rewritten
			//__manager.save(__temp, id, this.__data.getProperty(Maps.DATA, "original_format"));
		}
		
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				__node.setImage(__temp);
			}});

		while (__node.getImage() == null)
			Thread.sleep(1);

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

		//wait until the image has been added to the node, will cause problems if not.
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
		while (!this.__stop)
		{
			try 
			{
				//wait until we have something to do
				while (this.__action == Action.NULL)
					Thread.sleep(1);
				
				switch (this.__action) 
				{
					case SHOW:
					{
						//there is no need to load if it is hidden
						if (!__viewport.intersects(getLocation()))
							break;

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

