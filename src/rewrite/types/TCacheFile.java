package rewrite.types;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import libFileExtensions.files.TBinaryFile;

public class TCacheFile extends TBinaryFile{
	private static final long serialVersionUID = 1255846491775200168L;
	private String UUID;

	public TCacheFile(String pathname, boolean create) throws IOException {
		super(pathname, create);
		// TODO Auto-generated constructor stub
	}

	public TCacheFile(String path) throws IOException
	{
		this(path, true);
	}
	
	public boolean exists()
	{
		return this.exists();
	}
	
	public void write(Object object) throws FileNotFoundException, IOException
	{
		if (object == null)
			return;
		
		ObjectOutputStream stream = new ObjectOutputStream(this.output(true));
		
		stream.writeObject(object);
		stream.close();
		
		this.close();
	}
	
	public Object get() throws ClassNotFoundException, IOException
	{
		ObjectInputStream stream = new ObjectInputStream(this.input());
		
		Object object = null;
		object = stream.readObject();
	
		stream.close();
		this.close();
		
		return object;
	}
	
	public String getID()
	{
		return this.UUID;
	}
}
