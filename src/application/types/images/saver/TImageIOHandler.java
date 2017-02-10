package application.types.images.saver;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

import application.types.TResult;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

/*
	Return something else than a Map
	Perhaps a custom type?
 */

public class TImageIOHandler implements Callable<TResult<Image>>
{
	public enum Method {SAVE, LOAD};

	private String __ID, path, type;
	private Image __img;
	private Method __method;
	public Object lock = new Object();

	public TImageIOHandler(Method method, String path, String ID, Image img, String type) 
	{
		this.__ID = ID;
		this.__method = method;
		this.path = path;
		this.__img = img;
		this.type = type;
	}

	public TResult<Image> saveFXImage(Image __image, String type) throws Exception 
	{
		if (__image == null)
			throw new NullPointerException("saveFXImage: __image == null");

		System.out.println("__image == null: " + __image == null);
		
		TResult<Image> img = new TResult<Image>();
		
		img.set_method(TResult.Method.SAVE);
		img.set_image(this.__ID);

		boolean success = false;
		//there is a problem with gif files
		if (type.equals("gif"))
		{
			System.out.println("Done: " + __image.getProgress());
			System.out.println("Errors: " + __image.isError());
		
			BufferedImage temp = null;
			SwingFXUtils.fromFXImage(__image, temp);
			WritableImage newImage = null;
			SwingFXUtils.toFXImage(temp, newImage);
			
			__image = newImage;
		}
		
		success = ImageIO.write(SwingFXUtils.fromFXImage(__image, null), type, new File(this.path + this.__ID));

		System.out.println("Image writing: " + success);

		synchronized (lock) {
			lock.notify();
		}
		
		if (!success)
		{
			img.set_status(TResult.Status.ERROR);
			throw new Exception("TCacheManager: Image was not saved");
		}
		else
		{
			img.set_status(TResult.Status.SUCCESS);
			return img;
		}
	}
	
	/*
	
	Testing if this sees as a javadoc.
	
	 */
	public TResult<Image> readFXImage() throws IOException
	{
		TResult<Image> __result = new TResult<Image>();
		
		__result.set_method(TResult.Method.SAVE);
		__result.set_image(this.__ID);
		__result.set_status(TResult.Status.ERROR);
		
		File file = null;
		if ((file = new File(this.path + this.__ID)).exists())
		{
			System.out.println("File exists: " + this.path);
			System.out.println("File: Read: " + file.canRead() + ", Write: " + file.canWrite());
		}
		
		
		//wait until it can be read
		
		__result.set((Image) SwingFXUtils.toFXImage(ImageIO.read(new File(this.path + this.__ID)), null));
		__result.set_status(TResult.Status.SUCCESS);
	
		synchronized (lock) {
			lock.notify();
		}
		
		return __result;
	}
	
	
	/*
	Will be called always no matter if we read or save an image. There will be no overlapping IDs
	 */
	@Override
	public TResult<Image> call() 
	{
		//System.out.println("TImageIOHandler: Doing stuff");
		
		//TResult<Image> img = null;
		try {
			switch (this.__method) {
			case LOAD:
			{	
				return readFXImage();
			}
			
			case SAVE:
			{
				System.out.println("We are saving");
				System.out.println("Is image null: " + this.__img == null);
				
				return saveFXImage(this.__img, this.type);
			}
			default:
				break;
			}
		} catch (Exception e) {
			System.out.println("TImageIOHandler: Exception: " + e.getMessage());
			System.out.println("Is image null: " + this.__img == null);
			e.printStackTrace();
		}
		System.out.println("TImageIOHandler: We shouldn't be here!");
		
		return null;
	}
	
}
