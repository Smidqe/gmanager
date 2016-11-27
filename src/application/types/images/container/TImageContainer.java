package application.types.images.container;

import java.util.concurrent.ExecutionException;

import application.types.TCacheManager;
import application.types.TImage;
import application.types.TImage.Maps;
import application.types.TImageLoader;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TImageContainer implements Runnable
{

	//private static final long serialVersionUID = 7636870256042424249L;
	private TImage __image_data;
	private ImageView __container;
	
	private boolean __show, __visible;
	private Image __image;
	private String __size;
	
	public TImageContainer(TImage image, ImageView container) throws InterruptedException, ExecutionException 
	{
		this.__image_data = image;
		this.__container = container;
		//this.__UUID = Executors.newSingleThreadExecutor().submit(new TIDCreator<>(this)).get();
	}

	public void show(boolean value)
	{
		this.__show = value;
	}
	
	public void setSize(String size)
	{
		this.__size = size;
	}
	
	public boolean isVisible()
	{
		return this.__visible;
	}
	
	public TImage getImageContainer()
	{
		return this.__image_data;
	}
	
	public ImageView getImageView()
	{
		return this.__container;
	}
	
	//Basically this one just arms/disarms the image 
	@Override
	public void run() 
	{
		if (__image_data == null)
			return;

		//System.out.println("Data: " + this.__image_data.getMap(Maps.MAP_IMAGES).toString());
		
		try {

			if (this.__show && !this.__visible)
			{
				__image = new TImageLoader(this.__image_data, this.__size).call().get(0);
	
				//probably add a timer here to make sure that it wont be an infinite loop
				while (__image == null)
					Thread.sleep(1);
			}

			Platform.runLater(new Runnable() 
			{
				@Override
				public void run() 
				{
					__container.setImage(__show ? __image : null);
				}
			});

			this.__visible = this.__show;
			
			if (!this.__visible)
				this.__image = null;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

			
	}
}
