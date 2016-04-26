package application.images;

import java.util.ArrayList;

import application.types.TImage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;

public class images {
	private ArrayList<TImage> images;
	private TImage last;
	private int current_site;
	private static images self = new images();
	
	public static images instance()
	{
		return self;
	}
	
	protected images()
	{
		this.images = new ArrayList<TImage>();
	}
	
	public void append(ArrayList<TImage> __images)
	{
		
	}

	
	public void populate(TilePane tiles, boolean append)
	{
		if (!append)
		{
			tiles.getChildren().clear();
			
			for (TImage image : images)
				tiles.getChildren().add(new ImageView((Image) image));
		
			return;
		}
		
		
	}
	
	public void clear()
	{
		this.images.clear();
	}
	
	public void setLast(TImage image)
	{
		this.last = image;
	}
	
	public TImage getLast()
	{
		return this.last;
	}
	
	public ArrayList<TImage> getImages()
	{
		return this.images;
	}

}
