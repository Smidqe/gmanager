package application.types;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.UUID;
import java.util.concurrent.Callable;

public class TIDCreator<T> implements Callable<String>{

	private T variable;
	
	public TIDCreator(T object)
	{
		this.variable = object;
	}
	
	private byte[] bytes() throws IOException
	{
	    try 
	    (
	    		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		        ObjectOutput out = new ObjectOutputStream(bos)
		) 
	    {
		        out.writeObject(variable);
		        return bos.toByteArray();
		} 
	}

	@Override
	public String call() throws Exception 
	{
		return UUID.nameUUIDFromBytes(bytes()).toString();
	}
}
