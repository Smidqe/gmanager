package application.types.custom.gallery;

import javafx.geometry.BoundingBox;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;

public class TViewport {

	private TilePane __tiles;
	private ScrollPane __scroll;
	
	public TViewport(TilePane tiles, ScrollPane scroll) 
	{
		// TODO Auto-generated constructor stub
		__scroll = scroll;
		__tiles = tiles;
	}
	
	
	public BoundingBox getViewportLocation()
	{
		double __value = (__tiles.getHeight() - __scroll.getViewportBounds().getHeight()) * __scroll.getVvalue();
		return new BoundingBox(0, __value, __scroll.getWidth(), __scroll.getViewportBounds().getHeight());
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
	
	public boolean intersects(Node node)
	{
		return getViewportLocation().intersects(node.getBoundsInParent());
	}
	
}
