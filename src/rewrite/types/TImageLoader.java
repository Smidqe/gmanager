package rewrite.types;

import java.util.concurrent.Callable;

import javafx.scene.image.Image;
import rewrite.types.TImage.enum_map;

public class TImageLoader implements Callable<Image>
{
	private TImageContainer image;
	private String version;
	
	public TImageLoader(TImageContainer image, String version)
	{
		this.image = image;
		this.version = version;
	}
	
	private Image load(String URL)
	{
		return new Image(URL);
	}
	
	private Image load()
	{
		return load(image.getImage().getProperty(enum_map.MAP_IMAGES, version));
	}
	

	@Override
	public Image call() throws Exception {
		return load();
	}
}
