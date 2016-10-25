package application.gui;


/*
	TODO:
		- Figure out why GUI goes white when there a lot of tiles
		- Overall clean this controller (not a priority)
		- 

		R2 TODO:
			- Simplify (if possible)
 */

import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Screen;
import javafx.stage.Window;

import application.types.custom.TGallery;

public class controller_main implements Initializable
{ 
	//change name once first version is ready (put into a enum?)
	public static final int __INDEX_LAYER_IMAGES = 0;
	public static final int __INDEX_LAYER_FILTERS = 1;
	public static final int __INDEX_LAYER_SEARCH = 2;
	public static final int __INDEX_LAYER_ACCOUNT = 3;
	public static final int __INDEX_LAYER_SETTINGS = 4;
	public static final int __INDEX_LAYER_PAGESWITCH = 5; 
	
	@FXML private AnchorPane lr_images, lr_search, lr_filters, lr_account, lr_settings, lr_pageswitch, lr_marks, ap_title, ap_main;
	@FXML private ScrollPane sp_images;
	@FXML private TilePane tp_images, tp_search;
	@FXML private MenuItem mi_account, mi_settings, mi_switch, mi_exit;
	@FXML private Button btn_mark, btn_download, btn_exit, btn_fullscreen;
	
	private final int __BORDER_WIDTH = 10;
	
	private List<AnchorPane> __layers;
	private Point2D point;
	private Rectangle2D window;
	private boolean fullscreen = false; 
	
	private Node current_pane;
	private TGallery __gallery;
	
	private Cursor getCursor(MouseEvent event, Scene scene)
	{
		Point2D p = new Point2D(event.getX(), event.getY());

		if (p.getX() < __BORDER_WIDTH && p.getY() < __BORDER_WIDTH)
			return Cursor.NW_RESIZE;
		else if (p.getX() < __BORDER_WIDTH && p.getY() > scene.getHeight() - __BORDER_WIDTH)
			return Cursor.SW_RESIZE;
		else if (p.getX() > scene.getWidth() - __BORDER_WIDTH && p.getY() < __BORDER_WIDTH)
			return Cursor.NE_RESIZE;
		else if (p.getX() > scene.getWidth() - __BORDER_WIDTH && p.getY() > scene.getHeight() - __BORDER_WIDTH)
			return Cursor.SE_RESIZE;
		else if (p.getY() < __BORDER_WIDTH)
			return Cursor.N_RESIZE;
		else if (p.getX() < __BORDER_WIDTH)
			return Cursor.W_RESIZE;
		else if (p.getY() > scene.getHeight() - __BORDER_WIDTH)
			return Cursor.S_RESIZE;
		else if (p.getX() > scene.getWidth() - __BORDER_WIDTH)
			return Cursor.E_RESIZE;
		
		return Cursor.DEFAULT;
	}

	@FXML
	private void showFull(MouseEvent e)
	{
//		if (e.getEventType() != MouseEvent.MOUSE_CLICKED)
//			return;
//		
//		int index = getSubTile(e, tp_images);
//		
//		//don't load a image that is null or has failed to load for some odd reason
//		if (index == -1)
//			return;
//		
//		//__images.setLast(__images.getImages().get(index).getImage());
//	
//		Parent root;
//        try {
//			Stage stage = new Stage();
//	        root = FXMLLoader.load(getClass().getResource("/application/gui/fullwindow/fullwindow_new.fxml"));
//	        
//	        Scene scene = new Scene(root);
//	        
//	        stage.initStyle(StageStyle.UNIFIED);
//	        stage.setResizable(true);
//	        stage.setScene(scene);   
//	        stage.show();
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
	}
	
	private String source(String regex, String text, int group)
	{
		Pattern __pattern = Pattern.compile(regex);
		Matcher __matcher = __pattern.matcher(text);
		
		if (__matcher.find())
			return __matcher.group(group);
		else
			return null;
	}
	
	private void mode(Node pane, boolean enable)
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

		if (__event.getSource() instanceof MenuItem)
			__source = source("\\s*([(id=)]*)=(.*),", __event.getSource().toString(), 2); //menuitems only have ID avaivable from ActionEvent unless specifically casted into a MenuItem! 
		else
			__source = source("\\s*([^=]*)'(.*)'", __event.getSource().toString(), 2);

		if (!__source.isEmpty())
			mode(current_pane, false);
		
		switch (__source)
		{
			case "Images": case "Watched": mode(__layers.get(__INDEX_LAYER_IMAGES), true); break;
			case "Search": mode(__layers.get(__INDEX_LAYER_SEARCH), true); break;
			case "mi_account" : mode(__layers.get(__INDEX_LAYER_ACCOUNT), true); break;
			case "mi_settings" : mode(__layers.get(__INDEX_LAYER_SETTINGS), true); break;
			case "mi_switch" : mode(__layers.get(__INDEX_LAYER_PAGESWITCH), true); break;
			default: return;
		}
	}
	
	@FXML
	private void exit(ActionEvent e)
	{
		try {
			__gallery.stop();
		} catch (InterruptedException | ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Platform.exit();
		System.exit(0);
	}

	private void setWindow(Window window, Rectangle2D box)
	{
		if (window == null)
			return;
		
		window.setX(box.getMinX());
		window.setY(box.getMinY());
		
		window.setWidth(box.getWidth());
		window.setHeight(box.getHeight());
	}
	
	private boolean isWindowFull(Window window)
	{

		return getClosestScreen(window).getBounds().getWidth() == window.getWidth() && 
			   getClosestScreen(window).getBounds().getHeight() == window.getHeight();
	}
	
	private ArrayList<Screen> intersectingScreens(Window window)
	{
		ObservableList<Screen> screens = Screen.getScreens();
		
		ArrayList<Screen> result = new ArrayList<Screen>();
		for (Screen screen : screens)
			if (screen.getBounds().intersects(window.getX(), window.getY(), window.getWidth(), window.getHeight()))
				result.add(screen);
		
		return result;
	}
	
	private Screen getClosestScreen(Window window)
	{
		ArrayList<Screen> screens = intersectingScreens(window);

		System.out.println("windows: " + screens);
		
		if (screens.size() == 0)
			return null;

		double min = Math.sqrt(Math.pow(window.getX() - screens.get(0).getBounds().getMinX(), 2) + Math.pow(window.getY() - screens.get(0).getBounds().getMinY(), 2));
		double distance = 0;
		int index = 0;
		for (int i = 0; i < screens.size(); i++)
		{
			distance = Math.sqrt(Math.pow(window.getX() - screens.get(i).getBounds().getMinX(), 2) + Math.pow(window.getY() - screens.get(i).getBounds().getMinY(), 2));
			
			if (distance < min)
			{
				min = distance;
				index = i;
			}
		}
		
		return screens.get(index);
	}

	private void setFullscreen(boolean full)
	{
		Window window = ap_main.getScene().getWindow();
		Rectangle2D closest = getClosestScreen(window).getBounds();
		
		if (closest == null)
			return;
		
		if (!fullscreen) //to remember the last window position.
			this.window = new Rectangle2D(window.getX(), window.getY(), window.getWidth(), window.getHeight()); 

		if (isWindowFull(window) && window.getX() != closest.getMinX())
		{
			System.out.println("Screen already fullscreen");
			
			setWindow(window, closest);
			return;
		}
		
		System.out.println("Setting the window");

		setWindow(window, !full ? this.window : closest);
		
		this.fullscreen = isWindowFull(window);
	}
	
	@FXML
	private void setFullscreen(ActionEvent event)
	{
		setFullscreen(!this.fullscreen);
	}

	private void relocate(MouseEvent event, Window window, Point2D point)
	{
        window.setX(event.getScreenX() + point.getX());
        window.setY(event.getScreenY() + point.getY());
	}
	
	private void resize(MouseEvent event, Scene scene)
	{
		if (scene.getCursor().equals(Cursor.DEFAULT))
			return;
		
		Cursor cursor = scene.getCursor();
		double modifier = 0;

		if (cursor.equals(Cursor.N_RESIZE) || cursor.equals(Cursor.NW_RESIZE) || cursor.equals(Cursor.NE_RESIZE))
		{
			if (event.getSceneY() < 0)
				modifier = scene.getWindow().getY() - event.getScreenY() + scene.getWindow().getHeight();
			else
				modifier = scene.getWindow().getHeight() - (event.getScreenY() - scene.getWindow().getY());
			
			scene.getWindow().setHeight(modifier);
			scene.getWindow().setY(event.getScreenY());
		}
		
		if (cursor.equals(Cursor.S_RESIZE) || cursor.equals(Cursor.SW_RESIZE) || cursor.equals(Cursor.SE_RESIZE))
			scene.getWindow().setHeight(event.getSceneY());
		
		
		if (cursor.equals(Cursor.W_RESIZE) || cursor.equals(Cursor.NW_RESIZE) || cursor.equals(Cursor.SW_RESIZE))
		{
			if (event.getSceneX() < 0)
				modifier = scene.getWindow().getX() - event.getScreenX() + scene.getWindow().getWidth();
			else
				modifier = scene.getWindow().getWidth() - (event.getScreenX() - scene.getWindow().getX());
			
			scene.getWindow().setWidth(modifier);
			scene.getWindow().setX(event.getScreenX());
		}
		
		if (cursor.equals(Cursor.E_RESIZE) || cursor.equals(Cursor.NE_RESIZE) || cursor.equals(Cursor.SE_RESIZE))
			scene.getWindow().setWidth(event.getSceneX());
	}
	
	@SuppressWarnings("unused")
	private List<Integer> getMaxTilesInViewport(ScrollPane parent, TilePane tiles)
	{
		List<Integer> values = new ArrayList<Integer>(2);
		
		values.set(0, (int) (parent.getViewportBounds().getWidth() / tiles.getTileWidth()));
		values.set(1, (int) (parent.getViewportBounds().getWidth() / tiles.getTileWidth()));
		
		return values;
	}
	
	@Override
	public synchronized void initialize(URL arg0, ResourceBundle arg1) 
	{	

		try {
			__gallery = new TGallery(tp_images, sp_images);
		} catch (MalformedURLException | InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		
		btn_fullscreen.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) 
			{
				setFullscreen(!fullscreen);
			}
		});
		
		
		ap_main.setOnMouseMoved(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent arg0) 
			{
				ap_main.getScene().setCursor(getCursor(arg0, ap_main.getScene()));
			}
			
		});
		ap_main.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) 
			{ 
				point = new Point2D(ap_title.getScene().getWindow().getX() - event.getScreenX(), ap_title.getScene().getWindow().getY() - event.getScreenY());
			}
			
		});
		
		ap_main.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) 
			{ 		
				__gallery.allowRefreshing(false);
				
				if (ap_main.getScene().getCursor().equals(Cursor.DEFAULT))
					relocate(event, ap_main.getScene().getWindow(), point); 
				else
					resize(event, ap_main.getScene());
				
				__gallery.allowRefreshing(true);
			}
			
		});
		
		ap_title.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) 
			{
				
				if (event.getClickCount() == 2)
					setFullscreen(!fullscreen);
			}
			
		});


		__layers = new ArrayList<AnchorPane>();
		__layers.addAll(Arrays.asList(lr_images, lr_search, lr_filters, lr_account, lr_settings));
		
		for (AnchorPane pane : __layers)
			mode(pane, false);
		
		mode(__layers.get(0), true);
	}
}
