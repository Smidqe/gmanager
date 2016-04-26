package application.settings;

import application.types.TFile;

public class settings {
	private static settings __self = new settings();
	private TFile file;
	
	
	private settings()
	{
		
	}
	
	public String get(String section, String variable)
	{
		return file.iniRead(section, variable);
	}
	
	public void set(String section, String key, String value)
	{
		file.iniWrite(value, section, key);
	}
	
	public static settings instance()
	{
		return __self;
	}
}
