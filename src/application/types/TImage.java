package application.types;

public class TImage{
	
	private long width, height, faves, upvotes;
	private double size;
	private String url, thumbnail, image, source, name, description, type;
	
	public TImage()
	{
	}

	public long width()
	{
		return this.width;
	}

	public long height()
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

	public long favorites() {
		return faves;
	}

	public long upvotes() {
		return upvotes;
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

	public void setWidth(long l) {
		this.width = l;
	}

	public void setHeight(long l) {
		this.height = l;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setFavorites(long l) {
		this.faves = l;
	}

	public void setUpvotes(int upvotes) {
		this.upvotes = upvotes;
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

	@Override
	public String toString() {
		return "TImage [width=" + width + ", height=" + height + ", faves=" + faves + ", upvotes=" + upvotes + ", size="
				+ size + ", url=" + url + ", thumbnail=" + thumbnail + ", image=" + image + ", source=" + source
				+ ", name=" + name + ", description=" + description + ", type=" + type + "]";
	}
	
	
}
