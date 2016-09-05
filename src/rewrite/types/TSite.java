package rewrite.types;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TSite 
{
	private String name;

	private Map<String, String> IDs;
	private List<String> skippables;
	private Map<String, URL> urls;
	
	public TSite()
	{
		this.IDs = new HashMap<String, String>();
		this.skippables = new ArrayList<String>();
		this.urls = new HashMap<String, URL>();
	}
	
	public void setURL(String name, URL url) throws MalformedURLException
	{
		this.urls.put(name, url);
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
}
