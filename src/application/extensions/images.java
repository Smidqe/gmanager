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

	JavaFX has quite limited functionality on the image class, they cannot be cloned or anything
	
*/

public class images 
{
	//clones a javafx image (works for non animated images, not sure about apng's)

	public static Image clone(Image __image)
	{
		if (__image == null)
			throw new NullPointerException("Variable __image can't be null");

		//utilise the getBytes function to get the byte[] from javafx.image
		byte[] __stream = getBytes(__image);
		
		//create a new writable image with same width and height as the original
		WritableImage __temp = new WritableImage((int) __image.getWidth(), (int) __image.getHeight());
		
		//get the pixel writer from the empty image
		PixelWriter __writer = __temp.getPixelWriter();
		
		//copy the pixels from the source
		__writer.setPixels(0, 0, (int) __image.getWidth(), (int) __image.getHeight(), PixelFormat.getByteBgraInstance(), __stream, 0, (int) __image.getWidth() * 4);
		
		//whilst casting is not required, cast it anyways
		return (Image) __temp;
	}
	
	public static byte[] getBytes(Image __image)
	{
		if (__image == null)
			throw new NullPointerException("Variable __image can't be null");
		
		PixelReader __reader = __image.getPixelReader();

		int w = (int) __image.getWidth();
		int h = (int) __image.getHeight();
	
		//
		byte[] __buffer = new byte[w * h * 4];
		
		__reader.getPixels(0, 0, w, h, PixelFormat.getByteBgraInstance(), __buffer, 0, w * 4);

		return __buffer;
	}
	
//	public static byte[] combineGIF(Map<Integer, byte[]> __map)
//	{
//		int __total = 0;
//		
//		for (int __index : __map.keySet())
//			__total += (__map.get(__index).length);
//		
//		byte[] __result = new byte[__total];
//		
//		int __prev = 0;
//		byte[] __temp;
//		for (int i = 0 ; i < __map.size(); i++)
//		{
//			__temp = __map.get(i);
//		
//			for (int __index = 0; i < __temp.length; __index++)
//				__result[__prev + __index] = __temp[__index];
//			
//			__prev += __temp.length;
//			
//			if (__prev > __total)
//				throw new ArrayIndexOutOfBoundsException("__prev is larger than the calculated __total");
//		}
//	
//		return __result;
//	}
	
	
//	public static byte[] getBytesFromGIF(Image __image) throws IOException
//	{
//		if (__image == null)
//			return null;
//		
//		ImageReader __reader = ImageIO.getImageReadersByFormatName("gif").next();
//
//		__reader.setInput(ImageIO.createImageInputStream(__image));
//		int __count = __reader.getNumImages(true);
//
//		//byte[] __buffer = new byte[__count * (__reader.getWidth(0) * __reader.getHeight(0))];
//		
//		Map<Integer, byte[]> __result = new LinkedHashMap<Integer, byte[]>();
//		for (int i = 0; i < __count; i++)
//			__result.put(i, getBytes(SwingFXUtils.toFXImage(__reader.read(i), null))); 
//		
//		return combineGIF(__result);
//	}
//	
//	public static Image cloneGIF(Image __image) throws IOException
//	{
//		byte[] __stream = getBytesFromGIF(__image);
//		
//		WritableImage __temp = new WritableImage((int) __image.getWidth(), (int) __image.getHeight());
//		//get the pixel writer from the empty image
//		PixelWriter __writer = __temp.getPixelWriter();
//		
//		//copy the pixels from the source
//		__writer.setPixels(0, 0, (int) __image.getWidth(), (int) __image.getHeight(), PixelFormat.getByteBgraInstance(), __stream, 0, (int) __image.getWidth() * 4);
//		
//		return __temp;
//	}
	
	//gets the javafx image dimensions or bounds
	public static BoundingBox dimensions(Image __image)
	{
		return new BoundingBox(0, 0, __image.getWidth(), __image.getHeight());
	}
}
