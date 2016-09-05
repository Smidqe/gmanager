package application.types;

import application.images.image.TImage2;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TImageContainer2 {
	private TImage2 image;
	private ImageView container;
	
	public TImageContainer2(TImage2 image, ImageView container)
	{
		this.image = image;
		this.container = container;
	}

	public ImageView getContainer()
	{
		return this.container;
	}
	
	public TImage2 getImage()
	{
		return this.image;
	}
	
	public void arm(boolean show, boolean thumbnail)
	{
		if (this.image == null || this.container == null)
			return;
		
		System.out.println("Arming");
		
		this.container.setImage(new Image(show ? this.image.getProperty("url_thumbnail") : null, true));
	}
}
