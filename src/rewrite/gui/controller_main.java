package rewrite.gui;


/*
	TODO:
		- Figure out why GUI goes white when there a lot of tiles
		- Perhaps utilise javafx's own Service and Task classes
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.BoundingBox;
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

import rewrite.types.TGrabber;
import rewrite.types.TManager;
import rewrite.types.TThumbnailRefresher;
import rewrite.types.TTileManager;
import rewrite.types.queues.TRefresherQueue;

public class controller_main implements Initializable
{ //change name once first version is ready.
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
	private boolean fullscreen; 
	private int page = 1;
	private TManager __manager;
	private ExecutorService __executor;
	private TTileManager __tiles;
	private TGrabber __grabber;
	private TThumbnailRefresher __refresher;
	private TRefresherQueue __refresher_queue;
	private BlockingDeque<String> __grabber_deque;
	
	private Node current_pane;
	
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

//	private int getSubTile(MouseEvent e, TilePane pane)
//	{
//		for (int index = 0; index < pane.getChildren().size(); index++)
//		{
//			Node tile = pane.getChildren().get(index);
//
//			if (tile.getBoundsInParent().contains(e.getX(), e.getY()))
//				return index;
//		}
//		
//		return -1;
//	}
//
//	private AnchorPane getActive()
//	{
//		for (AnchorPane pane : this.__layers)
//			if (pane.isVisible())
//				return pane;
//		
//		return null;
//	}
	
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
		__executor.shutdown();
		
		Platform.exit();
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
		
		double min = 50000;
		double distance = 0;
		int index = -1;
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
	
		if (isWindowFull(window) && window.getX() != getClosestScreen(window).getBounds().getMinX())
		{
			setWindow(window, getClosestScreen(window).getBounds());
			return;
		}
		
		if (!fullscreen) //to remember the last window position.
			this.window = new Rectangle2D(window.getX(), window.getY(), window.getWidth(), window.getHeight()); 

		setWindow(window, !full ? this.window : getClosestScreen(window).getBounds());
		
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

	
	public boolean inViewport(ScrollPane viewport, TilePane pane, Node node)
	{
		double __value = pane.getHeight() * viewport.getVvalue() - viewport.getViewportBounds().getHeight() * viewport.getVvalue();

		return new BoundingBox(0, __value, viewport.getWidth(), __value + viewport.getHeight()).intersects(node.getBoundsInParent());
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
	@SuppressWarnings("unused")
	private void fillUntilLimit()
	{
		
	}
	
	@SuppressWarnings("unused")
	private void refresh()
	{
		/*
		 	TODO:
		 		- Create a new refreshing method that can be eventually moved to the TThumbnailRefresher class
		 		- Will be used in conjuction with the new resize listener.
		 */
	}
	
	@Override
	public synchronized void initialize(URL arg0, ResourceBundle arg1) 
	{	
		__grabber_deque = new LinkedBlockingDeque<String>();
		__executor = Executors.newCachedThreadPool(r -> {
	        Thread t = new Thread(r);
	        
	        t.setName("Thread: FX-Executor" + r.getClass().getClass().getTypeName());
	        t.setDaemon(true);
	        return t;
	    });
		
		
		__manager = TManager.getInstance();
		__tiles = __manager.getTileManager();
		__grabber = TGrabber.instance();
		__refresher = TThumbnailRefresher.instance();
		__refresher.bind(__tiles);
		__refresher_queue = TRefresherQueue.instance();
		__refresher.setDeque(TRefresherQueue.instance().getDeque());
		__tiles.bind(tp_images, sp_images, TRefresherQueue.instance().getDeque());
		__grabber.bind(__tiles, __grabber_deque);
		
		btn_fullscreen.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {setFullscreen(!fullscreen);}
		});
		
		
		ap_main.setOnMouseMoved(new EventHandler<MouseEvent>(){

			@Override
			public void handle(MouseEvent arg0) {
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
				if (ap_main.getScene().getCursor().equals(Cursor.DEFAULT))
					relocate(event, ap_main.getScene().getWindow(), point); 
				else
					resize(event, ap_main.getScene());
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
		
		final ChangeListener<Number> listener = new ChangeListener<Number>()
		{
			final Timer timer = new Timer();
			TimerTask task = null;
			final long delay = 200;
			
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				// TODO Auto-generated method stub
				
				if (task != null)
					task.cancel();
				
				task = new TimerTask()
				{
					@Override
					public void run() 
					{
						System.out.println("Hello.");
						
						try {
							__refresher_queue.put("");
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};
				
				timer.schedule(task, delay);
			}
		};
		
		ap_main.widthProperty().addListener(listener);
		ap_main.heightProperty().addListener(listener);
		
		sp_images.vvalueProperty().addListener(new ChangeListener<Number>(){

			@Override
			public synchronized void changed(ObservableValue<? extends Number> arg0, Number arg1, Number valueNew) 
			{
				try {
					__refresher_queue.put("");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (valueNew.doubleValue() == 1.0 /*&& __settings.getBoolean("bAutomaticPaging")*/)
					try {
						__grabber.setURL(__manager.getSite().getURL("images", "?page=", ++page), true);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}		
			
		});
		
		tp_images.prefHeightProperty().bind(sp_images.heightProperty());

		__layers = new ArrayList<AnchorPane>();
		__layers.addAll(Arrays.asList(lr_images, lr_search, lr_filters, lr_account, lr_settings, lr_pageswitch));
		
		for (AnchorPane pane : __layers)
			mode(pane, false);
		
		mode(__layers.get(0), true);
		try {
			__grabber.setURL(__manager.getSite().getURL("images", "?page=", page), false);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		__executor.submit(__refresher);
		__executor.submit(__grabber);
	}
}
