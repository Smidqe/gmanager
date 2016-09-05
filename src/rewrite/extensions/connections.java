package rewrite.extensions;


import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Arrays;

public class connections {
	public connections()
	{
		
	}
	
	public static String ping(String site) throws MalformedURLException, IOException
	{
		String value = null;
		
		if (site.contains("https"))
			value = String.valueOf(((HttpsURLConnection) verify(site).openConnection()).getResponseCode());
		else
			value = String.valueOf(((HttpURLConnection) verify(site).openConnection()).getResponseCode());
	
		return value;
	}
	
	//not really necessary, but good nonetheless
	public static URL verify(String site) throws MalformedURLException {
		if (site.isEmpty())
			return null;
		
		boolean valid = strings.contains(Arrays.asList("http", "https"), site, false);
		if (!valid)
			return null;

		return new URL(site);
	}
	
	public static JSONObject getJSON(String site) throws IOException, ParseException
	{
		if (!site.contains(".json") || !ping(site).equals("200"))
			return null;

		URL url;
		if ((url = verify(site)) == null)
			return null;
		
		InputStreamReader __reader = new InputStreamReader(url.openStream());
		JSONObject json = (JSONObject) new JSONParser().parse(__reader);
		__reader.close();
		
		return json;
	}


	public static String ping(URL url) throws MalformedURLException, IOException 
	{
		return ping(url.toString());
	}
}
