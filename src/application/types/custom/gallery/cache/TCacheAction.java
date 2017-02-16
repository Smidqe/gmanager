package application.types.custom.gallery.cache;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import application.extensions.images;
import application.types.TSettings;
import javafx.scene.image.Image;

public class TCacheAction implements Runnable
{
	public enum cStatus {IDLE, RUNNING}
	public enum cAction {NULL, READ, WRITE}
	
	private Image __image;
	private boolean __stop;
	private cAction __action;
	private String __id;
	private String __folder;
	
	//rest of the variables will be handled in TCacheManager
	public TCacheAction(String id, Image image) 
	{
		this.__image = image;
		this.__id = id;
		this.__folder = TSettings.instance().getPath("cache");
	}

	//mainly used for reading to get the image
	public Image getImage()
	{
		if (this.__image == null)
			return null;
		
		//clone the image
		Image __temp = images.clone(__image);
		
		//discard the loaded image
		this.__image = null;
		
		//return the cloned image
		return __temp;
	}

	public String getID()
	{
		return this.__id;
	}
	
	public void setID(String id)
	{
		this.__id = id;
	}
	
	public void setFolder(String folder)
	{
		this.__folder = folder;
	}
	
	public void setAction(cAction action)
	{
		this.__action = action;
	}
	
	public cAction getAction()
	{
		return this.__action;
	}
	
	public void setImage(Image __img)
	{
		this.__image = images.clone(__img);
	}
	
	public void read() throws IOException
	{
		if (Files.exists(Paths.get(__folder, __id), new LinkOption[] {LinkOption.NOFOLLOW_LINKS}))
			return;
		
		//not sure how to approach this, perhaps turning a file input into a byte[] array and from there creating the image? Since gif reading doesn't work
		byte[] __bytes = Files.readAllBytes(Paths.get(this.__folder, this.__id));
		this.__image = new Image(new ByteArrayInputStream(__bytes));
	}
	
	public void write() throws IOException
	{
		//dont write if the file already exists (there is no reason)
		System.out.println(Paths.get(__folder, __id).toString());
		
		if (Files.exists(Paths.get(__folder, __id), new LinkOption[] {LinkOption.NOFOLLOW_LINKS}))
			return;
		
		Files.write(Paths.get(__folder, __id), images.getBytes(this.__image), StandardOpenOption.CREATE);
	}
	
	public void stop()
	{
		this.__stop = true;
	}

	@Override
	public void run() 
	{
		while (!this.__stop)
		{
			this.__action = cAction.NULL; //set the action to null
			
			try {
				//wait until we have been given a task
				while (this.__action == cAction.NULL)
					Thread.sleep(1);
			
				switch (this.__action) {
				case READ:
				{
					read();
					break;
				}
				
				case WRITE:
				{
					//dont' even start running if we don't have image
					if (this.__image == null)
						break;
						
					write();
					break;
				}
					
				default:
					break;
				}
			} catch (InterruptedException | IOException e) {
				// TODO: handle exception
			}
		}
		
		System.out.println("TCacheAction: Shutting down");
	}
}
