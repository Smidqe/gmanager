package rewrite.types.queues;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class TRefresherQueue 
{
	private static TRefresherQueue instance = new TRefresherQueue();
	private BlockingDeque<String> queue;
	
	private TRefresherQueue() 
	{
		queue = new LinkedBlockingDeque<String>();
	}
	
	public synchronized String take() throws InterruptedException
	{
		return this.queue.take();
	}
	
	public synchronized void put(String object) throws InterruptedException
	{
		this.queue.addLast(object);
		notify();
	}
	
	public synchronized int size()
	{
		return this.queue.size();
	}
	
	public BlockingDeque<String> getDeque()
	{
		return this.queue;
	}
	
	public static TRefresherQueue instance()
	{
		return instance;
	}
}
