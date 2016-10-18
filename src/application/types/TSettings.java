package application.types;

import java.util.HashMap;
import java.util.Map;
@SuppressWarnings("unused")
public class TSettings 
{
	private Map<String, String> settings;
	private Map<String, String> paths;
	private Map<String, TSite> sites;
	
	private static TSettings self = new TSettings();
	
	public synchronized static TSettings instance()
	{
		return self;
	}
	
	protected TSettings()
	{
		this.settings = new HashMap<String, String>();
		this.paths = new HashMap<String, String>();
		
		//load ini files to the according 
	}
	
	public synchronized String getString(String key)
	{
		return this.settings.get(key);
	}
	
	public synchronized boolean getBoolean(String key)
	{
		return Boolean.getBoolean(this.settings.get(key));
	}
	
	public synchronized int getInteger(String key)
	{
		return Integer.getInteger(this.settings.get(key));
	}
	
	public synchronized Map<String, String> getPaths()
	{
		return this.paths;
	}
	
	public synchronized String getPath(String key)
	{
		return this.paths.get(key);
	}
	
	//add rest of the functions later.
}
