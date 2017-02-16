package application.types.custom.gallery;

import javafx.geometry.BoundingBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;

/*
	TODO:
		- Turn this into a singleton, there is no need for multiple viewports unless I make a tabbed image searches.
			- Even if I do that something needs to be rewritten
 */

public class TViewport 
{

	private TilePane __tiles;
	private ScrollPane __scroll;
	
	public TViewport(TilePane tiles, ScrollPane scroll) 
	{
		__scroll = scroll;
		__tiles = tiles;
	}
	
	
	public BoundingBox getViewportLocation()
	{
		double __value = (__tiles.getHeight() - __scroll.getViewportBounds().getHeight()) * __scroll.getVvalue();
		BoundingBox __bounds = new BoundingBox(0, __value, __scroll.getWidth(), __scroll.getViewportBounds().getHeight());
		
		return __bounds;
	}
	
	public BoundingBox getViewportBounds()
	{
		return (BoundingBox) __scroll.getViewportBounds();
	}

	public TilePane getTilePane()
	{
		return this.__tiles;
	}
	
	public ScrollPane getScrollPane()
	{
		return this.__scroll;
	}
	
	public int getTilesInRow()
	{
		return (int) Math.floor(__scroll.getWidth() / 150);
	}
	
	public boolean intersects(BoundingBox box)
	{
		return getViewportLocation().intersects(box);
	}
}
