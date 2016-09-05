package application.images.image;

import java.io.Serializable;
import java.util.UUID;

import javafx.scene.image.Image;

public class TImage implements Serializable{
	private static final long serialVersionUID = 1L;

	private String thumbnail, image, ID, name, source, type;
	private long width, height, score, upvotes, downvotes, faves, comments;
	private double size;
	private Image imgThumb, imgFull;

	public TImage()
	{
		this.ID = UUID.randomUUID().toString();
	}
	
	public void setThumbnail(Image image)
	{
		this.imgThumb = image;
	}
	
	public Image getThumbnail()
	{
		return this.imgThumb;
	}
	
	public void setFullImage(Image image)
	{
		this.imgFull = image;
	}
	
	public Image getFullImage()
	{
		return this.imgFull;
	}
	
	public String getThumbnailURL() 
	{
		return thumbnail;
	}
	
	public String getImage() 
	{
		return image;
	}
	
	public String getID() 
	{
		return ID;
	}
	
	public String getName() 
	{
		return name;
	}
	
	public String getSource() 
	{
		return source;
	}
	
	public String getType() 
	{
		return type;
	}
	
	public long getWidth() 
	{
		return width;
	}
	
	public long getHeight() 
	{
		return height;
	}
	
	public long getScore() 
	{
		return score;
	}
	
	public long getUpvotes() 
	{
		return upvotes;
	}
	
	public long getDownvotes() 
	{
		return downvotes;
	}
	
	public long getFaves() 
	{
		return faves;
	}
	
	public long getComments() 
	{
		return comments;
	}
	
	public double getSize() 
	{
		return size;
	}

	public void setThumbnailURL(String thumbnail) 
	{
		this.thumbnail = thumbnail;
	}
	
	public void setImage(String image) 
	{
		this.image = image;
	}

	public void setName(String name) 
	{
		this.name = name;
	}
	
	public void setSource(String source) 
	{
		this.source = source;
	}
	
	public void setType(String type) 
	{
		this.type = type;
	}
	
	public void setWidth(long width) 
	{
		this.width = width;
	}
	
	public void setHeight(long height) 
	{
		this.height = height;
	}
	
	public void setScore(long score) 
	{
		this.score = score;
	}
	
	public void setUpvotes(long upvotes) 
	{
		this.upvotes = upvotes;
	}
	
	public void setDownvotes(long downvotes) 
	{
		this.downvotes = downvotes;
	}
	
	public void setFaves(long faves) 
	{
		this.faves = faves;
	}
	
	public void setComments(long comments) 
	{
		this.comments = comments;
	}
	
	public void setSize(double size) 
	{
		this.size = size;
	}
}
