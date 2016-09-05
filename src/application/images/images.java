package application.images;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import application.data.resources.globals.globals;
import application.images.image.TImage;
import application.parsers.parsers;
import application.web.connections.connections;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;

/*
	TODO:
		- Ini files will hold the 'keys' that we need. 
			- FULL, THUMBNAIL, SOURCE, URL, something else?
			- New class, probably enum.
 */


public class images {
	public static final int __ID_IMAGE_SIZE_FULL = 0; //the full image url
	public static final int __ID_IMAGE_SIZE_THUMBNAIL = 1; //thumbnail url
	public static final int __ID_IMAGE_SIZE_WIDTH = 2; //image width
	public static final int __ID_IMAGE_SIZE_HEIGHT = 3; //image height
	public static final int __ID_IMAGE_SIZE = 4; //image size
	public static final int __ID_IMAGE_SOURCE = 5; //image source if exists
	public static final int __ID_IMAGE_NAME = 6; //name or id ???
	public static final int __ID_IMAGE_DESCRIPTION = 7; //if there is any description
	public static final int __ID_IMAGE_TYPE = 8; //type or mime_type
	public static final int __ID_IMAGE_FAVORITES = 9; //amount of faves
	public static final int __ID_IMAGE_UPVOTES = 10; //amount of upvotes/score

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
		
		this.images.clear();
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
		case globals.__INDEX_SITE_DERPIBOORU : 
			this.images.addAll(create_db(parsers.parse_db(json))); break;
		}
		
		
	}
	
	public void offloadNotVisible()
	{
		
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
			case __ID_IMAGE_SIZE_FULL:
			{
				switch (site)
				{
					case globals.__INDEX_SITE_DERPIBOORU: return "image";
					case globals.__INDEX_SITE_IMGUR: return "link";
				}
			}
			
			case __ID_IMAGE_SIZE_THUMBNAIL:
			{
				switch (site)
				{
					case globals.__INDEX_SITE_DERPIBOORU: return "thumb_small";
					case globals.__INDEX_SITE_IMGUR: return "";
				}
			}
			
			//move these under sub switches once a new site is added! TODO
			case __ID_IMAGE_SIZE_WIDTH: return "width";
			case __ID_IMAGE_SIZE_HEIGHT: return "height";
			case __ID_IMAGE_SIZE: return (site == globals.__INDEX_SITE_DERPIBOORU ? "" : "size");
			case __ID_IMAGE_SOURCE: return (site == globals.__INDEX_SITE_DERPIBOORU ? "source_url" : "");
			case __ID_IMAGE_NAME: return "id";
			case __ID_IMAGE_DESCRIPTION: return "description";
			case __ID_IMAGE_TYPE: return (site == globals.__INDEX_SITE_DERPIBOORU ? "mime_type" : "type");
			case __ID_IMAGE_FAVORITES: return (site == globals.__INDEX_SITE_DERPIBOORU ? "faves" : ""); 
			case __ID_IMAGE_UPVOTES: return (site == globals.__INDEX_SITE_DERPIBOORU ? "upvotes" : "");
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<TImage> create_db(List<Map<String, Object>> values) throws IOException
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
			
			img.setName((String) values.get(i).get(getID(this.current_site, __ID_IMAGE_NAME)));
			img.setWidth((long) values.get(i).get(getID(this.current_site, __ID_IMAGE_SIZE_WIDTH)));
			img.setHeight((long) values.get(i).get(getID(this.current_site, __ID_IMAGE_SIZE_HEIGHT)));
			img.setFaves((long) values.get(i).get(getID(this.current_site, __ID_IMAGE_FAVORITES)));
			img.setSource((String) values.get(i).get(getID(this.current_site, __ID_IMAGE_SOURCE)));
			img.setType((String) values.get(i).get(getID(this.current_site, __ID_IMAGE_TYPE)));
			img.setImage("https:" + (String) values.get(i).get(getID(this.current_site, __ID_IMAGE_SIZE_FULL)));		
			img.setThumbnailURL("https:" + (String) ((WeakHashMap<String, Object>) values.get(i).get("representations")).get(getID(this.current_site, __ID_IMAGE_SIZE_THUMBNAIL)));

			list.add(img);
		}

		return list;
		
	}
	
	public String getSite() 
	{
		switch (current_site)
		{
			case globals.__INDEX_SITE_DERPIBOORU: return "Derpibooru";
			case globals.__INDEX_SITE_IMGUR: return "Imgur";
		}
		
		return null;
	}

	public void populate(TilePane tiles, boolean append)
	{
		if (!append)
		{
			tiles.getChildren().clear();
			
			for (TImage image : images)
				tiles.getChildren().add(new ImageView(image.getImage()));
		
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

	public String getThumbnail(int index)
	{
		switch	(this.current_site)
		{
			case globals.__INDEX_SITE_DERPIBOORU: return this.images.get(index).getThumbnailURL();
			case globals.__INDEX_SITE_IMGUR: return "";
		}
		
		return null;
	}
	
	public List<Integer> size(int index)
	{
		if (this.images.size() < index)
			return null;
		
		return Arrays.asList((int) this.images.get(index).getWidth(), (int) this.images.get(index).getHeight());
	}
	
	public String getImageURL(int index)
	{
		return this.images.get(index).getImage();
	}
	
	public void setCurrentSite(int site)
	{
		this.current_site = site;
	}

	public void loadPage(int page) throws IOException, ParseException 
	{
		append(connections.getJSON(connections.getSite(globals.__INDEX_SUBSITE_DERPIBOORU_IMAGES) + getPage(page)));
	}
	
	public String getPage(int page) 
	{
		switch(current_site)
		{
			case globals.__INDEX_SUBSITE_DERPIBOORU_IMAGES:
			case globals.__INDEX_SUBSITE_DERPIBOORU_SEARCH:
			case globals.__INDEX_SUBSITE_DERPIBOORU_WATCHED:
				return "?page=" + String.valueOf(page);
		}
		
		return "";
	}
}
