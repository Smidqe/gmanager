package application.images.image;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import application.types.TIDCreator;
import javafx.scene.image.Image;

public class TImage2 implements Serializable{
	private static final long serialVersionUID = 1L;

	private HashMap<String, String> properties;
	private HashMap<String, Image> images;

	public TImage2()
	{
		this.properties = new HashMap<String, String>();
		this.images = new HashMap<String, Image>();
	}
	
	public String getProperty(String key)
	{
		return properties.get(key);
	}
	
	public void setProperty(String key, String value)
	{
		this.properties.put(key, value);
	}

	public void setImage(String key, Image image)
	{
		this.images.put(key, image);
	}
	
	public Image getImage(String key)
	{
		return this.images.get(key);
	}
	
	public void createID() throws Exception
	{		
		ExecutorService service = Executors.newSingleThreadExecutor();
		this.properties.put("ID", service.submit(new TIDCreator<>(this)).get());
	}
}
