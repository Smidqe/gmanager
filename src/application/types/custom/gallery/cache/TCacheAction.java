package application.types.custom.gallery.cache;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import application.extensions.images;
import javafx.scene.image.Image;

public class TCacheAction implements Runnable
{
	public enum Status {IDLE, RUNNING}
	public enum Action {NULL, READ, WRITE}
	
	private Image __image;
	private boolean __stop;
	private Action __action;
	private String __id;
	private String __folder;
	
	public TCacheAction(String id) 
	{
		this.__image = null;
		this.__id = id;
	}

	//mainly used for reading to get the image
	public Image getImage()
	{
		//clone the image
		Image __temp = images.clone(__image);
		
		//discard the loaded image
		this.__image = null;
		
		//return the cloned image
		return __temp;
	}

	public void setAction(Action action)
	{
		this.__action = action;
	}
	
	public void setImage(Image __img)
	{
		this.__image = __img;
	}
	
	public void read()
	{
		
	}
	
	public void write()
	{
		//dont write if the file already exists (there is no reason)
		if (Files.exists(Paths.get(__folder, __id), new LinkOption[] {LinkOption.NOFOLLOW_LINKS}))
			return;
		
		
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
			this.__action = Action.NULL; //set the action to null
			
			try {
				//wait until we have been given a task
				while (this.__action == Action.NULL)
					Thread.sleep(1);
			
				switch (this.__action) {
				case READ:
				{
					break;
				}
				case WRITE:
				{
					if (this.__image != null)
						break;
						
					write();
					
					break;
				}
					
				default:
					break;
				}
				
			
			
			} catch (InterruptedException e) {
				// TODO: handle exception
			}
		}
	}
}
