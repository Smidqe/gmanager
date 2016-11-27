package application.types;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import application.types.TImage.Maps;
import application.types.images.container.TImageContainer;


public class TImageSaver implements Runnable
{
	public enum SaverStatus {IDLE, RUNNING, ERROR};
	
	private boolean __stop = false;
	private TCacheManager __manager;
	private BlockingDeque<TImageContainer> __images_to_save;
	
	public TImageSaver() 
	{
		this.__images_to_save = new LinkedBlockingDeque<TImageContainer>();
	}

	public void add(TImageContainer container) throws InterruptedException
	{
		this.__images_to_save.put(container);
	}
	
	public void add(List<TImageContainer> list) throws InterruptedException
	{
		for (TImageContainer container: list)
			add(container);
	}
	
	public void bind(TCacheManager manager)
	{
		this.__manager = manager;
	}
	
	public void stop()
	{
		this.__stop = true;
	}
	
	@Override
	public void run() 
	{
		TImageContainer container = null;

		// TODO Auto-generated method stub
		while (!this.__stop)
		{
			container = null;
			
			while (!this.__stop)
			{
				//check if there is any items in the deque
					try {
						container = __images_to_save.take();
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				
				
				try {
					System.out.println("Waiting for it to finish.");
					
					while (container.getImageView().getImage() == null)
						Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			if (this.__stop)
				break;

			System.out.println("Saver: Running");
			
			synchronized (this) {
				this.notify();
			}
		
			
			try {
				__manager.saveFXImage(container.getImageView().getImage(), container.getImageContainer().getProperty(Maps.MAP_PROPERTIES, "id"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			__manager.clear();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
