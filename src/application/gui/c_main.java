package application.gui;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.parser.ParseException;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Screen;
import javafx.stage.Window;
import application.globals.constants;
import application.images.*;
import application.parsers.parsers;

public class c_main implements Initializable{

	@FXML private AnchorPane lr_images, lr_search, lr_filters, lr_account, lr_settings, lr_pageswitch, lr_marks, current_pane, ap_title, ap_main;
	@FXML private ScrollPane sp_images;
	@FXML private TilePane tp_images, tp_search;
	@FXML private MenuItem mi_account, mi_settings, mi_switch, mi_exit;
	@FXML private Button btn_mark, btn_download;
	
	
	@SuppressWarnings("unused")
	private images __images;
	private ArrayList<AnchorPane> __layers;
	
	private Point2D point;
	private Rectangle2D window;
	private boolean fullscreen; 
	private int page = 0;

	private String source(String regex, String text, int group)
	{
		Pattern __pattern = Pattern.compile(regex);
		Matcher __matcher = __pattern.matcher(text);
		
		if (__matcher.find())
			return __matcher.group(group);
		else
			return null;
	}
	
	private void mode(AnchorPane pane, boolean enable)
	{
		pane.setDisable(!enable);
		pane.setVisible(enable);
		
		if (enable)
		{
			pane.toFront();
			this.current_pane = pane;
		}
	}
	
	@FXML
	private void focus(ActionEvent __event)
	{
		String __source = "";
		
		System.out.println("__event: " + __event.getSource().toString());

		if (__event.getSource() instanceof MenuItem)
			__source = source("\\s*([(id=)]*)=(.*),", __event.getSource().toString(), 2); //menuitems only have ID avaivable from ActionEvent unless specifically casted into a MenuItem! 
		else
			__source = source("\\s*([^=]*)'(.*)'", __event.getSource().toString(), 2);
		
		System.out.println("__source: " + __source);

		if (!__source.isEmpty())
			mode(current_pane, false);
		
		switch (__source)
		{
			case "Images": case "Watched": mode(__layers.get(constants.__INDEX_LAYER_IMAGES), true); break;
			case "Search": mode(__layers.get(constants.__INDEX_LAYER_SEARCH), true); break;
			case "mi_account" : mode(__layers.get(constants.__INDEX_LAYER_ACCOUNT), true); break;
			case "mi_settings" : mode(__layers.get(constants.__INDEX_LAYER_SETTINGS), true); break;
			case "mi_switch" : mode(__layers.get(constants.__INDEX_LAYER_PAGESWITCH), true); break;
			default: return;
		}
	}
	
	@FXML
	private void exit(ActionEvent e)
	{
		Platform.exit();
	}

	private void resize(boolean full)
	{
		ObservableList<Screen> bounds = Screen.getScreens();
		Window window = ap_main.getScene().getWindow();
	
		if (!fullscreen)
			this.window = new Rectangle2D(window.getX(), window.getY(), window.getWidth(), window.getHeight()); 
		else
		{
			window.setX(this.window.getMinX());
			window.setY(this.window.getMinY());
			
			window.setWidth(this.window.getWidth());
			window.setHeight(this.window.getHeight());
			
			this.fullscreen = false;
			return;
		}	
		
		for (Screen screen : bounds)
			if (screen.getBounds().intersects(window.getX(), window.getY(), window.getWidth(), window.getHeight()))
			{
				window.setX(screen.getBounds().getMinX());
				window.setY(screen.getBounds().getMinY());
				
				window.setWidth(screen.getBounds().getWidth());
				window.setHeight(screen.getBounds().getHeight());
				
				this.fullscreen = true;
				return;
			}
	}
	
	private void relocate(MouseEvent event, Window window, Point2D point)
	{
        window.setX(event.getScreenX() + point.getX());
        window.setY(event.getScreenY() + point.getY());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	
		
		for (int i = 0; i < 15; i++)
			tp_images.getChildren().add(new ImageView("https://derpicdn.net/img/2016/5/2/1144648/thumb_small.jpeg"));
		
		ap_main.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) { point = new Point2D(ap_title.getScene().getWindow().getX() - event.getScreenX(), ap_title.getScene().getWindow().getY() - event.getScreenY());}
			
		});
		
		ap_main.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) { relocate(event, ap_main.getScene().getWindow(), point); }
			
		});
		
		ap_title.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				
				if (event.getClickCount() == 2)
					resize(!fullscreen);
			}
			
		});
		
		
		__layers = new ArrayList<AnchorPane>();
		__layers.addAll(Arrays.asList(lr_images, lr_search, lr_filters, lr_account, lr_settings, lr_pageswitch));
	
		
		for (AnchorPane pane : __layers)
			mode(pane, false);
		
		mode(__layers.get(0), true);
		
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
			
			//won't be this deep, since we already know what is what and how they work. we already have the keys
			for (String key : map.keySet())
			{				
				System.out.println(key);
				if (map.get(key) instanceof ArrayList)
				{
					ArrayList<Object> values = (ArrayList<Object>) map.get(key);
					
					for (Object __object : values)
					{
						
						if (__object instanceof HashMap)
						{
							
							
							HashMap<String, Object> mmap = (HashMap<String, Object>) __object;
							System.out.println(mmap);
							
							for (Object __obj : mmap.keySet())
							{
//								if (mmap.get(__obj) instanceof ArrayList)
//									System.out.println("ArrayList");
//								
//								if (mmap.get(__obj) instanceof HashMap)
//									System.out.println("HashMap"); 
								
								//System.out.println("KEY[SUB]: " + __obj);
								//System.out.println("VALUE[SUB]: " + mmap.get(__obj));
							
								
							}
						}
					}
				}
			}
			
			
		} catch (IOException | ParseException e) {
			System.out.println("Error occurred");
		}
		
		
	}
}
