package application.gui.updaters;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;

public class imageUpdater implements Runnable
{
	private Thread thread;
	private TilePane tiles;
	private String name;
	private ScrollPane viewport;
	
	private boolean isInterrupted;
	
	public imageUpdater(String name, TilePane tiles, ScrollPane viewport)
	{
		this.name = name;
		this.tiles = tiles;
		this.viewport = viewport;
	}
	
	public void terminate()
	{
		this.isInterrupted = true;
	}
	
	private ArrayList<ImageView> getNotVisible()
	{
		ArrayList<ImageView> result = new ArrayList<ImageView>();
		
		for (Node node : tiles.getChildren())
			if (node.getBoundsInParent().getMaxY() < -viewport.getViewportBounds().getMinY() || node.getBoundsInParent().getMinY() > viewport.getViewportBounds().getMaxY())
				result.add((ImageView) node);
	
		
		return result;
	}

	
	@Override
	public void run() 
	{
		while (!isInterrupted)
		{
			//check if imageview is out of bounds of viewport
			//load them off if they are
			ArrayList<ImageView> hidden = getNotVisible();
			
			if (hidden.size() > 0)
				for (ImageView view : hidden)
					view.setImage(null);
			
			
		}
		
	}
	
   public void start ()
   {
      if (thread == null)
      {
         thread = new Thread (this, name);
         thread.start();
      }
   }
}
