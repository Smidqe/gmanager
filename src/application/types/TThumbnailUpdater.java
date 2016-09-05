package application.types;

import java.net.MalformedURLException;
import java.util.ArrayList;

import javafx.geometry.BoundingBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import application.web.TImageDownload;

//rework this class before rolling it out to the program, seriously needs it!
@SuppressWarnings("unused")
public class TThumbnailUpdater implements Runnable{
	private boolean interrupted;
	
	private boolean useCache;
	private ArrayList<TImageContainer> containers;
	
	private TCache cache;
	private ScrollPane viewport;
	private TilePane tiles;
	
	public TThumbnailUpdater()
	{
		this.containers = new ArrayList<TImageContainer>();
	
		this.interrupted = false;
		this.cache = TCache.instance();
		this.useCache = false;
	}
	
	public void setViewport(ScrollPane pane)
	{
		this.viewport = pane;
	}
	
	public void setContainer(ArrayList<TImageContainer> image)
	{
		this.containers = image;
	}

	public void useCache(boolean value)
	{
		this.useCache = value;
	}
	
	public void load(String ID)
	{	
		for (TImageContainer container : containers)
		{
			if (container.getImage().getThumbnail() != null)
				return;
			
			if (this.useCache)
				container.getImage().setThumbnail((Image) cache.get(container.getImage().getID()));
			else
			{
				try {
					TImageDownload download = new TImageDownload(container.getImage().getThumbnailURL());
					Thread __thread = new Thread(download);
					
					__thread.start();
					__thread.join();
					
					container.getImage().setThumbnail(download.getImage());
				} catch (MalformedURLException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	}
	
	private boolean inViewport(TImageContainer tile)
	{
		double __value = tiles.getHeight() * viewport.getVvalue() - viewport.getViewportBounds().getHeight() * viewport.getVvalue();

		return new BoundingBox(0, __value, viewport.getWidth(), __value + viewport.getHeight()).intersects(tile.getContainer().getBoundsInParent());
	}
	
	public void show()
	{
		for (TImageContainer container : containers)
		{
			if (!inViewport(container))
				continue;

			if (container.getImage().getThumbnail() == null)
				load(container.getImage().getID());
			
			container.arm(true);
		}
	}

	public void hide()
	{
		for (TImageContainer container : containers)
		{
			if (inViewport(container))
				continue;
			
			container.arm(false);
		}
	}
	
	public void terminate()
	{
		this.interrupted = true;
	}

	@Override
	public void run() 
	{
		System.out.println("Thumbnail updater started");
		
		
		synchronized (this)
		{
			while (!interrupted)
			{		
				show();
				hide();
				
			}
		}
	}
}
