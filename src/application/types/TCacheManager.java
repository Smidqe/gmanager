package application.types;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;

import application.extensions.arrays;
import application.types.factories.FThreadFactory;
import application.types.images.saver.TImageIOHandler;
import javafx.scene.image.Image;

/*
	TODO:
		- Clean this POS.
		- Probably 
 */

public class TCacheManager implements Runnable
{
	public Object lock = new Object();
	private static TCacheManager __self = new TCacheManager();
	private TSettings __settings = TSettings.instance();
	
	private boolean __stop;
	//private 
	private BlockingDeque<TCacheJob> __data;
	
	private Map<String, String> ids;
	private Map<String, Future<TResult<Image>>> __output;
	//the executor for IO handlers.
	private ExecutorService __executor;
	
	
	public TCacheManager() 
	{
		this.ids = new WeakHashMap<String, String>();
		this.__stop = false;
		
		this.__output = new ConcurrentHashMap<String, Future<TResult<Image>>>();
		this.__executor = Executors.newCachedThreadPool(new FThreadFactory("TCacheManager", "Subthreads", true));
		this.__data = new LinkedBlockingDeque<TCacheJob>();

	}
	
	public void save(Image img, String ID, String type) throws InterruptedException
	{
		if (img == null)
			System.out.println("img is null");
		
		//check if we already have the file in our system
		for (String id : ids.keySet())
			if (id.equals(ID))
				return;
		
		save(Arrays.asList(img), Arrays.asList(ID), Arrays.asList(type));
	}
	
	//return a list of futures, so that we can wait while they are being written (before showing them, prevents nulls)
	public void save(List<Image> images, List<String> IDs, List<String> types) throws InterruptedException
	{
		if (images.size() != IDs.size())
			return;

		for (int i = 0; i < images.size(); i++)
			this.__data.put(createJob(TCacheJob.Method.SAVE, IDs.get(i), images.get(i), types.get(i)));
	}
	
	public TCacheJob createJob(TCacheJob.Method method, String ID, Image img, String type)
	{
		if (img == null)
			System.out.println("img is null");
		
		TCacheJob job = new TCacheJob();
		
		job.setID(ID);
		job.setImage(img);
		job.setMethod(method);
		job.setType(type);
		return job;
	}
	
	public void load(String ID, String type) throws InterruptedException
	{
		//check if we already have a job for this ID
		load(Arrays.asList(ID), Arrays.asList(type));
		
		
	}
	
	public TCacheJob getJob(String ID)
	{
		if (__data.size() == 0)
			return null;
			
		Iterator<TCacheJob> iterator = __data.iterator();
		TCacheJob job = null;
		while (iterator.hasNext())
		{
			job = iterator.next();
			
			if (job.getID().equals(ID))
				return job;
		}
		
		return null;
	}
	
	public void stop(String ID)
	{
		Iterator<String> __iterator = __output.keySet().iterator();
		
		while (__iterator.hasNext())
		{
			String __key = __iterator.next();
			
			if (!__key.equals(ID))
				continue;
			
			Future<TResult<Image>> __job = __output.get(__key);
			__job.cancel(true);
		}
	}
	
	public boolean jobExists(String ID)
	{
		if (__data.size() == 0)
			return false;
			
		Iterator<TCacheJob> iterator = __data.iterator();
		TCacheJob job = null;
		while (iterator.hasNext())
		{
			job = iterator.next();
			
			if (job.getID().equals(ID))
				return true;
		}
		
		return false;
	}
	
	public void load(List<String> IDs, List<String> types) throws InterruptedException
	{
		for (int i = 0; i < IDs.size(); i++)
		{
			if (jobExists(IDs.get(i)))
				continue;
			
			this.__data.put(createJob(TCacheJob.Method.LOAD, IDs.get(i), null, types.get(i)));
		}
		System.out.println("TCacheManager.load(): Added jobs");
	}
	
	public synchronized Map<String, Future<TResult<Image>>> getCurrentJobs()
	{
		return this.__output;
	}
	
	public boolean exists(String ID) 
	{
		for (String id : this.ids.keySet())
			if (id.equals(ID))
				return true;
		
		return false;
	}

	public void clear() throws IOException
	{
		File temp = null;
		for (String ID : ids.keySet())
		{
			System.out.println("TCacherManager: Deleting: " + ID);
			
			temp = new File(__settings.getPath("cache") + ID + ids.get(ID));
			temp.delete();
		}
		
		ids.clear();
	}

	public static TCacheManager instance()
	{
		return __self;
	}

	@Override
	public void run() 
	{
		TCacheJob job;
		TImageIOHandler __handler = null;
		boolean __load;
		Future<TResult<Image>> __future = null;
		
		TImageIOHandler.Method __method = null;
		
		while (!this.__stop)
		{
			try {
				job = __data.take();
				
				if (this.__stop || job.getID().equals(""))
					break;
				
				if (job.getImage() == null)
					System.out.println("job.getImage: image == null");
				
				__load = job.getMethod() == TCacheJob.Method.LOAD;
				__method = __load ? TImageIOHandler.Method.LOAD : TImageIOHandler.Method.SAVE;
				//change
				__handler = new TImageIOHandler(__method, __settings.getPath("cache") + job.getID(), job.getType(), job.getImage(), job.getType());

				//Add it to current jobs
				__future = __executor.submit(__handler);
				this.__output.put(job.getID(), __future);
				this.ids.put(job.getID(), job.getType());
				
				//wait for the image to write
				synchronized (__handler.lock)
				{
					__handler.lock.wait();
				}
				synchronized (lock) {
					lock.notify();
				}
				
			} catch (InterruptedException e) {
				
				if (__method == TImageIOHandler.Method.LOAD)
					System.out.println("We are loading");
				else
					System.out.println("We are saving");
				
				e.printStackTrace();
			}
		}
		
		System.out.println("TCacheManager: Shutting down");
	}

	public void stop() throws InterruptedException, IOException 
	{
		this.__stop = true;
		this.__data.put(new TCacheJob("", null, ""));
		
		clear();
	}
}
