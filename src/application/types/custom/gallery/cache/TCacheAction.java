package application.types.custom.gallery.cache;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import application.extensions.images;
import application.types.TSettings;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/*
 	TODO:
 		
 */

public class TCacheAction implements Runnable
{
	public enum Status {IDLE, RUNNING, ERROR}
	public enum Action {NULL, READ, WRITE}
	
	private Image __image;
	private String __format;
	private boolean __stop;
	private Action __action;
	private Status __status;
	private String __id;
	private String __folder;
	private int __height, __width;
	
	//rest of the variables will be handled in TCacheManager
	public TCacheAction(String id, Image image) 
	{
		this.__image = image;
		this.__id = id;
		this.__folder = TSettings.instance().getPath("cache");
		this.__action = Action.NULL;
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

	public void setDimensions(int w, int h)
	{
		this.__width = w;
		this.__height = h;
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
	
	public void setAction(Action action)
	{
		this.__action = action;
	}
	
	public Action getAction()
	{
		return this.__action;
	}
	
	public Status getStatus()
	{
		return this.__status;
	}
	
	public void setImage(Image __img)
	{
		this.__image = images.clone(__img);
	}
	

	public void setFormat(String format) {
		this.__format = format;
	}
	
	public void read() throws IOException, InterruptedException
	{
		if (!Files.exists(Paths.get(__folder, __id), new LinkOption[] {LinkOption.NOFOLLOW_LINKS}))
			return;

		//not sure how to approach this, perhaps turning a file input into a byte[] array and from there creating the image? Since gif reading doesn't work
		//byte[] __bytes = Files.readAllBytes(Paths.get(this.__folder, this.__id));

		File __file = Paths.get(__folder, __id).toFile();
		
		while (!__file.canRead())
			Thread.sleep(1);
		
		ByteArrayInputStream __stream = new ByteArrayInputStream(Files.readAllBytes(Paths.get(this.__folder, this.__id)));
		
		
		this.__image = SwingFXUtils.toFXImage(ImageIO.read(__stream), null);
	}
	
	public void write() throws InterruptedException
	{
		//dont write if the file already exists (there is no reason)
		if (Files.exists(Paths.get(__folder, __id), new LinkOption[] {LinkOption.NOFOLLOW_LINKS}))
			return;
		
		File __file = null;
		try {
			__file = Files.createFile(Paths.get(__folder, __id)).toFile();
		} catch (IOException e1) {
			this.__status = Status.ERROR;
			return;
		}

		if (__file == null)
		{
			this.__status = Status.ERROR;
			return;
		}
		
		while (!__file.canWrite())
			Thread.sleep(1);
		
		//this works for other images except gifs.
		try
		{
			//Files.write(Paths.get(__folder, __id), images.getBytes(__image), StandardOpenOption.CREATE);
			WritableImage __temp = new WritableImage(__width, __height);
			
			PixelWriter __writer = __temp.getPixelWriter();
			PixelReader __reader = __image.getPixelReader();
			
			
			byte[] __stream = new byte[__width * __height * 4];
			
			__reader.getPixels(0, 0, __width, __height, PixelFormat.getByteBgraInstance(), __stream, 0, __width * 4);
			__writer.setPixels(0, 0, __width, __height, PixelFormat.getByteBgraInstance(), __stream, 0, (int) __image.getWidth() * 4);

			ImageIO.write(SwingFXUtils.fromFXImage(__temp, null), __format, __file);
		}
		catch (IOException e)
		{
			System.out.println("-- EXCEPTION: ID: " + this.__id);
			e.printStackTrace();
			System.out.println("-- END --");
		}
		//Files.write(Paths.get(__folder, __id), images.getBytes(this.__image), StandardOpenOption.CREATE);
	}
	
	public void stop()
	{
		this.__stop = true;
	}

	@Override
	public void run() 
	{
		System.out.println("TCacheAction: " + this.__id + " started.");
		System.out.println("Preliminary action: " + this.__action);
		while (!this.__stop)
		{
			this.__status = Status.IDLE;
			
			try {
				//wait until we have been given a task
				while (this.__action == Action.NULL)
					Thread.sleep(1);
			
				this.__status = Status.RUNNING;
				
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
				e.printStackTrace();
			}
			
			this.__action = Action.NULL; //set the action to null
		}
		
		System.out.println("TCacheAction: Shutting down");
	}

}
