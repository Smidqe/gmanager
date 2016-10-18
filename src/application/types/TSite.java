package application.types;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class TSite 
{
	private String name;

	private Map<String, String> IDs;
	private List<String> skippables;
	private Map<String, URL> urls;
	private boolean authenticate;
	
	public TSite()
	{
		this.IDs = new WeakHashMap<String, String>();
		this.skippables = new ArrayList<String>();
		this.urls = new WeakHashMap<String, URL>();
		
		loadSettings();
	}
	
	private void loadSettings()
	{
		
	}
	
	public void setURL(String name, URL url) throws MalformedURLException
	{
		this.urls.put(name, url);
	}

	public void setSkippable(String ID)
	{
		this.skippables.add(ID);
	}
	
	public synchronized void setID(String key, String value)
	{
		this.IDs.put(key, value);
	}
	
	public synchronized Map<String, String> getIDs()
	{
		return this.IDs;
	}
	
	public List<String> getSkippables()
	{
		return this.skippables;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public synchronized URL getURL(String name)
	{
		return getURL(name, "", 0);
	}
	
	public synchronized URL getURL(String name, String prefix, int page)
	{
		if (page == 0)
			return this.urls.get(name);
		
		try {
			return new URL(urls.get(name).toString() + prefix + String.valueOf(page));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public boolean needsAuthentication()
	{
		return this.authenticate;
	}
	
	
}
