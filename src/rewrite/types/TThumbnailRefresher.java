package rewrite.types;

/*
 *   TODO:
 *   	- Make this into constantly updating (whenever we scroll, so that we don't need to continuesly create a new one)
 *   		- This should be easy, but where should we put the wait?
 *  	
 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
	private boolean stop = false;

	private ExecutorService __service;
	private BlockingDeque<String> queue;
	
	public static TThumbnailRefresher instance()
	{
		return __self;
	}
	
	private TThumbnailRefresher() 
	{
		this.hidden = new ArrayList<Node>();
		this.showing = new ArrayList<Node>();
	}

	public void setDeque(BlockingDeque<String> deque)
	{
		this.queue = deque;
	}
	
	public void bind(TTileManager manager)
	{
		this.manager = manager;
	}
	
	public BoundingBox getViewportBoundsOnParent(ScrollPane viewport, TilePane pane)
	{
		//double __value = pane.getHeight() * viewport.getVvalue() - viewport.getViewportBounds().getHeight() * viewport.getVvalue();
		double __value = (pane.getHeight() - viewport.getViewportBounds().getHeight()) * viewport.getVvalue();
		
		return new BoundingBox(0, __value, viewport.getWidth(), viewport.getViewportBounds().getHeight());
	}
	
	public boolean inViewport(ScrollPane viewport, TilePane pane, Node node)
	{
		return getViewportBoundsOnParent(viewport, pane).intersects(node.getBoundsInParent());
	}
	
	private void release(List<Node> nodes)
	{
		if (nodes.size() == 0)
			return;
		
		for (Node node : nodes)
			((ImageView) node).setImage(null);
	}
	
	public void scan()
	{
		//System.out.println("Refresher - Scanning");
		
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
			
			if (hidden && ((ImageView) node).getImage() == null)
				continue;
			
			if (hidden)
				this.hidden.add(node);
			else
				this.showing.add(node);
		}
		
		//System.out.println("Ending scan");
	}
	
	//move this one to a different thread
	private void load(List<Node> nodes) throws Exception
	{
		if (nodes.size() == 0)
			return;

		List<Image> images = new ArrayList<Image>();
		List<TImage> containers = new ArrayList<TImage>();
		
 		for (Node node : nodes)
			containers.add(manager.getContainerByNode(node).getImage());

 		images = __service.submit(new TImageLoader(containers, "thumb_small")).get();

 		if (images == null)
 			return;
 		
		for (int i = 0; i < nodes.size(); i++)
			((ImageView) nodes.get(i)).setImage(images.get(i));
	}

	public List<Node> getShown()
	{
		return this.showing;
	}
	
	public List<Node> getHidden()
	{
		return this.hidden;
	}
	
	public void stop()
	{
		this.stop = true;
	}
	
	@Override
	public synchronized void run() 
	{
		__service = Executors.newCachedThreadPool(r -> {
	        Thread t = new Thread(r);
	        t.setDaemon(true);
	        return t;
		});
		
		while (!stop)
		{
			scan();

			//will notify image loaders that they can now run.
			notify();
			
			//move these to different threads
			try {
				load(this.showing);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			release(this.hidden);

			
			synchronized (queue) {
				try {
					queue.take();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		__service.shutdown();
		
		System.out.println("TThumbnailRefresher - Shutting down");
	}
}
