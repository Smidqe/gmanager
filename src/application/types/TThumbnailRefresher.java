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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
		System.out.println("Refresher - Scanning");
		
		List<Node> nodes = __gallery.getTilePane().getChildren();
		
		System.out.println("Refresher - Got children");
		
		if (nodes.size() < 15)
		{
			System.out.println("Refresher - No nodes got");
			return;
		}
		System.out.println("Refresher - Got nodes");
		System.out.println(nodes);
		
		if (this.hidden.size() > 0)
			this.hidden.clear();
		
		if (this.showing.size() > 0)
			this.showing.clear();

		System.out.println("Refresher - Arrays cleared");
		boolean hidden = false;
		for (Node node : nodes)
		{	
			hidden = (!inViewport(node));
			
			if (node == null)
				continue;
			
			System.out.println("Checking if not hidden");
			if (!hidden && (((ImageView) node).getImage() != null))
			{
				System.out.println("Does this finish?");
				continue;
			}
			System.out.println("Checking if hidden");
			if (hidden && (((ImageView) node).getImage() == null))
				continue;
			
			System.out.println("Adding to corresponding arrays");
			if (hidden)
				this.hidden.add(node);
			else
				this.showing.add(node);
		}

		System.out.println("Hidden: Amount: " + this.hidden.size());
		System.out.println("Showing: Amount: " + this.showing.size());
	}
	
	//move this one to a different thread
	private void load(List<Node> nodes) throws Exception
	{
		if (nodes.size() == 0)
			return;

 		for (Node node : nodes)
			__gallery.getManager().getContainerByNode(node).arm(true, "thumb_small");
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
				System.out.println("Refresher - Starting loading/offloading");
				
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
		
		System.out.println("TThumbnailRefresher - Shutting down");
	}
}
