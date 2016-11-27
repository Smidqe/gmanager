package application.types;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import javax.imageio.ImageIO;

import application.extensions.arrays;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

/*
	TODO:
		- Make better save/load methods, currently we can't apply every class to a binary file due to serialization limitations
		- Make it non singleton, we need to have multiple cache folders (if there is multiple) 
 */

public class TCacheManager implements Runnable
{
	public enum Methods {METHOD_SAVE, METHOD_LOAD, METHOD_STOP}
	
	
	private List<String> ids;
	private static TCacheManager __self = new TCacheManager();
	private TSettings __settings = TSettings.instance();
	private boolean __stop;
	private BlockingDeque<Map<String, Image>> __images_to_save;
	private BlockingDeque<String> __images_to_read;
	private BlockingDeque<Methods> __method;
	private BlockingDeque<Map<String, Image>> __loaded;

	
	public TCacheManager() 
	{
		this.ids = new ArrayList<String>();
		this.__stop = false;
		this.__images_to_save = new LinkedBlockingDeque<Map<String, Image>>();
		this.__images_to_read = new LinkedBlockingDeque<String>();
		this.__method = new LinkedBlockingDeque<Methods>();
		this.__loaded = new LinkedBlockingDeque<Map<String, Image>>();
	}
	
	//this should serialize all the objects(classes) that you have
	@SuppressWarnings("unused")
	private <T> byte[] serialize(T object)
	{
		return null;
	}
	
	public void add(List<Image> imgs, List<String> IDs) throws InterruptedException
	{
		//check if they are not the same size;
		
		for (int i = 0; i < imgs.size(); i++)
		{
			if (exists(IDs.get(i)))
				continue;
			WeakHashMap<String, Image> map = new WeakHashMap<String, Image>();
			map.put(IDs.get(i), imgs.get(i));
			this.__images_to_save.put(map);
		}
	}
	
	public void add(Image img, String ID) throws InterruptedException
	{
		add(Arrays.asList(img), Arrays.asList(ID));
	}
	
	public void start(Methods method) throws InterruptedException
	{
		this.__method.put(method);
	}

	public void load(String iD) throws InterruptedException 
	{
		this.__images_to_read.put(iD);
	}
	
	public BlockingDeque<Map<String, Image>> getLoaded() 
	{
		return __loaded;
	}
	
	public boolean exists(String ID) 
	{
		return arrays.exists(ids, ID);
	}

	//JavaFX images cannot be serialized, therefore they cannot be saved by normal means, I want to replace this with a more general save method
	public void saveFXImage(Image __image, String ID) throws Exception 
	{
		if (__image == null)
			System.out.println("Image is null");
		
		//System.out.println("Image: " + __image.toString() + ", ID: " + ID);
		
		boolean success = ImageIO.write(SwingFXUtils.fromFXImage(__image, null), "png", new File(__settings.getPaths().get("cache") + ID));

		System.out.println("Image writing: " + success);
		
		if (success)
			ids.add(ID);
		else
			throw new Exception("TCacheManager: Image was not saved");
	}
	
	public Image readFXImage(String ID) throws IOException
	{
		if ((arrays.position(ids, ID)) == -1)
			return null;

		return (Image) SwingFXUtils.toFXImage(ImageIO.read(new File(__settings.getPaths().get("cache") + ID)), null);
	}
	
	public void clear() throws IOException
	{
		File temp = null;
		for (String ID : ids)
		{
			System.out.println("TCacherManager: Deleting: " + ID);
			
			temp = new File(__settings.getPath("cache") + ID);
			temp.delete();
		}
		
		ids.clear();
	}

	public static TCacheManager instance()
	{
		return __self;
	}

	@Override
	public void run() {
		
		Map<String, Image> img = null;
		Methods value = null;
		while (!this.__stop)
		{
			try {
				value = __method.take();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if (value == Methods.METHOD_STOP || this.__stop)
			{
				try {
					this.clear();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;
			}	
			
			if (value == Methods.METHOD_SAVE)
			{
				try {
					img = __images_to_save.take();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					saveFXImage(img.get(img.keySet().toArray()[0]), (String) img.keySet().toArray()[0]);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				String ID = null;
				Image __image = null;
				
				try {
					ID = __images_to_read.take();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					__image = readFXImage(ID);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
				
				Map<String, Image> map = new WeakHashMap<String, Image>();
				
				map.put(ID, __image);
				try {
					__loaded.put(map);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}

	public void stop() throws InterruptedException 
	{
		this.__stop = true;
		this.__method.put(Methods.METHOD_STOP);
	}
}
