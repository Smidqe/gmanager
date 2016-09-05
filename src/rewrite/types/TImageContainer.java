package rewrite.types;

import rewrite.types.TImage;
import rewrite.types.TImage.enum_map;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import application.types.TIDCreator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TImageContainer implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5108226962075856755L;
	private TImage image;
	private transient ImageView container;
	
	private transient String UUID;
	private boolean visible;
	
	public TImageContainer(TImage image, ImageView container) throws InterruptedException, ExecutionException
	{
		this.image = image;
		this.container = container;
		
		this.UUID = Executors.newSingleThreadExecutor().submit(new TIDCreator<>(this)).get();
	
		System.out.println(Thread.currentThread().getName() + " - Created a new TImageContainer");
	}

	public ImageView getContainer()
	{
		System.out.println(Thread.currentThread().getName() + " - Accessing getContainer");

		
		return this.container;
	}
	
	public TImage getImage()
	{
		return this.image;
	}
	
	public String getUUID()
	{
		return this.UUID;
	}
	
	public boolean visible()
	{
		return this.visible;
	}
	
	public void arm(boolean show, String size)
	{
		if (this.image == null || this.container == null)
			return;
		
		System.out.println("Arming");
		
		this.container.setImage(new Image(show ? this.image.getProperty(enum_map.MAP_IMAGES, size) : null, true));
		this.visible = show;
	}
}
