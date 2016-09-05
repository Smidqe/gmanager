package application.web;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class TImageDownload extends Observable implements Runnable
{	
	public enum Status
	{
		IDLE, RUNNING, FINISHED, INTERRUPTED, ERROR;
	}
	
	private Image image;
	private TConnection connection;
	private Status status;
	
	public TImageDownload(URL url)
	{
		this.connection = new TConnection(url);
		this.status = Status.IDLE;
	}
	
	public TImageDownload(String url) throws MalformedURLException
	{
		this(new URL(url));
	}

	public void stop()
	{
		this.status = Status.INTERRUPTED;
	}
	
	public synchronized Image getImage()
	{
		return image;
	}
	
	private WritableImage convert(BufferedImage image)
	{
		return SwingFXUtils.toFXImage(image, null);
	}
	
	@Override
	public void run() 
	{
		this.status = Status.RUNNING;
		changed();
		
		while (this.image == null && (!status.equals(Status.FINISHED) || !status.equals(Status.INTERRUPTED) || !status.equals(Status.ERROR)))
		{
			
			if (status.equals(Status.IDLE))
			{
				
				try {
					if (connection.ping() != 200)
						break;
	
					HttpsURLConnection __connection = connection.getConnection();
					
					__connection.connect();

					InputStream stream = __connection.getInputStream();
					synchronized (this)
					{
						this.image = convert(ImageIO.read(stream));
						this.status = Status.FINISHED;
				
						stream.close();
					}
					changed();
				} catch (IOException e) {
					this.status = Status.ERROR;
				}
			}
		}
	}
	
	public void changed()
	{
		setChanged();
		notifyObservers();
	}

	public Status getStage() {
		// TODO Auto-generated method stub
		return this.status;
	}
}
