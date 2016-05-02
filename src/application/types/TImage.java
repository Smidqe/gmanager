package application.types;

import java.util.HashMap;

public class TImage{
	private HashMap<String, Object> info;
	
	private int width, height, faves, upvotes, comments;
	private double size;
	private String url, thumbnail, image, source, name, description, type;
	

	public TImage(String url) 
	{
		this.url = url;
		this.info = new HashMap<String, Object>();
	}
	
	public TImage()
	{
		this.info = new HashMap<String, Object>();
	}

	public void setInfo(HashMap<String, Object> info)
	{
		this.info = info;
	}
	
	public HashMap<String, Object> getInfo()
	{
		return this.info;
	}
	
	public int width()
	{
		return this.width;
	}

	public int height()
	{
		return this.height;
	}

	public void setURL(String url)
	{
		this.url = url;
	}
	
	public String URL()
	{
		return this.url;
	}

	public double size() {
		return size;
	}

	public int favorites() {
		return faves;
	}

	public int upvotes() {
		return upvotes;
	}

	public int comments() {
		return comments;
	}

	public String thumbnail() {
		return thumbnail;
	}

	public String image() {
		return image;
	}

	public String source() {
		return source;
	}

	public String name() {
		return name;
	}

	public String description() {
		return description;
	}

	public String type() {
		return type;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setFavorites(int faves) {
		this.faves = faves;
	}

	public void setUpvotes(int upvotes) {
		this.upvotes = upvotes;
	}

	public void setComments(int comments) {
		this.comments = comments;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setType(String type) {
		this.type = type;
	}
}
