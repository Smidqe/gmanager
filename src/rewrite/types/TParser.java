package rewrite.types;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import rewrite.extensions.connections;

public class TParser implements Callable<List<Map<String, Object>>>{

	private JSONObject object;
	
	public TParser(JSONObject object)
	{
		this.object = object;
	}
	
	public TParser(URL url)
	{
		try {
			this.object = connections.getJSON(url.toString());
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static List<Object> convert(JSONArray __array)
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
	
	private static Map<String, Object> convert(JSONObject __object)
	{
		return JSONtoMap(__object);
	}
	
	public static Map<String, Object> JSONtoMap(JSONObject __object)
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
	
	public static Map<String, Object> parseJSON(InputStream stream) throws IOException, ParseException
	{
		JSONParser parser = new JSONParser();
		InputStreamReader reader = new InputStreamReader(stream);
		JSONObject object = (JSONObject) parser.parse(reader);
		
		reader.close();
		
		return JSONtoMap(object);
	}
	
	public static Map<String, Object> parseJSON(URL url) throws IOException, ParseException
	{
		return parseJSON(url.openStream());
	}

	public static Map<String, Object> parseJSON(String path) throws MalformedURLException, IOException, ParseException
	{
		return parseJSON((new File(path)).toURI().toURL().openStream());
	}

    @SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> call() throws Exception {
		Map<String, Object> __map = JSONtoMap(object);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for (Object obj : (List<Object>) __map.get("images"))
			list.add((Map<String, Object>) obj);
		
		__map.clear();
		
		return list;
	}
}
