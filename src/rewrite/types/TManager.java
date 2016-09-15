package rewrite.types;

import java.net.MalformedURLException;
import java.net.URL;


public class TManager
{
	private static TManager instance = new TManager();
	private TSite site;
	private TTileManager tileManager;
	private TCacheManager cacheManager;
	
	public static TManager getInstance()
	{
		return instance;
	}
	
	private TManager()
	{
		this.site = new TSite();
		try {
			//this site is temporary and will be used for development/debugging
			//normally it would load necessary information from ini files.
			this.site.setURL("images", new URL("https://derpibooru.org/images.json"));
			this.tileManager = TTileManager.instance();
			this.cacheManager = TCacheManager.instance();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public TTileManager getTileManager()
	{
		return this.tileManager;
	}
	
	public TCacheManager getCacheManager()
	{
		return this.cacheManager;
	}
	
	public TSite getSite()
	{
		return this.site;
	}
	
	public void setSite(TSite site)
	{
		this.site = site;
	}
}
