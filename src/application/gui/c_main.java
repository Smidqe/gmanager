package application.gui;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import application.images.manager.manager;
import application.types.TThumbnailUpdater;
import application.web.TImageDownload;
import application.web.TParser;
import application.web.TTileUpdater;
import application.Main;
import application.data.resources.globals.globals;
import application.data.settings.settings;

@SuppressWarnings("unused")
public class c_main implements Initializable{ //change name once first version is ready.
	public static final int __INDEX_LAYER_IMAGES = 0;
	public static final int __INDEX_LAYER_FILTERS = 1;
	public static final int __INDEX_LAYER_SEARCH = 2;
	public static final int __INDEX_LAYER_ACCOUNT = 3;
	public static final int __INDEX_LAYER_SETTINGS = 4;
	public static final int __INDEX_LAYER_PAGESWITCH = 5; 
	
	@FXML private AnchorPane lr_images, lr_search, lr_filters, lr_account, lr_settings, lr_pageswitch, lr_marks, current_pane, ap_title, ap_main;
	@FXML private ScrollPane sp_images;
	@FXML private TilePane tp_images, tp_search;
	@FXML private MenuItem mi_account, mi_settings, mi_switch, mi_exit;
	@FXML private Button btn_mark, btn_download, btn_exit, btn_fullscreen;
	
	private final int __BORDER_WIDTH = 10;
	
	private manager __images;
	private ArrayList<AnchorPane> __layers;
	private Point2D point;
	private Rectangle2D window;
	private boolean fullscreen; 
	private int page = 1;
	private settings __settings;
	private TParser parser;
	private TTileUpdater __parser_observer;
	//private TImageDownloadObserver __download_observer;
	
	private Thread __thread_parser, __thread_tile_updater;
	
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

	private int getSubTile(MouseEvent e, TilePane pane)
	{
		for (int index = 0; index < pane.getChildren().size(); index++)
		{
			Node tile = pane.getChildren().get(index);

			if (tile.getBoundsInParent().contains(e.getX(), e.getY()))
				return index;
		}
		
		return -1;
	}
	
	private AnchorPane getActive()
	{
		for (AnchorPane pane : this.__layers)
			if (pane.isVisible())
				return pane;
		
		return null;
	}
	
	@FXML
	private void showFull(MouseEvent e)
	{
		if (e.getEventType() != MouseEvent.MOUSE_CLICKED)
			return;
		
		int index = getSubTile(e, tp_images);
		
		//don't load a image that is null or has failed to load for some odd reason
		if (index == -1)
			return;
		
		__images.setLast(__images.getImages().get(index).getImage());
	
		Parent root;
        try {
			Stage stage = new Stage();
	        root = FXMLLoader.load(getClass().getResource("/application/gui/fullwindow/fullwindow_new.fxml"));
	        
	        Scene scene = new Scene(root);
	        
	        stage.initStyle(StageStyle.UNIFIED);
	        stage.setResizable(true);
	        stage.setScene(scene);   
	        stage.show();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
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
	
	private ArrayList<ImageView> getHidden(TilePane pane)
	{
		ArrayList<ImageView> hidden = new ArrayList<ImageView>();
		
		for (Node node : pane.getChildren())
			if (!inViewport(sp_images, tp_images, node))
				hidden.add((ImageView) node);

		return hidden;
	}
	
	private int getHiddenCount(TilePane pane)
	{
		return getHidden(pane).size();
	}
	
	//move to a different thread. 
	private void refreshTiles() throws MalformedURLException
	{
		ObservableList<Node> list = tp_images.getChildren();
		
		int index = 0;
		for (Node node : list)
		{
			if (!inViewport(sp_images, tp_images, node))
				((ImageView) node).setImage(null);
			else
				__images.load(index);
			
			index++;
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {	
		/*
			TODO:
				- Rewrite this once we have finished the automation of paging and such.
		 */
		
		btn_fullscreen.setStyle("-fx-background-image: url(" + Main.class.getResource("data/resources/images/btn_fullscreen_pressed.png").toExternalForm() + ")");
		
		
		__thread_parser = new Thread();
		__parser_observer = new TTileUpdater();
		
		__thread_tile_updater = new Thread();
		
		__images = manager.instance();
		__images.setCurrentSite(manager.siteID.__DERPIBOORU);
		__images.setTilePane(tp_images);
		__settings = settings.instance();
		
		parser = new TParser();
		__parser_observer.observe(parser);
		
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
		
		sp_images.vvalueProperty().addListener(new ChangeListener<Number>(){

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number valueNew) 
			{
				try {
					refreshTiles();
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (valueNew.doubleValue() > 0.9 && __settings.bAutomaticPaging && !__thread_parser.isAlive())
				{

					try {
						parser.setURL(new URL(globals.__URL_SUBSITE_DERPIBOORU_IMAGES + "?page=" + String.valueOf(page++)));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}

					__thread_parser = new Thread(parser);
					__thread_parser.start();

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
			parser.setURL(new URL(globals.__URL_SUBSITE_DERPIBOORU_IMAGES + "?page=" + String.valueOf(page++)));
			Thread thread = new Thread(parser);
			
			thread.start();
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
