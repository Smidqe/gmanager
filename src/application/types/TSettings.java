package application.types;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import application.extensions.strings;
import application.types.sites.TSite;

public class TSettings 
{
	enum Maps {SETTINGS, PATHS, SITES, FILES};
	
	private Map<String, String> __settings; //holds all global variables
	private Map<String, String> __paths; //holds all paths
	private Map<String, TSite> __sites; //holds all sites
	private Map<String, Ini> __files;
	//keep this as a singleton
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
		this.__files = new HashMap<String, Ini>();
		
		this.load();
	}

	private void load()
	{
		this.__paths.put("cache", "src/application/data/cache/");		
		
		
		//load all files
		Ini __ini = null;
		try {
			__ini = new Ini(new File("src/application/data/resources/config/settings.ini"));
			
			//load the ini file
			__ini.load();
			
			Section __map = null;
			for (String __section : __ini.keySet())
			{
				__map = __ini.get(__section);
				
				System.out.println(__section);
				for (String __key : __map.keySet())
				{
					switch (__section) 
					{
						case "PATHS": __paths.put(__key, __map.get(__key)); break;
						case "SETTINGS": __settings.put(__key, __map.get(__key)); break;
						case "FILES": __files.put(__key, new Ini(new File(__map.get(__key))));
						
						default:
							break;
					}
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		//System.out.println(strings.parse("[&&, ||, !, -, AND, OR, NOT, ~, ^, (, )]", ',', "[]".toCharArray()));
		//__ini = __files.get("sites");
	}
	
	public void save(Maps map)
	{
		
	}
	
	public void change(Maps map, Object value)
	{
		
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
	/*
	 	load
	 */
}
