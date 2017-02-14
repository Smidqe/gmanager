package application.types.custom.gallery.cache;

import java.io.IOException;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import application.types.TSettings;


/*
	TODO:
		- Rewrite this POS

 */
@SuppressWarnings("unused")
public class TCacheManager2 implements Runnable
{
	public enum Status {IDLE, RUNNING, ERROR};
	
	private static TCacheManager2 __self = new TCacheManager2();
	private boolean __stop;
	
	private BlockingDeque<TCacheAction> __queue;
	private String __path; //gets it from the TSettings
	
	//the executor for IO handlers.
	private ExecutorService __executor;
	private TCacheAction __job;
	private Status __status;
	
	
	public TCacheManager2() 
	{
		this.__stop = false;
		
		this.__executor = Executors.newCachedThreadPool();
		this.__queue = new LinkedBlockingDeque<TCacheAction>();
		this.__path = TSettings.instance().getPath("cache");
	}

	
	
	public static TCacheManager2 instance()
	{
		return __self;
	}

	@Override
	public void run() 
	{
		while (!this.__stop)
		{
			this.__status = Status.IDLE;
			
			
			try {
				__job = this.__queue.take();
				
				this.__status = Status.RUNNING;
				
				
				
				
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}

	public void stop() throws InterruptedException, IOException 
	{
		this.__stop = true;
		this.__queue.put(null);
	}
}
