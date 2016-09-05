package application.data.settings;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import application.data.resources.globals.globals;
import libFileExtensions.files.TFile;
import libFileExtensions.files.TIni;
import libFileExtensions.folders.TFolder;

@SuppressWarnings("unused")
public class settings {
	public final static int __ID_FILE_SETTINGS_GENERAL = 0;
	public final static int __ID_FILE_SETTINGS_IMAGES = 1;
	public final static int __ID_FILE_SETTINGS_SITES = 2;
	
	private static final String __SECTION_GENERAL = "GENERAL";
	private static final String __SECTION_PATHS = "PATHS";
	
	public boolean bDoubleClickToOpen = false;
	public boolean bOpenFullscreen = false;
	public boolean bAutomaticPaging = true;
	public boolean bCacheAsLocal = false;
	public boolean bRetainFullImages = false;
	public boolean bClearCacheOnExit = true;
	
	public int iStartSite;
	public int iAmountPerPage;
	public int iMaxThreads;
	public int iMaxDownloadsAtTime;
	
	public String pCachePath = null;
	public String pSettingsPath = null;
	public String pImagesPath = null;
	public String pDownloadPath = null;
	
	private static settings __self = new settings();
	public TIni images, settings, sites;
	public TFolder cache;
	private globals constants;
	
	private settings()
	{
		try 
		{
			this.settings = new TIni("src/application/data/resources/config/settings.ini", false);
			this.images = new TIni("src/application/data/resources/config/images.ini", false);
			this.sites = new TIni("src/application/data/resources/config/sites.ini", false);
		
			this.settings.information();
			this.images.information();
			this.sites.information();
			
			this.cache = new TFolder(settings.entry(__SECTION_PATHS, "cache"), false);
			load();
		} 
		catch (IOException | URISyntaxException e) 
		{
			e.printStackTrace();
		}
	}

	public TIni getIni(int fileID)
	{
		switch (fileID)
		{
			case __ID_FILE_SETTINGS_GENERAL: return this.settings; 
			case __ID_FILE_SETTINGS_IMAGES: return this.images;
			case __ID_FILE_SETTINGS_SITES: return this.sites;
		}
		
		return null;
	}

	public String getIniID(int fileID, String section, String ID)
	{
		TIni file;
		if ((file = getIni(fileID)) == null)
			return null;
				
		return file.entry(section, ID);
	}
	
	public void load()
	{
//		bDoubleClickToOpen = Boolean.parseBoolean(this.settings.entry(__SECTION_GENERAL, "bDoubleClickOpen"));
//		bOpenFullscreen = Boolean.parseBoolean(this.settings.entry(__SECTION_GENERAL, "bOpenFullscreen"));
//		bAutomaticPaging = Boolean.parseBoolean(this.settings.entry(__SECTION_GENERAL, "bAutomaticPaging"));
//		
//		iStartSite = Integer.parseInt(this.settings.entry(__SECTION_GENERAL, "iStartSite"));
//		iAmountPerPage = Integer.parseInt(this.settings.entry(__SECTION_GENERAL, "iAmountPerPage"));
	} 
	
	public void clearTemps()
	{
		ArrayList<TFile> files = cache.getFiles();
		
		for (TFile file : files)
			file.delete();
	}
	
	public void save()
	{
	}
	
	public static settings instance()
	{
		return __self;
	}
}
