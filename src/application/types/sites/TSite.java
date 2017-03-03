package application.types.sites;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class TSite 
{
	public enum MAPS {ID, URL, VARIABLE};
	
	private String name;

	private Map<String, String> __IDs;
	private Map<String, URL> __urls;
	
	private List<String> __skippables;
	private boolean authenticate;
	private boolean __searcheable;
	
	private Map<String, String> __variables;
	
	public TSite()
	{
		this.__IDs = new WeakHashMap<String, String>();
		this.__skippables = new ArrayList<String>();
		this.__urls = new WeakHashMap<String, URL>();
		this.__variables = new WeakHashMap<String, String>();
	}

	public void putURL(String name, URL url) throws MalformedURLException
	{
		this.__urls.put(name, url);
	}

	public void addSkippable(String ID)
	{
		this.__skippables.add(ID);
	}

	public boolean isSearcheable()
	{
		return this.__searcheable;
	}
	
	public void put(MAPS map, String name, String value)
	{
		Map<String, String> __map = null;
		
		switch (map) 
		{
			case ID: __map = this.__IDs; break;
			case VARIABLE: __map = this.__variables; break;

			default:
				break;
		}
		
		__map.put(name, value);
	}
	
	public void setID(String key, String value)
	{
		this.__IDs.put(key, value);
	}
	
	public Map<String, String> getIDs()
	{
		return this.__IDs;
	}
	
	public List<String> getSkippables()
	{
		return this.__skippables;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public URL getURL(String name)
	{
		return getURL(name, 0);
	}
	
	public URL getURL(String name, int page)
	{
		if (page == 0)
			return this.__urls.get(name);
		
		try {
			return new URL(__urls.get(name).toString() + __variables.get("prefix") + String.valueOf(page));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public void setAuthentication(boolean value)
	{
		this.authenticate = value;
	}
	
	public boolean needsAuthentication()
	{
		return this.authenticate;
	}
	
	
}
