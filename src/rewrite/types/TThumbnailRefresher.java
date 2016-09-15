package rewrite.types;

/*
 *   TODO:
 *   	- Make this into constantly updating (whenever we scroll, so that we don't need to continuesly create a new one)
 *   		- This should be easy, but where should we put the wait?
 *  	
 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
	private DoubleProperty scrollPosition;
	private ChangeListener<Number> __listener;
	
	public static TThumbnailRefresher instance()
	{
		return __self;
	}
	
	private TThumbnailRefresher() 
	{
		this.hidden = new ArrayList<Node>();
		this.showing = new ArrayList<Node>();
	}

	public void bind(TTileManager manager, DoubleProperty doubleProperty)
	{
		this.manager = manager;
		this.scrollPosition = doubleProperty;
		
		__listener = new ChangeListener<Number>()
		{

			@Override
			public synchronized void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				notify();
			}
		};

		scrollPosition.addListener(__listener);
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
	
	public void scan()
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
			
			if (hidden && ((ImageView) node).getImage() == null)
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
			images.add(executor.submit(new TImageLoader(manager.getContainerByNode(nodes.get(i)).getImage(), "thumb_small")));

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

	public void stop()
	{
		this.stop = true;
	}
	
	@Override
	public void run() 
	{

		
		while (!stop)
		{
			System.out.println("Scroll position: " + scrollPosition.doubleValue());

			scan();
			
			System.out.println("Refresher - Amount of tiles showing(previously hidden): " + this.showing.size());
			System.out.println("Refresher - Amount of tiles hidden(previously showing): " + this.hidden.size());
			
			load(this.showing);
			release(this.hidden);
			
			try {
				synchronized (__listener) {
					__listener.wait();
				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("Refresher was notified.");
		}
	}
}
