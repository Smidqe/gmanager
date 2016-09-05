package application.types;

import application.images.image.TImage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TImageContainer {
	private TImage image;
	private ImageView container;
	
	public TImageContainer(TImage image, ImageView container)
	{
		this.image = image;
		this.container = container;
	}
	
	public void setImage(TImage image)
	{
		this.image = image;
	}
	
	public void setContainer(ImageView view)
	{
		this.container = view;
	}
	
	public ImageView getContainer()
	{
		return this.container;
	}
	
	public TImage getImage()
	{
		return this.image;
	}
	
	public void arm(boolean show)
	{
		if (this.image == null || this.container == null)
			return;
		
		System.out.println("Arming");
		
		this.container.setImage(new Image(show ? this.image.getThumbnailURL() : null, true));
	}
}
