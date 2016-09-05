package application.web.connections;


import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import application.data.resources.globals.globals;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class connections {
	public connections()
	{
		
	}

	
	//TODO: Move this one to settings eventually and change it to get(int ID)
	public static String getSite(int site)
	{
		switch (site)
		{
			
			//case constants.__INDEX_SITE_DERPIBOORU: return constants.__URL_SITE_DERPIBOORU;
			case globals.__INDEX_SUBSITE_DERPIBOORU_IMAGES: return globals.__URL_SUBSITE_DERPIBOORU_IMAGES;
			case globals.__INDEX_SUBSITE_DERPIBOORU_SEARCH: return globals.__URL_SUBSITE_DERPIBOORU_SEARCH;
		
		}
		
		return null;
	}
	
	public static String ping(String site, boolean https) throws MalformedURLException, IOException
	{
		if (https)
			return String.valueOf(((HttpsURLConnection) verify(site, https).openConnection()).getResponseCode());
		else
			return String.valueOf(((HttpURLConnection) verify(site, https).openConnection()).getResponseCode());
	}
	
	public static String ping(int site, int page, boolean https) throws IOException
	{
		return ping(getSite(site), https);
	}
	
	//not really necessary, but good nonetheless
	public static URL verify(String site, boolean https) throws MalformedURLException {
		if (site.isEmpty())
			return null;

		if (!site.contains("https://") && https || !site.contains("http://") && !https)
			return null;

		return new URL(site);
	}
	
	public static JSONObject getJSON(String site) throws IOException, ParseException
	{
		if (!site.contains(".json") || !ping(site, true).equals("200"))
			return null;

		URL url;
		if ((url = verify(site, true)) == null)
			return null;
		
		InputStreamReader __reader = new InputStreamReader(url.openStream());
		JSONObject json = (JSONObject) new JSONParser().parse(__reader);
		__reader.close();
		
		return json;
	}


	public static String ping(URL url, boolean equals) throws MalformedURLException, IOException {
		return ping(url.toString(), equals);
	}
	
	public static JSONObject db_getPage(String URL, int page) throws IOException, ParseException
	{
		return getJSON(URL + "?page=" + String.valueOf(page));
	}
}
