package rewrite.types;

import rewrite.types.TImage;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import application.types.TIDCreator;
import javafx.scene.image.ImageView;

public class TImageContainer implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5108226962075856755L;
	private TImage image;
	private transient ImageView container;
	
	private String UUID;
	private boolean visible;
	
	public TImageContainer(TImage image, ImageView container) throws InterruptedException, ExecutionException
	{
		this.image = image;
		this.container = container;
		
		this.UUID = Executors.newSingleThreadExecutor().submit(new TIDCreator<>(this)).get();
	}

	public ImageView getContainer()
	{
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
	
	public boolean isVisible()
	{
		return this.visible;
	}
	
	public void arm(boolean show, String size) throws InterruptedException, ExecutionException
	{
		if (this.image == null || this.container == null)
			return;
		
		//System.out.println("Arming");
		
		if (show)
			this.container.setImage(Executors.newSingleThreadExecutor().submit(new TImageLoader(this.image, size)).get().get(0));
		else
			this.container.setImage(null);

		this.visible = show;
	}
}
