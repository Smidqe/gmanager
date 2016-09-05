package application.images.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.WeakHashMap;

import org.json.simple.JSONObject;

import application.data.settings.settings;
import application.images.image.TImage;
import application.parsers.parsers;
import application.types.TImageContainer;
import application.types.TUtilities;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;



public class manager extends Observable implements Runnable{
	//contains the identifiers used in ini file for easy finding.
	public enum imageID {
		FULL("image"), //the full image url
		THUMBNAIL("thumbnail"), //thumbnail url
		WIDTH("width"), //image width
		HEIGHT("height"), //image height
		SIZE("size"), //image size
		SOURCE("source"), //image source if exists
		NAME("name"), //name or id ???
		DESCRIPTION("description"), //if there is any description
		TYPE("type"), //type or mime_type
		FAVORITES("favorites"), //amount of faves
		UPVOTES("upvotes"); //amount of upvotes/score
		
		String ID;
		
		imageID(String str) 
		{
			ID = str;
		}
		
		String getID()
		{
			return ID;
		}
	};
	
	public enum siteID
	{
		__DERPIBOORU("DERPIBOORU"),
		__IMGUR("IMGUR");
		
		String ID;
		
		siteID(String str) 
		{
			ID = str;
		}
		
		String getID()
		{
			return ID;
		}
	}
	
	private ArrayList<TImageContainer> __containers;
	
	private static manager self = new manager();
	
	private siteID site;
	private settings __settings;
	private TImage last;
	private boolean interrupted;
	private TilePane __pane;

	public static manager instance()
	{
		return self;
	}
	
	protected manager()
	{
		__settings = settings.instance();
		__containers = new ArrayList<TImageContainer>();
	}
	
	public void setTilePane(TilePane tiles)
	{
		this.__pane = tiles;
	}
	
	public void setCurrentSite(siteID site)
	{
		this.site = site;
	}
	
	private void append(TImageContainer img, boolean append)
	{
		if (!append)
			return;
		
		__containers.add(img);
	}
	
	public void append(TImageContainer image)
	{
		append(image, !__containers.contains(image));
	}
	
	public void append(List<TImageContainer> __images)
	{
		if (__images == null)
			return;
		
		for (TImageContainer img : __images)
			append(img, !__containers.contains(img));
	}
	
	public void append(JSONObject json) throws IOException
	{
		if (json == null)
			return;
		
		List<TImageContainer> created = new ArrayList<TImageContainer>();

		switch(this.site)
		{
			case __DERPIBOORU :
			case __IMGUR:
				created = create(parsers.parse_db(json)); break;
			default:
				break;
		}

		created = TUtilities.clearDuplicates(created);
		
		this.__containers.addAll(created);
	}

	public void getNotVisibleTiles()
	{
	}
	/*
		public void refreshTiles()
	{
		if (this.images.size() == this.tiles.size())
			return;
		
		int index = 0;
		
		//refactor this one, possibly the ranges??
		if (this.tiles.size() > 0)
			index = this.tiles.size();

		for (int i = index; i < this.images.size(); i++)
			this.tiles.add(new ImageView(new Image(this.images.get(i).getThumbnailURL(), true)));

		synchronized (this)
		{
			__pane.getChildren().addAll(this.tiles.subList(index, this.tiles.size()));
		}
	}
	 */
	public void refreshTiles()
	{
		if (this.__containers.size() == 0)
			return;

		int index = 0;

		if (__pane.getChildren().size() != 0)
		{			
			ImageView last = (ImageView) __pane.getChildren().get(__pane.getChildren().size() - 1);
			for (int i = 0 ; i < size(); i++)
				if (__containers.get(i).getContainer().equals(last))
				{
					index = ++i;
					break;
				}
		}

		System.out.println(index);
		System.out.println(__containers.size());
		
		for (int i = index; i < size(); i++)
			__pane.getChildren().add(__containers.get(i).getContainer());
	}
	
	public int size()
	{
		return this.__containers.size();
	}
	
	public ArrayList<TImageContainer> getImages()
	{
		return this.__containers;
	}
	
	private String getIdentifier(imageID id)
	{
		return __settings.getIni(settings.__ID_FILE_SETTINGS_IMAGES).entry(site.getID(), id.getID());
	}
	
	@SuppressWarnings("unchecked")
	public List<TImageContainer> create(List<Map<String, Object>> values) throws IOException
	{
		ArrayList<TImageContainer> list = new ArrayList<TImageContainer>();
		for (int i = 0; i < values.size(); i++)
		{
			TImageContainer img = new TImageContainer(new TImage(), new ImageView());

			img.getImage().setName((String) values.get(i).get(getIdentifier(imageID.NAME)));
			img.getImage().setWidth((long) values.get(i).get(getIdentifier(imageID.WIDTH)));
			img.getImage().setHeight((long) values.get(i).get(getIdentifier(imageID.HEIGHT)));
			//img.setFaves((long) values.get(i).get(getIdentifier(imageID.FAVORITES)));
			img.getImage().setSource((String) values.get(i).get(getIdentifier(imageID.SOURCE)));
			img.getImage().setType((String) values.get(i).get(getIdentifier(imageID.TYPE)));
			img.getImage().setImage("https:" + (String) values.get(i).get(getIdentifier(imageID.FULL)));		
			img.getImage().setThumbnailURL("https:" + (String) ((WeakHashMap<String, Object>) values.get(i).get("representations")).get(getIdentifier(imageID.THUMBNAIL)));

			//Filter the gifs for now, until I figure out a way to recreate the gifs from stream. (JavaFX has a bug where it fails to create GIFs)
			if (!img.getImage().getType().equals("image/gif"))
			{
				img.arm(true);
				list.add(img);
			}
		}

		return list;
		
	}

	public TImage getLast() {
		return this.last;
	}
	
	public void setLast(TImage image)
	{
		System.out.println(this.__containers);
		
		this.last = image;
	}

	public void stop()
	{
		this.interrupted = true;
	}
	
	@Override
	public void run() {
		while (!interrupted)
		{
		}
	}

	public void load(int index)
	{
		TImageContainer c = __containers.get(index);
		
		if (c.getContainer().getImage() != null)
			return;
		
		c.getContainer().setImage(new Image(c.getImage().getThumbnailURL(), true));
	}
	
	public TImageContainer index(ImageView node) 
	{
		for (TImageContainer container : __containers)
			if (container.getContainer().equals(node))
				return container;

		return null;
	}
}
