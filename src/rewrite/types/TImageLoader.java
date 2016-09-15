package rewrite.types;

import java.util.concurrent.Callable;

import javafx.scene.image.Image;
import rewrite.types.TImage.enum_map;

/*
 	
 */


public class TImageLoader implements Callable<Image>
{
	private TImage image;
	private String version;
	
	public TImageLoader(TImage container, String version)
	{
		this.image = container;
		this.version = version;
	}
	
	private Image load(String URL)
	{
		return new Image(URL, 150, 150, true, false, true);
	}
	
	private Image load()
	{
		return load(image.getProperty(enum_map.MAP_IMAGES, version));
	}
	

	@Override
	public Image call() throws Exception {
		return load();
	}
}
