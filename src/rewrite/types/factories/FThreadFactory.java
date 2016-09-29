package rewrite.types.factories;

import java.util.concurrent.ThreadFactory;

public class FThreadFactory implements ThreadFactory
{
	private String name = "";
	private String object = "";
	private int count = 0;
	private boolean daemon = true;
	
	public FThreadFactory(String name, String object, boolean daemon) 
	{
		this.name = name;
		this.object = object;
		this.daemon = daemon;
	}

	@Override
	public Thread newThread(Runnable r) {
		// TODO Auto-generated method stub
		Thread t = new Thread(r);
		
		t.setName(this.name + " " + object + " " + count++);
		t.setDaemon(this.daemon);
		
		return t;
	}

}
