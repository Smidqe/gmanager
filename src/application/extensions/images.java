package application.extensions;

import javafx.geometry.BoundingBox;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

/*
	Just a random collection of useful and not so useful functions that 
	expand the functionality of javafx image class. 
 */

public class images 
{
	//clones a javafx image
	public static Image clone(Image __image)
	{
		if (__image == null)
			throw new NullPointerException("Variable __image can't be null");

		//utilise the below function to get the byte[] from javafx.image
		byte[] __stream = getBytes(__image);
		WritableImage __temp = new WritableImage((int) __image.getWidth(), (int) __image.getHeight());
		PixelWriter __writer = __temp.getPixelWriter();
		
		__writer.setPixels(0, 0, (int) __image.getWidth(), (int) __image.getHeight(), PixelFormat.getByteBgraInstance(), __stream, 0, (int) __image.getWidth() * 4);
		
		return (Image) __temp;
	}
	
	public static byte[] getBytes(Image __image)
	{
		if (__image == null)
			throw new NullPointerException("Variable __image can't be null");
		
		PixelReader __reader = __image.getPixelReader();

		int w = (int) __image.getWidth();
		int h = (int) __image.getHeight();
		
		System.out.println("w: " + w + ", h: " + h);
		System.out.println("__reader: " + __reader);
		//not sure why we need the * 4
		byte[] __buffer = new byte[w * h * 4];
		
		__reader.getPixels(0, 0, w, h, PixelFormat.getByteBgraInstance(), __buffer, 0, w * 4);

		return __buffer;
	}
	
	//gets the javafx image dimensions or bounds
	public static BoundingBox dimensions(Image __image)
	{
		return new BoundingBox(0, 0, __image.getWidth(), __image.getHeight());
	}
}
