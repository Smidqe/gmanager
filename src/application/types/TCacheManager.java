package application.types;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import application.data.settings.settings;

@SuppressWarnings("unused") //this is here for now until I finish this class
public class TCacheManager 
{
	private static TCacheManager instance = new TCacheManager();
	private List<String> ids;
	private TSettings __settings = TSettings.instance();
	
	public TCacheManager() 
	{
		this.ids = new ArrayList<String>();
	}
	
	public TCacheFile getFileByID(String id) throws IOException
	{
		return new TCacheFile(__settings.getPaths().get("cache") + id, false);
	}
	
	public <T extends Serializable> void save(List<T> objects) throws IOException
	{
		if (objects == null)
			throw new NullPointerException("TCacheManager::save(list): Given argument was null");
		
		for (T obj : objects)
			save(obj);
	}
	
	public <T extends Serializable> void save(T object) throws IOException
	{
		if (object == null)
			throw new NullPointerException("TCacheManager::save(object): Given argument was null");
		
		TCacheFile file = new TCacheFile(__settings.getPaths().get("cache"), true);
		
		file.write(object);
		file.close();
		
		ids.add(file.getUUID());
	}
	
	public <T extends Serializable> T load(String id) throws ClassNotFoundException, IOException
	{
		int index = ids.indexOf(id);
		
		if (index == -1)
			return null;

		return getFileByID(ids.get(index)).get();
	}
	
	
	public static TCacheManager instance()
	{
		return instance;
	}

}
