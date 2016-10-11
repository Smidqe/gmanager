package application.types;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutionException;

import javafx.scene.Node;
import javafx.scene.layout.TilePane;

public class TTileManager 
{
	private List<TImageContainer> images = new ArrayList<TImageContainer>();
	private TilePane __gallery;

	//used for allowing refreshing to happen, instead of notify
	private BlockingDeque<String> __refresher_deque;
	
	public TTileManager(TilePane node, BlockingDeque<String> deque)
	{
		this.__gallery = node;
		this.__refresher_deque = deque;
	}

	public synchronized void add(TImageContainer image) throws InterruptedException, ExecutionException
	{
		if (image.getContainer() == null)
		{
			System.out.println(Thread.currentThread().getName() + " - Null imageview");
			return;
		}
		
		image.arm(true, "thumb_small");
		
		this.images.add(image);
		this.__gallery.getChildren().add(image.getContainer());
		
		__refresher_deque.put("");
	}
	
	public synchronized void add(List<TImageContainer> list) throws InterruptedException, ExecutionException
	{
		if (list == null)
			throw new NullPointerException();

		for (TImageContainer container : list)
			add(container);
		
		__refresher_deque.put("");
	}

	public TilePane getTilePane()
	{
		return this.__gallery;
	}
	
	public List<TImageContainer> getImages()
	{
		return this.images;
	}

	public TImageContainer getContainerByNode(Node node)
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

	public BlockingDeque<String> getDeque() 
	{
		return this.__refresher_deque;
	}
}
