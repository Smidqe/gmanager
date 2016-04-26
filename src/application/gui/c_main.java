package application.gui;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.json.simple.parser.ParseException;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;

import application.images.*;
import application.parsers.parsers;

public class c_main implements Initializable{

	@FXML private AnchorPane lr_images, lr_search, lr_filters, lr_account, lr_settings;
	@FXML private TilePane tp_images, tp_search;
	
	private images __images;
	
	private void focus(ActionEvent __event)
	{
		/*
		 Depending on the source, different layers will appear.
		 
		 TODO:
		 	- Get the source ID's!
		 */
	}
	
	@FXML
	private void exit(ActionEvent e)
	{
		Platform.exit();
	}
	
	public void populate()
	{
		ObservableList<Node> __children = tp_images.getChildren();
		
		int index = 0;
		ImageView tile = null;
		for (Node child : __children)
		{
			if (index > __images.getImages().size())
				return;
			
			tile = (ImageView) child;
			tile.setImage(__images.getImages().get(index));
			
			index++;
		}
	}
	
	public ImageView child(int index)
	{
		if (!(index >= 0 && index <= tp_images.getChildren().size()))
			return null;
		
		ObservableList<Node> __children = tp_images.getChildren();
		
		int current = 0;
		for (Node child : __children)
		{
			if (index == current)
				return (ImageView) child;
			
			current++;
		}
		
		return null;
	}
	
	public Node child(Point2D p)
	{
		ObservableList<Node> __children = tp_images.getChildren();

		for (Node iv : __children)
		{
			if (iv.getBoundsInParent().contains(p))
				return iv;
		}
		
		return null;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		//lr_settings.setVisible(true);
		//lr_settings.toFront();
	
	
		URL site = null;
		try {
			site = new URL("https://derpibooru.org/images.json");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			Map<String, Object> map = parsers.parseJSON(site.openStream());
			
			System.out.println("Size: " + map.size());
			for (String key : map.keySet())
			{				
				if (map.get(key) instanceof ArrayList)
				{
					ArrayList<Object> values = (ArrayList<Object>) map.get(key);
					
					for (Object __object : values)
					{
						if (__object instanceof HashMap)
						{
							HashMap<String, Object> mmap = (HashMap<String, Object>) __object;
							
							
							for (Object __obj : mmap.keySet())
							{
								if (mmap.get(__obj) instanceof ArrayList)
									System.out.println("ArrayList");
								
								if (mmap.get(__obj) instanceof HashMap)
									System.out.println("HashMap"); 
								
								System.out.println("KEY[SUB]: " + __obj);
								System.out.println("VALUE[SUB]: " + mmap.get(__obj));
							
								
							}
						}
						
						//parsers.parse(((Map<String, Object>)__object)., false);
					}
				}
			}
			
			
		} catch (IOException | ParseException e) {
			System.out.println("Error occurred");
		}
		
		
	}

}
