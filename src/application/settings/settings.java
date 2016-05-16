package application.settings;

import libfs.files.TIni;

import java.io.IOException;

import application.globals.constants;

public class settings {
	private static settings __self = new settings();
	public TIni images, settings;
	public constants constants;
	
	private settings()
	{
		try 
		{
			this.settings = new TIni("/config/settings.ini", false);
			this.images = new TIni("/config/images.ini", false);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		
	}
	
/*

	





*/
	
	public static settings instance()
	{
		return __self;
	}
}
