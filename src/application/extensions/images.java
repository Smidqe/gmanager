package application.extensions;

import java.io.ByteArrayInputStream;

import javafx.geometry.BoundingBox;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;

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
		
		PixelReader __reader = __image.getPixelReader();

		int w = (int) __image.getWidth();
		int h = (int) __image.getHeight();
		
		//not sure why we need the * 4
		byte[] __buffer = new byte[w * h * 4];
		
		__reader.getPixels(0, 0, w, h, PixelFormat.getByteBgraInstance(), __buffer, 0, w * 4);
		
		return new Image(new ByteArrayInputStream(__buffer));
	}
	
	//gets the javafx image dimensions or bounds
	public static BoundingBox dimensions(Image __image)
	{
		return new BoundingBox(0, 0, __image.getWidth(), __image.getHeight());
	}
	
	
}
