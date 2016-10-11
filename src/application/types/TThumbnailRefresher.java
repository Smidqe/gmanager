package application.types;

/*
 *   TODO:
 *   	- Clean
 *   	- Branch the image loading to separate threads instead of doing it on this thread
 *   		- This should be relatively easy, need to create a new loader probably, or I can create it on this thread...
 *  	
 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import application.types.custom.TGallery;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TThumbnailRefresher implements Runnable
{
	private TGallery __gallery;
	private List<Node> hidden, showing;
	private boolean stop = false;

	private ExecutorService __service;
	private BlockingDeque<String> __deque;
	
	public TThumbnailRefresher(TGallery manager, BlockingDeque<String> deque) 
	{
		this.hidden = new ArrayList<Node>();
		this.showing = new ArrayList<Node>();
		
		this.__gallery = manager;
		this.__deque = deque;
	}

	public boolean inViewport(Node node)
	{
		return this.__gallery.getViewportLocation().intersects(node.getBoundsInParent());
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
		System.out.println("Refresher - Scanning");
		
		List<Node> nodes = __gallery.getTilePane().getChildren();
		
		if (nodes.size() == 0)
			return;
		
		this.hidden.clear();
		this.showing.clear();

		for (Node node : nodes)
		{
			boolean hidden = (!inViewport(node));
			
			if (!hidden && ((ImageView) node).getImage() != null)
				continue;
			
			if (hidden && ((ImageView) node).getImage() == null)
				continue;
			
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

		List<Image> images = new ArrayList<Image>();
		List<TImage> containers = new ArrayList<TImage>();
		
 		for (Node node : nodes)
			containers.add(__gallery.getManager().getContainerByNode(node).getImage());

 		//rewrite this section, currently acts as a bottle neck.
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

			
			synchronized (this.__deque) {
				try {
					this.__deque.take();
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
