package rewrite.types;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused") //this is here for now until I finish this class
public class TCacheManager 
{
	private static volatile TCacheManager instance = new TCacheManager();
	private String path;
	private List<TCacheFile> files;
	
	
	public TCacheManager() 
	{
		this.files = new ArrayList<TCacheFile>();
	}
	
	public <T extends Serializable> void save(List<T> objects) throws IOException
	{
		for (T obj : objects)
			save(obj);
	}
	
	public <T extends Serializable> void save(T object) throws IOException
	{
		/*
		path = settings.getCachePath(); 
		 
		 
		 
		 */
		
		TCacheFile file = new TCacheFile(path, true);
	
		
	}
	
	public boolean isOnCache(String id)
	{
		boolean found = false;
		
		for (TCacheFile file : files)
		{
			found = file.getID().equals(id);
		
			if (found)
				break;
		}
		
		return found;
	}
	
	public static TCacheManager instance()
	{
		return instance;
	}

}
