package application.types;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import application.types.TImage.Maps;
import application.types.custom.TGallery;
import application.types.custom.TGallery.Action;
import application.types.images.container.TImageContainer;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.TilePane;

public class TTileManager

{
	private List<TImageContainer> images = new ArrayList<TImageContainer>();
	private TilePane __gallery;
	private TCacheManager __saver;
	private ExecutorService __executor;

	//used for allowing refreshing to happen, instead of notify
	private BlockingDeque<TGallery.Action> __refresher_deque;
	
	public TTileManager(TilePane node, BlockingDeque<Action> __action_deque, TImageSaver saver)
	{
		this.__gallery = node;
		this.__refresher_deque = __action_deque;
		this.__saver = TCacheManager.instance();
		this.__executor = Executors.newSingleThreadExecutor();
	}
	
	public synchronized void add(TImageContainer image, boolean forceRefresh) throws Exception
	{
		if (image.getImageContainer() == null)
		{
			System.out.println(Thread.currentThread().getName() + " - Null imageview");
			return;
		}
		
		image.setSize("thumb_small");
		image.show(true);
		
		//remove this eventually
		__executor.submit(image).get();

		this.images.add(image);
		this.__gallery.getChildren().add(image.getImageView());

		if (forceRefresh)
			__refresher_deque.put(TGallery.Action.REFRESHER);
	}
	
	public synchronized void add(List<TImageContainer> list) throws Exception
	{
		if (list == null)
			throw new NullPointerException();

		for (TImageContainer container : list)
			add(container, false);
		
		__refresher_deque.put(TGallery.Action.REFRESHER);
	}

	public TilePane getTilePane()
	{
		return this.__gallery;
	}
	
	public List<TImageContainer> getImages()
	{
		return this.images;
	}

	public synchronized TImageContainer getContainerByNode(Node node)
	{
		if (node == null)
			return null;
		
		TImageContainer container = null;
		
		List<Node> nodes = getTilePane().getChildren();
		for (int i = 0; i < nodes.size(); i++)
		{
			container = (node.equals(nodes.get(i))) ? images.get(i) : null;
		
			if (container != null)
				break;
		}	
		
		return container;
	}

	public synchronized List<Image> getThumbnails()
	{
		List<TImageContainer> containers = getImages();
		List<Image> imgs = new ArrayList<Image>();
		
		containers.forEach(c -> imgs.add(c.getImageView().getImage()));
		return imgs;
	}
	
	public synchronized int getNullImageCount()
	{
		int count = 0;
		
		for (Image img : getThumbnails())
			count += (img == null) ? 1 : 0;
		
		return count;
	}
	
	public synchronized BlockingDeque<TGallery.Action> getDeque() 
	{
		return this.__refresher_deque;
	}
}
