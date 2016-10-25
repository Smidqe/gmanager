package application.types;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import application.extensions.arrays;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/*
	TODO:
		- Make better save/load methods, currently we can't apply every class to a binary file due to serialization limitations
 */

public class TCacheManager 
{
	private static TCacheManager instance = new TCacheManager();
	
	private List<String> ids;
	private TSettings __settings = TSettings.instance();
	
	public TCacheManager() 
	{
		this.ids = new ArrayList<String>();
	}
	
	public TCacheFile getFileByID(String UUID) throws IOException
	{
		return new TCacheFile(__settings.getPaths().get("cache") + UUID, false, "");
	}
	
	public <T extends Serializable> void save(List<T> objects, List<String> UUIDs) throws IOException
	{
		if (objects == null)
			throw new NullPointerException("TCacheManager::save(list): Given argument was null");
		
		for (int i = 0; i < objects.size(); i++)
			save(objects.get(i), UUIDs.get(i));
	}
	
	public <T extends Serializable> void save(T object, String UUID) throws IOException
	{
		if (object == null)
			throw new NullPointerException("TCacheManager::save(object): Given argument was null");
		
		TCacheFile file = new TCacheFile(__settings.getPaths().get("cache"), true, UUID);
		
		file.write(object);
		file.close();
		
		ids.add(file.getUUID());
	}
	
	public <T extends Serializable> T load(String id) throws ClassNotFoundException, IOException
	{
		int pos = arrays.position(ids, id);

		if (pos == -1)
			return null;
		
		return getFileByID(ids.get(pos)).get();
	}
	
	public static TCacheManager instance()
	{
		return instance;
	}

	public boolean exists(String ID) 
	{
		return arrays.exists(ids, ID);
	}

	//JavaFX images cannot be serialized, therefore they cannot be saved by normal means
	public void saveFXImage(Image __image, String ID) throws Exception 
	{
		boolean success = ImageIO.write(SwingFXUtils.fromFXImage(__image, null), "png", new TCacheFile(__settings.getPaths().get("cache"), ID));

		if (success)
			ids.add(ID);
		else
			throw new Exception("TCacheManager: Image was not saved");
	}
	
	public Image readFXImage(String ID) throws IOException
	{
		int pos = -1;
		
		if ((pos = arrays.position(ids, ID)) == -1)
			return null;

		return (Image) SwingFXUtils.toFXImage(ImageIO.read(getFileByID(ids.get(pos))), null);
	}
	
	public void clear() throws IOException
	{
		TCacheFile temp = null;
		for (String ID : ids)
		{
			System.out.println("TCacherManager: Deleting: " + ID);
			
			temp = new TCacheFile(__settings.getPath("cache"), false, ID);
			temp.delete();
		}
		
		ids.clear();
	}
}
