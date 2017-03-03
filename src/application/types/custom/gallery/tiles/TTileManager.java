package application.types.custom.gallery.tiles;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import application.types.custom.gallery.TViewport;
import application.types.custom.gallery.tiles.tile.TTile;
import application.types.custom.gallery.tiles.tile.TTile.Action;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.TilePane;

public class TTileManager implements Runnable
{
	public enum Status {IDLE, RUNNING, ERROR};
	
	private boolean __stop = false;
	
	//currently managed tiles
	private List<TTile> __images = new ArrayList<TTile>();
	//tiles waiting to be added
	private BlockingDeque<TTile> __queue = new LinkedBlockingDeque<TTile>();
	private ExecutorService __subthreads;
	
	private TilePane __tiles;
	private TViewport __viewport;
	
	private TTile __tile;
	private Status __status;
	private int __amount;
	
	public TTileManager(TViewport viewport, TilePane tiles)
	{
		this.__tiles = tiles;
		this.__subthreads = Executors.newCachedThreadPool();
		this.__amount = 0;
		this.__viewport = viewport;
	}
	
	public Status getStatus()
	{
		return this.__status;
	}
	
	public void add(TTile image) throws InterruptedException
	{
		if (image == null)
			throw new NullPointerException("Parameter: image is null");
		
		this.__queue.put(image);
	}
	
	public void add(List<TTile> images) throws InterruptedException
	{
		if (images == null)
			throw new NullPointerException("add(List<>): Parameter images is null");
		
		for (TTile tile : images)
			add(tile);
	}
	
	//no use currently
	public void remove(TTile image)
	{
		if (image == null)
			throw new NullPointerException("Parameter: image is null");
		
		int pos = __images.indexOf(image);
		
		if (pos == -1)
			return;
		
		this.remove(pos);
	}
	
	public void remove(int index)
	{
		this.__images.get(index).stop();
		this.__images.remove(index);
	}
	
	public void stop() throws InterruptedException
	{
		this.__stop = true;
		this.__queue.put(new TTile());
	}
	
	public TTile getTile(int index)
	{
		return __images.get(index);
	}
	
	public TTile getTile(Node node)
	{
		for (TTile __image : __images)
			if (__image.getNode().equals(node))
				return __image;
		
		return null;
	}
	
	public int amount()
	{
		return this.__images.size();
	}
	
	@Override
	public void run() 
	{
		int old;
		
		while (!this.__stop)
		{
			this.__status = Status.IDLE;
			
			try {
				__tile = __queue.take();
				
				this.__status = Status.RUNNING;
				
				if (__tile.getData() == null)
				{
					System.out.println("__tile.getData() is null"); 
					continue;
				}
				
				//bind the viewport to the tile, so that the tile can refresh itself
				__tile.bindToViewport(this.__viewport);
				
				//submit the tile to executor and add it to managed list
				__subthreads.submit(__tile);
				__images.add(__tile);
				
				//set the ID for the tile (for now only means amount, possibly will be for download eventually)
				//using just the nodes is possible, but to make sure what we just clicked and getting the data from the right tile
				__tile.setID(__amount++);
				
				//get the current size of the tilepane's children
				old = __tiles.getChildren().size();
				Platform.runLater(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						__tile.setAction(Action.SHOW);
						__tiles.getChildren().add(__tile.getNode());
						
					}});

				//wait until fx thread has put the imageview to tilepane
				while (__tiles.getChildren().size() == old)
					Thread.sleep(1);
				
			} catch (InterruptedException e) {
				//TODO: Do better exception handling eventually.
				System.out.println(e.getMessage());
				System.out.println(__tile.toString());
				
				this.__status = Status.ERROR;
				
				while (this.__status == Status.ERROR)
					try {
						Thread.sleep(1);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			}
		}
		
		//stop all the tiles
		for (int i = 0; i < __amount; i++)
			__images.get(i).stop();
		
		this.__subthreads.shutdown();
	}

}
