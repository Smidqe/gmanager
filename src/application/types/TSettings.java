package application.types;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import application.types.sites.TSite;
import libFileExtensions.files.TIniFile;

public class TSettings 
{
	private Map<String, String> __settings;
	private Map<String, String> __paths;
	private Map<String, TSite> __sites;
	
	private static TSettings self = new TSettings();
	
	public synchronized static TSettings instance()
	{
		return self;
	}
	
	protected TSettings()
	{
		this.__settings = new HashMap<String, String>();
		this.__paths = new HashMap<String, String>();
		this.__sites = new HashMap<String, TSite>();
		
		//load ini files to the according maps
		try 
		{
			TIniFile __file = new TIniFile("src/application/data/settings/settings.ini", false);
			
			__file.information();
		
			
			Map<String, Map<String, String>> information = __file.get();
			
			for (String key : information.keySet())
			{
				switch(key)
				{
					case "SETTINGS": break;
					case "PATHS": break;
					
					default: break;
				}
			}
		
			this.__paths.put("cache", "src/application/data/cache/");
			
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized String getString(String key)
	{
		return this.__settings.get(key);
	}
	
	public synchronized boolean getBoolean(String key)
	{
		return Boolean.parseBoolean(this.__settings.get(key));
	}
	
	public synchronized int getInteger(String key)
	{
		return Integer.parseInt(this.__settings.get(key));
	}
	
	public synchronized Map<String, String> getPaths()
	{
		return this.__paths;
	}
	
	public synchronized String getPath(String key)
	{
		return this.__paths.get(key);
	}
	
	public synchronized TSite getSite(String key)
	{
		return this.__sites.get(key);
	}
	//add rest of the functions later.
}
