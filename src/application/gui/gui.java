//package application.gui;
//
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.*;
//import java.util.ResourceBundle;
//
//import javafx.event.EventHandler;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.fxml.Initializable;
//import javafx.geometry.Point2D;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.layout.TilePane;
//import javafx.stage.Stage;
//import javafx.stage.StageStyle;
//
//import org.json.simple.JSONObject;
//import org.json.simple.JSONArray;
//import org.json.simple.parser.ParseException;
//
//import application.images.images;
//import application.types.TImage;
//import org.json.simple.parser.JSONParser;
//
//public class gui implements Initializable{
//
//	private images __images;
//	private String image_full;
//
//	@FXML
//	private TilePane __tiles;
//	
//	@FXML
//	public void full(MouseEvent e)
//	{
//		/*
//			Open the image in it full size (Don't exceed monitor size)
//		 */
//		
//		if (e.getEventType() == MouseEvent.MOUSE_CLICKED)
//		{	
//			
//			ImageView __tile = (ImageView) __images.child(new Point2D(e.getX(), e.getY()));
//			
//			System.out.println("Point: X: " + e.getX() + ", Y: " + e.getY());
//			
//			if (__tile == null)
//				return;
//			
//			__images.setLast(new TImage(image_full));
//		
//			Parent root;
//	        try {
//				Stage stage = new Stage();
//		        root = FXMLLoader.load(getClass().getResource("/application/gui/fullwindow/fullwindow.fxml"));
//		        
//		        Scene scene = new Scene(root);
//		        
//		        stage.initStyle(StageStyle.UNIFIED);
//		        stage.setResizable(true);
//		        stage.setScene(scene);   
//		        stage.show();
//		        
//		        
//
//	        } catch (IOException e1) {
//	            e1.printStackTrace();
//	        }
//		}
//	}
//	
//	@FXML
//	public void enlarge(MouseEvent e)
//	{
//		if (e.getEventType() == MouseEvent.MOUSE_ENTERED)
//		{
//			ImageView __tile = (ImageView) __images.child(new Point2D(e.getSceneX(), e.getSceneY() - __images.getTiles().getLayoutY()));
//			
//			if (__tile == null)
//				return;
//			
//			//__tile.setScaleX(1.2);
//			//__tile.setScaleY(1.2);
//			//__tile.toFront();
//			
//			System.out.println("We entered: " + __tile.toString());
//		}
//			
//	}
//
//	
//	@Override
//	public void initialize(URL location, ResourceBundle resources) {
//		System.out.println(__tiles.getPrefColumns());
//		
//		__images = images.instance();
//		__images.setTiles(__tiles);
//		
//		__tiles.setHgap(5);
//		__tiles.setVgap(5);
//		
//
//		URL site = null;
//		try {
//			site = new URL("https://derpibooru.org/images.json");
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		JSONParser parser = new JSONParser();
//		JSONObject object = null;
//		try {
//			object = (JSONObject) parser.parse(new InputStreamReader(site.openStream()));
//		} catch (IOException | ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		JSONArray msg = (JSONArray) object.get("images");
//		JSONObject imageObj = (JSONObject) msg.get(0);
//		JSONObject sizes = (JSONObject) imageObj.get("representations");
//
//		URL imageSite = null;
//		URL fullImage = null;
//		try {
//			imageSite = new URL("https:" + (String) sizes.get("small"));
//			fullImage = new URL("https:" + (String) sizes.get("full"));
//		} catch (MalformedURLException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//		image_full = fullImage.toString();
//		
//		System.out.println(imageSite.toExternalForm());
//		for (int i = 0; i < __tiles.getPrefColumns() * __tiles.getPrefRows(); i++)
//		{	
//			__tiles.getChildren().add(new ImageView(new Image(imageSite.toString(), 100, 100, false, false)));
//			
//			__tiles.getChildren().get(i).addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
//
//				@Override
//				public void handle(MouseEvent arg0) {
//					enlarge(arg0);
//				}
//				
//			});
//			__tiles.getChildren().get(i).addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
//
//				@Override
//				public void handle(MouseEvent event) {
//					shrink(event);
//					
//				}
//
//				private void shrink(MouseEvent event) {
//					ImageView __tile = (ImageView) __images.child(new Point2D(event.getSceneX(), event.getSceneY() - __images.getTiles().getLayoutY()));
//					
//					if (__tile == null)
//						return;
//
//					__tile.setScaleX(1.0);
//					__tile.setScaleY(1.0);
//				}
//				
//			});
//		}
//	}
//
//}
