package rewrite.types;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.TilePane;

public class TTileManager 
{
	private static volatile TTileManager instance = new TTileManager();
	private List<TImageContainer> images = new ArrayList<TImageContainer>();
	private TilePane pane;
	private ScrollPane parent;
	
	private TTileManager()
	{
		this.pane = null;
		this.parent = null;
	}
	
	public void bind(TilePane pane, ScrollPane parent)
	{
		this.pane = pane;
		this.parent = parent;
	}
	
	public synchronized void add(TImageContainer image)
	{
		if (image.getContainer() == null)
		{
			System.out.println(Thread.currentThread().getName() + " - Null imageview");
			return;
		}
		
		this.images.add(image);
		this.pane.getChildren().addAll(image.getContainer());
		
		notify();
	}
	
	public synchronized void add(List<TImageContainer> list)
	{
		if (list == null)
			throw new NullPointerException();

		for (TImageContainer container : list)
			add(container);

		notify();
	}

	public TilePane getPane()
	{
		return this.pane;
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
		
		List<Node> nodes = pane.getChildren();
		for (int i = 0; i < nodes.size(); i++)
		{
			container = (node.equals(nodes.get(i))) ? images.get(i) : null;
		
			if (container != null)
				break;
		}	
		
		return container;
	}

	public ScrollPane getParent() {
		return this.parent;
	}

	public static TTileManager instance() {
		return instance;
	}

}
