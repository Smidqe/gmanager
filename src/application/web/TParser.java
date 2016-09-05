package application.web;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.WeakHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;


public class TParser extends Observable implements Runnable
{
	private TConnection url;
	private JSONObject object;
	private List<Map<String, Object>> parsed;
	
	public TParser()
	{
		this.url = null;
		this.object = null;
	}
	
	public TParser(URL url)
	{
		this();
		this.url = new TConnection(url);
	}

	public void setURL(URL url)
	{
		this.url = new TConnection(url);
	}

	private List<Object> convert(JSONArray __array)
	{
		List<Object> __values = new ArrayList<Object>();
		
		for (int i = 0; i < __array.size(); i++)
		{
			Object value = __array.get(i);
			
			if (value instanceof JSONArray)
				value = convert((JSONArray) value);
			
			if (value instanceof JSONObject)
				value = convert((JSONObject) value);
			
			__values.add(value);
		}
		
		return __values;
	}
	
	private Map<String, Object> convert(JSONObject __object)
	{
		return JSONtoMap(__object);
	}
	
	private Map<String, Object> JSONtoMap(JSONObject __object)
	{
		if (__object == null)
			return null;
		
		Map<String, Object> __values = new WeakHashMap<String, Object>();

		
		for (Object __key : __object.keySet())
		{
			Object value = __object.get(__key);
			
			if (value instanceof JSONArray)
				value = convert((JSONArray) value);
			
			if (value instanceof JSONObject)
				value = convert((JSONObject) value);
			
			__values.put((String) __key, value);
		}
		
		return __values;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> parseObject(JSONObject object)
	{
		Map<String, Object> __map = JSONtoMap(object);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		for (Object obj : (List<Object>) __map.get("images"))
			list.add((Map<String, Object>) obj);
		
		__map.clear();
		
		return list;
	}

	public synchronized List<Map<String, Object>> parsed()
	{
		return this.parsed;
	}
	
	@Override
	public void run() {
		if (this.url == null)
			return;
		
		try
		{
			System.out.println(this.url.ping());
			
			if (this.url.ping() / 100 != 2)
				return;
			
			synchronized (this)
			{
				this.object = url.getJSON();
				this.parsed = parseObject(this.object);
				
				changed();
			}
		}
		catch (ParseException | IOException e)
		{
		}
	}
	
	public void changed()
	{
		setChanged();
		notifyObservers();
	}
}
