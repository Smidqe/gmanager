package application.images;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import application.globals.constants;
import application.types.TImage;
//import files.TIni;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;

/*
	TODO:
		- Ini files will hold the 'keys' that we need. 
			- FULL, THUMBNAIL, SOURCE, URL, something else?
			- New class, probably enum.
 */


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
	
	public void clearDoubles()
	{
		ArrayList<TImage> filtered = new ArrayList<TImage>();
		for (TImage image : images)
			append(filtered, image, !filtered.contains(image));
		
		this.images = filtered;
	}
	
	private void append(ArrayList<TImage> __images, TImage img, boolean append)
	{
		if (!append)
			return;
		
		__images.add(img);
	}
	
	public void append(TImage image)
	{
		append(images, image, !images.contains(image));
	}
	
	public void append(ArrayList<TImage> __images)
	{
		if (__images == null)
			return;
		
		for (TImage img : __images)
			append(images, img, !images.contains(img));
	}

	public void append(JSONObject json)
	{
		
	}
	
	public ArrayList<TImage> create(Map<String, Object> values)
	{
		/*
			Need to grab right values
				- Ultimately they are in ini files.
				- 
		
		 */
		//TIni ini = new TIni(null, false);
		for (int i = 0; i < values.size(); i++)
		{
			//TImage img = new TImage();
			
			//img.setImage((Arrayvalues.get("images").);
		}
		
		return null;
		
	}
	
	public void populate(TilePane tiles, boolean append)
	{
		if (!append)
		{
			tiles.getChildren().clear();
			
			for (TImage image : images)
				tiles.getChildren().add(new ImageView(image.URL()));
		
			return;
		}
		
		ImageView last = (ImageView) tiles.getChildren().get(tiles.getChildren().size());
		
		int index = images.indexOf(last.getImage());
		
		if (index != -1)
			;
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

	@SuppressWarnings("unchecked")
	public String getThumbnail(int index)
	{
		switch	(this.current_site)
		{
			case constants.__INDEX_SITE_DERPIBOORU: return (String) ((HashMap<String, Object>) this.images.get(index).getInfo().get("representations")).get("thumb_small");
			case constants.__INDEX_SITE_IMGUR: return "";
		}
		
		return null;
	}
	
	public List<Integer> size(int index)
	{
		switch (this.current_site)
		{
			case constants.__INDEX_SITE_DERPIBOORU: case constants.__INDEX_SITE_IMGUR:
				return Arrays.asList((int) this.images.get(index).width(), (int) this.images.get(index).height());
		}
		
		return null;
	}
	
	public String getImageURL(int index)
	{
		String __id = null;
		
		switch (this.current_site)
		{
			case constants.__INDEX_SITE_IMGUR: __id = "link"; break;
			case constants.__INDEX_SITE_DERPIBOORU: __id = "image"; break;
		}
		
		return (__id != null) ? (String) this.images.get(index).getInfo().get(__id) : null;
	}
	
	
}
