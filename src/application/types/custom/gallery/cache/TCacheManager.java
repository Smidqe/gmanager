package application.types.custom.gallery.cache;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import application.types.TSettings;


/*
	TODO:
		- Wait for the 
		

 */

public class TCacheManager implements Runnable
{
	public enum Status {IDLE, RUNNING, ERROR};
	
	private static TCacheManager __self = new TCacheManager();
	private boolean __stop;
	
	private BlockingDeque<TCacheAction> __queue;
	private List<TCacheAction> __managed;
	private String __path;
	
	//the executor for IO handlers.
	private ExecutorService __executor;
	private TCacheAction __job;
	private Status __status;
	
	private TCacheManager() 
	{
		this.__stop = false;
		
		this.__executor = Executors.newCachedThreadPool();
		this.__queue = new LinkedBlockingDeque<TCacheAction>();
		this.__path = TSettings.instance().getPath("cache");
		this.__managed = new ArrayList<TCacheAction>();
		
		this.__status = Status.IDLE;
	}

	public void add(TCacheAction action)
	{
		if (action == null)
			throw new NullPointerException("Variable is null, can't add a null object to queue");

		this.__queue.add(action);
	}
	
	public TCacheAction get(String id) throws InterruptedException
	{
		if (__managed == null)
			throw new NullPointerException("__managed list is not initialized");
		
		if (__managed.size() == 0)
			throw new ArrayIndexOutOfBoundsException("__managed's size is 0");

		TCacheAction __current = null;
		
		for (TCacheAction action : this.__managed)
			if (action.getID().equals(id))
			{
				__current = action;
				break;
			}
		
		return __current;
	}
	
	public boolean exists(String id) throws InterruptedException
	{
		if (__managed.size() == 0)
			return false;
		
		return (get(id) != null);
	}
	
	public static TCacheManager instance()
	{
		return __self;
	}

	public Status getStatus()
	{
		return this.__status;
	}
	
	@Override
	public void run() 
	{
		while (!this.__stop)
		{
			this.__status = Status.IDLE;

			try {
				__job = this.__queue.take();
	
				//means that we have gotten a stop call.
				if (__job.getID().equals("-1"))
					continue;
					
				this.__status = Status.RUNNING;
				
				__job.setFolder(__path);
				
				__executor.submit(__job);
				__managed.add(__job);
				
				//what else to do here?
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}

	public void stop() throws InterruptedException, IOException 
	{
		this.__stop = true;
		this.__queue.put(new TCacheAction("-1", null));
		
		for (TCacheAction action : __managed)
		{
			action.stop();
			if (Files.exists(Paths.get(__path, action.getID()), new LinkOption[] {LinkOption.NOFOLLOW_LINKS}))
				Files.delete(Paths.get(__path, action.getID()));
		}
	}
}
