package application.images;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import application.globals.constants;
import application.parsers.parsers;
import application.types.TImage;
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

	public void append(JSONObject json) throws IOException
	{
		if (json == null)
			return;
		
		switch(this.current_site)
		{
		case constants.__INDEX_SITE_DERPIBOORU : 
			this.images.addAll(create_db(parsers.parse_db(json))); break;
		}
		
		this.clearDoubles();
	}
	
	/*
	
	public String getID(int ID)
	{
		switch(this.current_site)
		{
			case __INDEX_SITE_DERPIBOORU: section = "IMAGE_DERPIBOORU"; break;
		}
		
		return settings.images.get(section, ID);
	}
	
	 */
	public String getID(int site, int ID)
	{
		switch (ID)
		{
			case constants.__ID_IMAGE_SIZE_FULL:
			{
				switch (site)
				{
					case constants.__INDEX_SITE_DERPIBOORU: return "image";
					case constants.__INDEX_SITE_IMGUR: return "link";
				}
			}
			
			case constants.__ID_IMAGE_SIZE_THUMBNAIL:
			{
				switch (site)
				{
					case constants.__INDEX_SITE_DERPIBOORU: return "thumb_small";
					case constants.__INDEX_SITE_IMGUR: return "";
				}
			}
			
			//move these under sub switches once a new site is added! TODO
			case constants.__ID_IMAGE_SIZE_WIDTH: return "width";
			case constants.__ID_IMAGE_SIZE_HEIGHT: return "height";
			case constants.__ID_IMAGE_SIZE: return (site == constants.__INDEX_SITE_DERPIBOORU ? "" : "size");
			case constants.__ID_IMAGE_SOURCE: return (site == constants.__INDEX_SITE_DERPIBOORU ? "source_url" : "");
			case constants.__ID_IMAGE_NAME: return "id";
			case constants.__ID_IMAGE_DESCRIPTION: return "description";
			case constants.__ID_IMAGE_TYPE: return (site == constants.__INDEX_SITE_DERPIBOORU ? "mime_type" : "type");
			case constants.__ID_IMAGE_FAVORITES: return (site == constants.__INDEX_SITE_DERPIBOORU ? "faves" : ""); 
			case constants.__ID_IMAGE_UPVOTES: return (site == constants.__INDEX_SITE_DERPIBOORU ? "upvotes" : "");
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<TImage> create_db(ArrayList<Map<String, Object>> values) throws IOException
	{
		ArrayList<TImage> list = new ArrayList<TImage>();
		/*
			Need to grab right values
				- Ultimately they are in ini files.
				- 
		
		 */
		for (int i = 0; i < values.size(); i++)
		{
			TImage img = new TImage();
			
			img.setName((String) values.get(i).get(getID(this.current_site, constants.__ID_IMAGE_NAME)));
			img.setWidth((long) values.get(i).get(getID(this.current_site, constants.__ID_IMAGE_SIZE_WIDTH)));
			img.setHeight((long) values.get(i).get(getID(this.current_site, constants.__ID_IMAGE_SIZE_HEIGHT)));
			img.setFavorites((long) values.get(i).get(getID(this.current_site, constants.__ID_IMAGE_FAVORITES)));
			img.setSource((String) values.get(i).get(getID(this.current_site, constants.__ID_IMAGE_SOURCE)));
			img.setType((String) values.get(i).get(getID(this.current_site, constants.__ID_IMAGE_TYPE)));
			img.setURL("https:" + (String) values.get(i).get(getID(this.current_site, constants.__ID_IMAGE_SIZE_FULL)));		
			img.setThumbnail("https:" + (String) ((HashMap<String, Object>) values.get(i).get("representations")).get(getID(this.current_site, constants.__ID_IMAGE_SIZE_THUMBNAIL)));

			list.add(img);
		}

		return list;
		
	}
	
	public String getSite() 
	{
		switch (current_site)
		{
			case constants.__INDEX_SITE_DERPIBOORU: return "Derpibooru";
			case constants.__INDEX_SITE_IMGUR: return "Imgur";
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

	public String getThumbnail(int index)
	{
		switch	(this.current_site)
		{
			case constants.__INDEX_SITE_DERPIBOORU: return this.images.get(index).thumbnail();
			case constants.__INDEX_SITE_IMGUR: return "";
		}
		
		return null;
	}
	
	public List<Integer> size(int index)
	{
		if (this.images.size() < index)
			return null;
		
		return Arrays.asList((int) this.images.get(index).width(), (int) this.images.get(index).height());
	}
	
	public String getImageURL(int index)
	{
		return this.images.get(index).URL();
	}
	
	public void setCurrentSite(int site)
	{
		this.current_site = site;
	}
}
