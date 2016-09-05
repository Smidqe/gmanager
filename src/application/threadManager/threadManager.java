package application.threadManager;

import java.util.ArrayList;

public class threadManager {
	private ArrayList<Thread> __threads;
	private static threadManager __self = new threadManager();
	
	private threadManager()
	{
		__threads = new ArrayList<Thread>();
	}

	private int get(String name)
	{
		for (int i = 0; i < __threads.size(); i++)
			if (__threads.get(i).getName().equals(name))
				return i;
		
		return -1;
	}
	
	public void start(String name)
	{
		int index = get(name);
		
		if (index == -1)
			return;
		
		__threads.get(index).start();
	}
	
	public void start(int index)
	{
		__threads.get(index).start();
	}
	
	public void startAll()
	{
		for (Thread t : __threads)
			t.start();
	
	}
	
	public void stopAll() throws InterruptedException
	{
		for (Thread t : __threads)
			t.join();
		
	}
	
	public threadManager instance()
	{
		return __self;
	}
}
