package rewrite.types;

import java.util.List;

@SuppressWarnings("unused") //this is here for now until I finish this class
public class TCacheManager implements Runnable
{
	private static volatile TCacheManager instance = new TCacheManager();
	private String path;
	private List<TCacheFile> files;
	
	
	public TCacheManager() 
	{
		// TODO Auto-generated constructor stub
	}
	
	public <T> void save(List<T> objects)
	{
		
	}
	
	public <T> boolean isOnCache(String id)
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

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
