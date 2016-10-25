package application.types;

/*
 *   TODO:
 *   	- Clean
 *   	- Branch the image loading to separate threads instead of doing it on this thread
 *   		- This should be relatively easy, need to create a new loader probably, or I can create it on this thread...
 *  	- Rewrite the scanning functionality
 *  		- Causes the refresher to get stuck upon scanning
 *  		- Also makes the gui go awry
 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutionException;

import application.extensions.arrays;
import application.types.custom.TGallery;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TThumbnailRefresher implements Runnable
{
	public enum Status {IDLE, SCANNING, RUNNING};
	
	private TGallery __gallery;
	private List<Node> hidden, showing;
	private boolean stop = false;

	private BlockingDeque<String> __deque;
	private Status __status;
	
	public TThumbnailRefresher(TGallery manager, BlockingDeque<String> deque) 
	{
		this.hidden = new ArrayList<Node>();
		this.showing = new ArrayList<Node>();
		
		this.__gallery = manager;
		this.__deque = deque;
	}

	public synchronized boolean inViewport(Node node)
	{
		return this.__gallery.getViewportLocation().intersects(node.getBoundsInParent());
	}
	
	private void release(List<Node> nodes) throws InterruptedException, ExecutionException
	{
		if (nodes.size() == 0)
			return;
		
		for (Node node : nodes)
			__gallery.getManager().getContainerByNode(node).arm(false, "");
	}
	
	public void scan()
	{
		List<Node> nodes = __gallery.getTilePane().getChildren();
		
		//if we haven't loaded the first batch.
		//TODO: Switch this to a site linked constant or perhaps something that the user can modify
		if (nodes.size() < 15)
			return;
		
		this.hidden.clear();
		this.showing.clear();
		
		boolean hidden = false;
		Image image = null;
		for (Node node : nodes)
		{
			if (node == null)
				continue;
			
			hidden = !inViewport(node);
			image = ((ImageView) node).getImage();
			
			//no reason to add those nodes that are not in viewport and not loaded, same for the opposite
			if ((hidden && (image == null)) || (!hidden && (image != null)))
				continue;
			
			this.hidden = arrays.add(this.hidden, node, hidden);
			this.showing = arrays.add(this.showing, node, !hidden);
		}
	}
	
	//move this one to a different thread?
	private void load(List<Node> nodes) throws Exception
	{
		if (nodes.size() == 0)
			return;

 		for (Node node : nodes)
			__gallery.getManager().getContainerByNode(node).arm(true, "thumb_small");
	}

	public void stop()
	{
		this.stop = true;
	}
	
	public Status getStatus()
	{
		return this.__status;
	}
	
	@Override
	public synchronized void run() 
	{
		
		while (!stop)
		{
			try {
				this.__deque.take();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			this.__status = Status.SCANNING;
			scan();

			this.__status = Status.RUNNING;
			if (this.hidden.size() > 0 || this.showing.size() > 0)
			{
				//System.out.println("Refresher - Starting loading/offloading");
				
				try {
					load(this.showing);
					release(this.hidden);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			}
			
			this.__status = Status.IDLE;
		}
		
		//System.out.println("TThumbnailRefresher - Shutting down");
	}
}
