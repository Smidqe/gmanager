package application.types;

import java.util.ArrayList;
import java.util.Observable;

import javafx.scene.layout.TilePane;
@SuppressWarnings("unused")
public class TManager extends Observable{

	private ArrayList<TImageContainer> images;
	private TilePane tiles;
	
	public TManager()
	{
		this.images = new ArrayList<TImageContainer>();
	}
	
	public void bind(TilePane pane)
	{
		this.tiles = pane;
	}
	
	
	public void add(TImageContainer container)
	{
		
	}
}
