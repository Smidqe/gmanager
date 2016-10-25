package application.types;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import application.types.TIDCreator;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

public class TImageContainer implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5108226962075856755L;
	private TImage image;
	private transient ImageView container;
	
	private String UUID;
	private boolean visible;
	
	public TImageContainer(TImage image, ImageView container) throws InterruptedException, ExecutionException
	{
		this.image = image;
		this.container = container;
		
		this.UUID = Executors.newSingleThreadExecutor().submit(new TIDCreator<>(this)).get();
	}

	public ImageView getContainer()
	{
		return this.container;
	}
	
	public TImage getImage()
	{
		return this.image;
	}
	
	public String getUUID()
	{
		return this.UUID;
	}
	
	public boolean isVisible()
	{
		return this.visible;
	}
	
	public void arm(boolean show, String size) throws InterruptedException, ExecutionException
	{
		//if there is no image assigned to this, don't proceed further.
		if (this.image == null)
			return;
		
		//should never happen, but let's pretend it can happen, there is no harm done
		if (this.container == null)
			this.container = new ImageView();
		
		//Setting the image has to happen on JavaFX thread, otherwise causes GUI corruption.
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (show)
					try {
						container.setImage((new TImageLoader(image, size)).call().get(0));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				else
					container.setImage(null);
				
			}
		});
		this.visible = show;
		
	}
}
