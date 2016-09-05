package application.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import javafx.scene.control.ProgressBar;

public class TDownload implements Runnable
{
	public enum statusID
	{
		IDLE, STARTED, RUNNING, FINISHED, CANCELLED, ERROR;
	}

	private static final int MAX_BUFFER_SIZE = 2048;
	
	private boolean trackProgress;
	private String targetPath;
	private ProgressBar bar;
	private statusID status;
	private TConnection connection;
	
	private int downloaded, size;
	
	public TDownload(URL url, String path)
	{
		this.connection = new TConnection(url);
		this.targetPath = path;
		this.status = statusID.IDLE;
	}
	
	public TDownload(String url, String path) throws MalformedURLException
	{
		this(new URL(url), path);
	}
	
	public statusID state()
	{
		return this.status;
	}
	
	public void bind(ProgressBar bar)
	{
		this.bar = bar;
	}
	
	public String targetPath()
	{
		return this.targetPath;
	}
	
	public String fileName()
	{
		String url = connection.getURL().getFile();
		return url.substring(url.lastIndexOf('/') + 1);		
	}
	
	@Override
	public void run() 
	{
		RandomAccessFile file = null;
		InputStream stream = null;
		
		this.status = statusID.STARTED;
		
		try {
			HttpsURLConnection https = connection.getConnection();
			
			if (connection.ping() != 200)
				this.status = statusID.ERROR;
			
			https.setRequestProperty("Range", "bytes=" + downloaded + "-");

			int content = 0;
			if ((content = https.getContentLength()) < 1)
				this.size = content;
			else
				this.status = statusID.ERROR;
			
			file = new RandomAccessFile(this.targetPath + fileName(), "rw");
			file.seek(this.downloaded);
			
			stream = https.getInputStream();
			while (this.status.equals(statusID.RUNNING))
			{
				byte buffer[];
				
				if (size - this.downloaded > MAX_BUFFER_SIZE)
					buffer = new byte[MAX_BUFFER_SIZE];
				else
					buffer = new byte[size - downloaded];
				
				int read = stream.read(buffer);
				
				if (read == -1)
					break;
				
				file.write(buffer, 0, read);
				this.downloaded += read;
				
				if (this.trackProgress && this.bar != null)
					bar.setProgress((size - downloaded) / size);		
			}
			
			if (this.status.equals(statusID.RUNNING) && (size - downloaded == 0))
				this.status = statusID.FINISHED;
			else
				this.status = statusID.ERROR;
			
			
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if (file != null)
				try {
					file.close();
				} catch (IOException e) {}
		
			if (stream != null)
				try {
					stream.close();
				} catch (IOException e) {}
		}
	}

}
