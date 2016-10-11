package application.types;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;

import javafx.scene.image.Image;

public class TImageURLLoader implements Callable<Image>{

	private URL url;
	
	public TImageURLLoader(URL url) 
	{
		this.url = url;
	}
	
	private Image load() throws IOException
	{
		if (this.url.openConnection().getContentLength() < 2)
			throw new IOException();
		
		
		return null;
	}
	
	@Override
	public Image call() throws Exception {
		return load();
	}

}
