package application.web;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TConnection 
{
	private URL url;
	
	public TConnection(URL url)
	{
		this.url = url;
	}
	
	public TConnection(String url) throws MalformedURLException
	{
		this.url = new URL(url);
	}
	
	public int ping() throws IOException
	{
		return ((HttpsURLConnection) this.url.openConnection()).getResponseCode();
	}
	
	public boolean exists() throws IOException
	{
		return (ping() != HttpsURLConnection.HTTP_NOT_FOUND);
	}
	
	public boolean isFile() throws IOException
	{
		return (url.openConnection().getContentLength() > 2);
	}
	
	public HttpsURLConnection getConnection() throws IOException
	{
		return ((HttpsURLConnection) this.url.openConnection());
	}
	
	public URL getURL()
	{
		return this.url;
	}
	
	public JSONObject getJSON() throws IOException, ParseException
	{
		if (!(url.getProtocol().contains("https") && (ping() == 200)))
			return null;
		
		InputStreamReader __reader = new InputStreamReader(this.url.openStream());
		JSONObject json = (JSONObject) new JSONParser().parse(__reader);
		__reader.close();
		
		return json;
	}
}
