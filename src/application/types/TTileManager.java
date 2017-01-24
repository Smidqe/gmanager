package application.types;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;

import application.types.TImage.Maps;
import application.types.custom.TGallery;
import application.types.custom.TGallery.Action;
import application.types.images.container.TImageContainer;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;

public class TTileManager implements Runnable
{
	public enum Status {IDLE, RUNNING, ERROR};
	
	private boolean __stop = false;
	private List<TImageContainer> images = new ArrayList<TImageContainer>();
	private TilePane __gallery;
	private ExecutorService __executor;
	private TCacheManager __cache;

	private BlockingDeque<TImageContainer> __images;
	//used for allowing refreshing to happen, instead of notify
	private BlockingDeque<TGallery.Action> __refresher_deque;
	private Status __status;
	
	public TTileManager(TilePane node, BlockingDeque<Action> __action_deque, TCacheManager cache)
	{
		this.__gallery = node;
		this.__refresher_deque = __action_deque;
		this.__images = new LinkedBlockingDeque<TImageContainer>();
		this.__executor = Executors.newSingleThreadExecutor();
		this.__cache = cache;
	}
	
	public synchronized void add(TImageContainer image) throws Exception
	{
		if (image == null)
			return;

		this.__images.put(image);
	}

	public synchronized void add(List<TImageContainer> list) throws Exception
	{
		if (list == null)
			throw new NullPointerException();

		for (TImageContainer container : list)
			add(container);
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

	public synchronized List<Image> getThumbnails()
	{
		List<TImageContainer> containers = getImages();
		List<Image> imgs = new ArrayList<Image>();
		
		containers.forEach(c -> imgs.add(c.getImageView().getImage()));
		return imgs;
	}
	
	public synchronized BlockingDeque<TGallery.Action> getDeque() 
	{
		return this.__refresher_deque;
	}

	
	public Status getStatus()
	{
		return this.__status;
	}
	
	public void stop()
	{
		this.__stop = true;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		TImageContainer __container = null;
		ImageView __view = null;
		Future<TResult<Image>> __result = null;
		boolean found;
		int old;
		
		while (!this.__stop)
		{
			try {
				this.__status = Status.IDLE;
				
				//System.out.println("Here");
				
				__container = __images.take();
				//set necessary variables
				
				this.__status = Status.RUNNING;
				
				if (this.__stop)
					break;
				
				//System.out.println("Something");
				
				__container.setSize("thumb_small");
				__container.show(true);

				__executor.submit(__container);
				__view = __container.getImageView();
				
				//System.out.println("We are here");
				if (__view == null)
					System.out.println("__view == null");
				
				while (__view.getImage() == null || __view.getImage().getProgress() != 1)
					Thread.sleep(1);

				if (!__container.getImageContainer().getProperty(Maps.MAP_PROPERTIES, "original_format").equals("gif"))
				{
					System.out.println("__view.getImage(): " + __view.getImage());
					
					__cache.save(__view.getImage(), __container.getImageContainer().getProperty(Maps.MAP_PROPERTIES, "id"), __container.getImageContainer().getProperty(Maps.MAP_PROPERTIES, "original_format"));
	
					//System.out.println("Done saving hooefully");
					
					//wait for the caching thing to finish
					__result = null;
					found = false;
				
					synchronized (__cache.lock) {
						__cache.lock.wait();
					}
					
					old = __gallery.getChildren().size();
					while (!found)
					{
						for (String ID : __cache.getCurrentJobs().keySet())
							if (ID.equals(__container.getImageContainer().getProperty(Maps.MAP_PROPERTIES, "id")))
							{
								
								__result = __cache.getCurrentJobs().get(ID);
								__cache.getCurrentJobs().remove(ID);
								break;
							}
						
						if (__result != null)
						{
							System.out.println("Something");
							__result.get(); //just finish it
							
							this.images.add(__container);
							
							Platform.runLater(new Runnable() 
							{
								@Override
								public void run() 
								{
									__gallery.getChildren().add(images.get(images.size() - 1).getImageView());
								}
							});
							
							while (__gallery.getChildren().size() == old)
								Thread.sleep(1);
							
							break;
						}
					}
	
					__refresher_deque.put(Action.REFRESHER);
				}

			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		System.out.println("TTileManager: Shutting down");
	}

}
