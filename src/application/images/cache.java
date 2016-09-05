package application.images;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.HashMap;

import application.data.settings.settings;
import application.images.image.TImage;
import libFileExtensions.files.TFile;

public class cache {
	private HashMap<String, TFile> files;
	private settings __settings;
	private boolean bDeleteOnExit;
	private static cache self = new cache();
	
	private cache()
	{
		files = new HashMap<String, TFile>();
	}
	
	public boolean inCache(TImage image)
	{
		boolean found = false;
		for (String str : files.keySet())
		{
			found = str.equals(image.getID());
			
			if (found)
				break;
		}
		
		return false;
	}

	public void setDeleteOnExit(boolean value)
	{
		this.bDeleteOnExit = value;
	}
	
	public boolean isDeleteOnExit()
	{
		return this.bDeleteOnExit;
	}
	
	public void delete(String key)
	{
		files.get(key).delete();
		files.remove(key);
	}
	
	public void deleteAll()
	{
		if (bDeleteOnExit)
			return;
		
		for (String str : files.keySet())
			delete(str);
	}
	
	public void create(TImage image) throws IOException
	{
		if (inCache(image))
			return;
		
		TFile file = new TFile(File.createTempFile("cache", null, new File(__settings.pCachePath)).getPath(), false);
		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(__settings.pCachePath + file.getName()));
		
		stream.writeObject(image);
		
		stream.flush();
		stream.close();
		
		files.put(image.getID(), file);
	}
	
	public TImage get(String id) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		ObjectInputStream stream = new ObjectInputStream(new FileInputStream(files.get(id)));
		
		TImage image = (TImage) stream.readObject();
		
		stream.close();
		
		return image;
	}
	
	public cache instance()
	{
		return self;
	}
}
