package application.data.resources.globals;


//TODO: Arrange these to somewhat reasonable order.
public class globals {
	//Layers used in GUI

	
	//Site constants
	public static final int __INDEX_SITE_DERPIBOORU = 0;
	public static final int __INDEX_SITE_IMGUR = 1;
	
	//Subsites if present (currently only on Derpibooru)
	public static final int __INDEX_SUBSITE_DERPIBOORU_IMAGES = 0; 
	public static final int __INDEX_SUBSITE_DERPIBOORU_WATCHED = 1;
	public static final int __INDEX_SUBSITE_DERPIBOORU_SEARCH = 2;
	
	//Site constants, URL
	public static final String __URL_SUBSITE_DERPIBOORU_IMAGES = "https://derpibooru.org/images.json";
	public static final String __URL_SUBSITE_DERPIBOORU_WATCHED = "https://derpibooru.org/images/watched.json";
	public static final String __URL_SUBSITE_DERPIBOORU_SEARCH = "https://derpibooru.org/search.json";
	
	//Image constants

	
	//Default path constants
	public static final String __PATH_DEFAULT_SETTINGS = "/data/settings.ini";
	public static final String __PATH_DEFAULT_IMAGE_FORMATS = "/data/formats/";
	public static final String __PATH_DEFAULT_DOWNLOADS = "/downloads/";
	
	//Program constants (not final, since they will be modified by the settings)

}
