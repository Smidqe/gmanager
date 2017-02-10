package application.types;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import application.types.custom.gallery.TViewport;
import application.types.custom.gallery.tiles.TTile;
import application.types.custom.gallery.tiles.TTile.Action;
import javafx.application.Platform;
import javafx.scene.layout.TilePane;

public class TTileManager implements Runnable
{
	public enum Status {IDLE, RUNNING, ERROR};
	
	private boolean __stop = false;
	
	private List<TTile> __images = new ArrayList<TTile>();
	private BlockingDeque<TTile> __queue = new LinkedBlockingDeque<TTile>();
	private ExecutorService __subthreads;
	
	private TilePane __tiles;
	
	private TTile __tile;
	
	public TTileManager(TViewport viewport, TilePane tiles)
	{
		this.__tiles = tiles;
		this.__subthreads = Executors.newCachedThreadPool();	
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
	
	public void remove(TTile image)
	{
		if (image == null)
			throw new NullPointerException("Parameter: image is null");
		
		int pos = __images.indexOf(image);
		
		if (pos == -1)
			return;
		
		TTile __temp = __images.get(pos);
		__temp.stop();
		__images.remove(pos);
	}
	
	public void stop() throws InterruptedException
	{
		this.__stop = true;
		this.__queue.put(new TTile(null));
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
			System.out.println("Amount of images managed: " + amount());
			
			try {
				__tile = __queue.take();
				
				if (__tile.getData() == null)
				{
					System.out.println("__tile.getData() is null"); 
					continue;
				}
				//System.out.println("__tile.id: " + __tile.getData().getProperty(Maps.DATA, "id"));
				__subthreads.submit(__tile);
				__images.add(__tile);
				
				//add it to our subthreads and to a list
				
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
				
				System.out.println(e.getMessage());
				System.out.println(__tile.toString());
			}
			
		}
	}

}
