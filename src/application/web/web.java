package application.web;

import application.globals.constants;
import javax.net.ssl.HttpsURLConnection;

import java.io.IOException;
import java.net.*;

public class web {
	public web()
	{
		
	}

	public String connect(int site, int page, String key) throws IOException
	{
		switch (site)
		{
		case constants.__INDEX_SITE_DERPIBOORU_IMAGES:
		case constants.__INDEX_SITE_DERPIBOORU_WATCHED:
		case constants.__INDEX_SITE_DERPIBOORU_SEARCH:
			break;
		}
		
		HttpsURLConnection connection = (HttpsURLConnection) verify("").openConnection();
		
		
		
		return null;
	}
	
	private URL verify(String site) {
		if (site.isEmpty())
			return null;
		
		if (!site.contains("https://"))
			;
		return null;
	}

	public String connect(int site, int page) throws IOException
	{
		return this.connect(site, page, "");
	}
}
