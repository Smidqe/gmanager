package application.types;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
		int index = ids.indexOf(id);
		
		if (index == -1)
			return null;

		return getFileByID(ids.get(index)).get();
	}
	
	public boolean exists(String ID)
	{
		return this.ids.indexOf(ID) != -1;
	}
	
	public static TCacheManager instance()
	{
		return instance;
	}

}
