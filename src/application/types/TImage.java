package application.types;

import javafx.scene.image.Image;

public class TImage extends Image{
	private String url;
	
	public TImage(String url) {
		super(url);
		// TODO Auto-generated constructor stub
		
		this.url = url;
	}

	public String getURL()
	{
		return this.url;
	}
}
