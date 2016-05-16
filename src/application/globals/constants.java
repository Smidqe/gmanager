package application.globals;


//TODO: Arrange these to somewhat reasonable order.
public class constants {
	//Layers used in GUI
	public static final int __INDEX_LAYER_IMAGES = 0;
	public static final int __INDEX_LAYER_FILTERS = 1;
	public static final int __INDEX_LAYER_SEARCH = 2;
	public static final int __INDEX_LAYER_ACCOUNT = 3;
	public static final int __INDEX_LAYER_SETTINGS = 4;
	public static final int __INDEX_LAYER_PAGESWITCH = 5; 
	
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
	public static final int __ID_IMAGE_SIZE_FULL = 0;
	public static final int __ID_IMAGE_SIZE_THUMBNAIL = 1;
	public static final int __ID_IMAGE_SIZE_WIDTH = 2;
	public static final int __ID_IMAGE_SIZE_HEIGHT = 3;
	public static final int __ID_IMAGE_SIZE = 4;
	public static final int __ID_IMAGE_SOURCE = 5;
	public static final int __ID_IMAGE_NAME = 6;
	public static final int __ID_IMAGE_DESCRIPTION = 7;
	public static final int __ID_IMAGE_TYPE = 8;
	public static final int __ID_IMAGE_FAVORITES = 9;
	public static final int __ID_IMAGE_UPVOTES = 10;
	
	//Default path constants
	public static final String __PATH_DEFAULT_SETTINGS = "/data/settings.ini";
	public static final String __PATH_DEFAULT_IMAGE_FORMATS = "/data/formats/";
	public static final String __PATH_DEFAULT_DOWNLOADS = "/downloads/";
	
	//
}
