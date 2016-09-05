package rewrite.types;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.geometry.BoundingBox;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;

public class TThumbnailRefresher implements Runnable
{
	private static TThumbnailRefresher __self = new TThumbnailRefresher();
	private TTileManager manager;
	private List<Node> hidden, showing;

	public static TThumbnailRefresher instance()
	{
		return __self;
	}
	
	private TThumbnailRefresher() 
	{
		this.hidden = new ArrayList<Node>();
		this.showing = new ArrayList<Node>();
	}

	public void bind(TTileManager manager)
	{
		this.manager = manager;
	}
	
	public boolean inViewport(ScrollPane viewport, TilePane pane, Node node)
	{
		double __value = pane.getHeight() * viewport.getVvalue() - viewport.getViewportBounds().getHeight() * viewport.getVvalue();

		return new BoundingBox(0, __value, viewport.getWidth(), __value + viewport.getHeight()).intersects(node.getBoundsInParent());
	}
	
	private void release(List<Node> nodes)
	{
		if (nodes.size() == 0)
			return;
		
		for (Node node : nodes)
			((ImageView) node).setImage(null);
	}
	
	private void scan()
	{
		System.out.println("Refresher - Scanning");
		
		List<Node> nodes = manager.getPane().getChildren();
		
		if (nodes.size() == 0)
			return;
		
		this.hidden.clear();
		this.showing.clear();
		
		ScrollPane parent = this.manager.getParent();
		TilePane pane = this.manager.getPane();
		
		for (Node node : nodes)
		{
			boolean hidden = (!inViewport(parent, pane, node));
			
			if (!hidden && ((ImageView) node).getImage() != null)
				continue;
			
			if (hidden)
				this.hidden.add(node);
			else
				this.showing.add(node);
		}
	}
	
	
	private void load(List<Node> nodes)
	{
		if (nodes.size() == 0)
			return;

		//create a separate thread to each node.
		ExecutorService executor = Executors.newFixedThreadPool(nodes.size());
		
		List<Future<Image>> images = new ArrayList<Future<Image>>();
		for (int i = 0; i < nodes.size(); i++)
			images.add(executor.submit(new TImageLoader(manager.getContainerByNode(nodes.get(i)), "thumb_small")));

		for (int i = 0; i < nodes.size(); i++)
			try {
				((ImageView) nodes.get(i)).setImage(images.get(i).get());
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		images.clear();
		executor.shutdown();
	}

	
	@Override
	public synchronized void run() 
	{
		scan();
		
		System.out.println("Refresher - Amount of tiles showing(previously hidden): " + this.showing.size());
		System.out.println("Refresher - Amount of tiles hidden(previously showing): " + this.hidden.size());
		
		load(this.showing);
		release(this.hidden);
	}
}
