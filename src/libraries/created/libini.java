package libraries.created;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class libini {
	private File file;
	
	public ArrayList<String> read()
	{
		if (!file.exists())
			return null;
		
		ArrayList<String> lines = new ArrayList<String>();
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath())));
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
	
	public ArrayList<Integer> sections()
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
	
	public String read(String section, String key)
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

	
	public String write(String section, String key, String value)
	{
		
		
		
		return null;
	}
	

}
