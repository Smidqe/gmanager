package application.types.custom.gallery.tiles.tile;

import java.util.concurrent.ExecutionException;

import application.extensions.images;
import application.types.TImage;
import application.types.TImage.Maps;
import application.types.custom.gallery.TViewport;
import application.types.custom.gallery.cache.TCacheAction;
import application.types.custom.gallery.cache.TCacheAction.cAction;
import application.types.custom.gallery.cache.TCacheManager;
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
	private TCacheManager __manager;
	
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
		this.__manager = TCacheManager.instance();
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
		TCacheAction __cache = null;
		//TODO: Check from the manager if the image is in use and if it 
		if (__manager.exists(id))
		{
			//while (__manager.inUse(id))
			//	Thread.sleep(1)
			
			//add a job to cache manager
			__cache = __manager.get(id);
			__cache.setAction(cAction.READ);
			
			while ((__temp = __cache.getImage()) == null)
				Thread.sleep(1);
		}
		
		//meaning it doesn't exist
		if (__temp == null)
		{
			//load the image from internet
			__temp = new Image(size, 150, 150, true, false, false);
			
			while (__temp.getProgress() != 1)
				Thread.sleep(1);
			
			//gif files still cause trouble, since they cannot be cloned the same method as other formats. due to their animation
			if (!this.__data.getProperty(Maps.DATA, "original_format").equals("gif"))
			{
				__manager.add(new TCacheAction(id, images.clone(__temp)));
			
				//don't check the data when it is running
				while (__manager.getStatus() == TCacheManager.Status.RUNNING)
					Thread.sleep(1);
					
				while (!__manager.exists(id))
					Thread.sleep(1);
				
				__cache = __manager.get(id);
				__cache.setAction(cAction.WRITE);
			}
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

