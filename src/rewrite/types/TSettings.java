package rewrite.types;

import java.util.HashMap;
import java.util.Map;

public class TSettings 
{
	
	private Map<String, String> settings;
	private static TSettings self = new TSettings();
	
	public synchronized static TSettings instance()
	{
		return self;
	}
	
	protected TSettings()
	{
		this.settings = new HashMap<String, String>();
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
}
