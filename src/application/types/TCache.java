package application.types;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.UUID;

import libFileExtensions.folders.TFolder;

public class TCache
{
	private HashMap<String, TCacheFile> files;
	private TFolder folder;

	private static TCache __self = new TCache();
	
	public TCache()
	{
		this.files = new HashMap<String, TCacheFile>();
	}
	
	public TCache(String path) throws URISyntaxException, IOException
	{
		this();
		this.folder = new TFolder(path, false);
	}

	public void setFolder(TFolder folder)
	{
		this.folder = folder;
	}
	
	public TFolder getFolder()
	{
		return this.folder;
	}
	
	public Object get(String ID)
	{
		return this.files.get(ID);
	}
	
	public boolean fileExists(String ID)
	{
		for (String key : files.keySet())
			if (ID.equals(key))
				return true;
		
		return false;
	}
	
	public void write() throws IOException
	{
		TCacheFile file;
		
		for (TCacheFile __file : this.files.values())
		{
			if (__file.exists())
				continue;
				
			file = new TCacheFile(folder.getPath() + UUID.randomUUID().toString(), true);
		
			ObjectOutputStream stream = new ObjectOutputStream(file.openOutput(false));

			stream.writeObject(__file);

			stream.close();
			file.closeOutput();
		}
		
		
	}
	
	public void add(String ID, TCacheFile object)
	{		
		this.files.put(ID, object);
	}
	
	public void remove(String ID)
	{
		this.files.remove(ID);
	}


	public static TCache instance() {
		// TODO Auto-generated method stub
		return __self;
	}

}
