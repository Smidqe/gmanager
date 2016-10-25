package application.types;

import java.io.IOException;

import application.types.TImage.Maps;
import application.types.custom.TGallery;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TImageSaver implements Runnable
{
	public enum SaverStatus {IDLE, RUNNING, ERROR};
	
	private boolean __stop = false;
	private TGallery __gallery;
	private TCacheManager __manager;
	
	public TImageSaver(TGallery gallery) 
	{
		this.__gallery = gallery;
		this.__manager = TCacheManager.instance();
	}

	public void stop()
	{
		this.__stop = true;
	}
	
	@Override
	public void run() 
	{
		Image img = null;
		
		// TODO Auto-generated method stub
		while (!this.__stop)
		{
			System.out.println("Saver: Running");
			
			ObservableList<Node> nodes = __gallery.getTilePane().getChildren();

			System.out.println("Saver: Amount of nodes: " + nodes.size());
			
			for (Node node : nodes)
			{
				TImageContainer container = __gallery.getManager().getContainerByNode(node);
				
				System.out.println("Saver: Container: " + container);
				
				if (TCacheManager.instance().exists(container.getUUID()))
				{
					System.out.println("Node is already in cache system");
					continue;
				}
				
				System.out.println("Saver: Image visible: " + container.isVisible());
				
				if (container.isVisible())
					img = ((ImageView) node).getImage();
				else
					img = new Image(container.getImage().getProperty(Maps.MAP_IMAGES, "thumb_small"), 150, 150, true, false, false);
				
				try {
					__manager.saveFXImage(img, container.getUUID());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			__manager.clear();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
