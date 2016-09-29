package rewrite.types;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TJobQueue
{
	private static TJobQueue __self = new TJobQueue();
	private final BlockingQueue<String> queue;

	public TJobQueue() 
	{
		this.queue = new ArrayBlockingQueue<String>(1024);
	}
	
	public void add(String message)
	{
		if (message == null)
			return;
		
		this.queue.add(message);
		notify();
	}
	
	public String take() throws InterruptedException
	{
		notify();
		return this.queue.take();
	}
	
	public static TJobQueue instance()
	{
		return __self;
	}
}
