package rewrite.types;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import libFileExtensions.files.TFile;

public class TCacheFile extends TFile{
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
		ObjectOutputStream stream = new ObjectOutputStream(this.openOutput(true));
		
		stream.writeObject(object);
		stream.close();
		
		this.closeOutput();
	}
	
	public Object get() throws ClassNotFoundException, IOException
	{
		ObjectInputStream stream = new ObjectInputStream(this.openInput());
		
		Object object = null;
		object = stream.readObject();
	
		stream.close();
		this.closeInput();
		
		return object;
	}
	
	public String getID()
	{
		return this.UUID;
	}
}
