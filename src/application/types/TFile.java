package application.types;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class TFile extends File{
	private boolean read, write, binary;
	private String name, path, type;
	private OutputStream __writer;
	private InputStream __reader;

	private static final long serialVersionUID = 1L;

	public TFile(String pathname)
	{
		this(pathname, true);
	}
	
	public TFile(String pathname, boolean read)
	{
		super(pathname);
		
		this.path = pathname;
		this.name = getName();
		this.type = getExtension();
		
		set_method(!read);
	}

	public void set_method(boolean write)
	{
		this.read = !write;
		this.write = write;
		
		System.out.println("Write: " + this.write + "Read: " + this.read);
		
		try {
			if (this.read)
				__reader = new FileInputStream(this.path);
			else
				__writer = new FileOutputStream(this.path);
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
			System.out.println("Path: " + path);
		}
	}

	public String getExtension()
	{
		try {
	        return this.type = this.name.substring(this.name.lastIndexOf(".") + 1);
	    } catch (Exception e) {
	        return "";
	    }
	}
	
	public long getSize()
	{
		return this.length();
	}
	
	public String getType()
	{
		return this.type;
	}
	
	public OutputStream output()
	{
		return (OutputStream) __writer;
	}
	
	public InputStream input()
	{
		return (InputStream) __reader;
	}
	
	public boolean write(String s, boolean overwrite)
	{
		if (!this.write || this.binary)
			return false;
		
		OutputStreamWriter w = new OutputStreamWriter(__writer);
		try {
			
			if (!overwrite)
				w.append(s);
			else
				w.write(s);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public ArrayList<String> read()
	{
		if (!this.read || this.binary)
			return null;
		
		ArrayList<String> lines = new ArrayList<String>();
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
			String __tmp;
			
			while ((__tmp = r.readLine()) != null)
				lines.add(__tmp);
			
			r.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return lines;
	}
	
	public ArrayList<String> find(String sub)
	{
		ArrayList<String> lines = read();
		ArrayList<String> result = new ArrayList<String>();
		
		for (String line : lines)
			if (line.contains(sub))
				result.add(line);
		
		return result;
	}
	
	private ArrayList<Integer> sections()
	{
		ArrayList<String> lines = read();
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		for (int index = 0; index < lines.size(); index++)
		{
			if (lines.get(index).startsWith("[") && lines.get(index).endsWith("]"))
				result.add(index);
		}
		
		return result;
	}
	
	public String iniRead(String section, String key)
	{
		ArrayList<String> lines = read();
		ArrayList<Integer> sections = sections();
			
		int index = -1;
		for (int i = 0; i < lines.size(); i++)
			if (lines.get(sections.get(i)).contains(section))
			{
				index = sections.get(i);
				break;
			}
		
		if (index == -1)
			return null;
		
		for (int i = index; i < sections.get(index + 1); i++)
			if (lines.get(i).contains(key))
				return lines.get(i).substring(lines.get(i).indexOf('=') + 1, lines.get(i).length() - 1);
		
		return null;
	}

	
	public String iniWrite(String value, String section, String key)
	{
		
		
		
		return null;
	}
	
	public TFile convert(File file)
	{
		return new TFile(file.getAbsolutePath());
	}
	
	public ArrayList<TFile> convert(File[] listFiles) 
	{
		ArrayList<TFile> converted = new ArrayList<TFile>();
		
		for (File f : listFiles)
			converted.add(this.convert(f));
		
		return converted;
	}
}
