package application.types.utilities;

import java.util.Calendar;
@SuppressWarnings("unused")
public class TTimer 
{
	private String __name;
	private long __start, __total, __prev;
	private boolean __paused;	
	
	public TTimer() 
	{
		this.__paused = true;
		this.__start = 0;
		this.__total = 0;
		this.__prev = 0;
	}
	
	public long getCurrentTime()
	{
		return Calendar.getInstance().get(Calendar.MILLISECOND);
	}
	
	public void start()
	{		
		this.__start = getCurrentTime();
		this.__paused = false;
	}
	
	public void pause()
	{
		this.__paused = true;
		this.__total += getCurrentTime() - this.__prev;
		this.__prev = getCurrentTime();
	}
	
	public void stop()
	{
		if (!this.__paused)
			pause();
		
		
	}
	
	public void reset()
	{
		this.__paused = false;
		this.__total = 0;
		this.__prev = getCurrentTime();
	}
	
	public long getTime()
	{
		if (this.__paused)
			return this.__total + getCurrentTime();
		else
			return this.__total;
	}
	
	public long getTotalTime()
	{
		return this.__total;
	}
}
