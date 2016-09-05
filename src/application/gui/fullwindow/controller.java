package application.gui.fullwindow;

import java.net.URL;
import java.util.ResourceBundle;

import application.images.manager.manager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class controller implements Initializable{

	@FXML
	private ImageView iv_view;
	private manager __images;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		__images = manager.instance();
		
		System.out.println(__images.getLast().getHeight());
		System.out.println(__images.getLast().getWidth());
		System.out.println(__images.getLast().getImage());
		
		iv_view.setFitHeight(__images.getLast().getHeight());
		iv_view.setFitWidth(__images.getLast().getWidth());
		
		iv_view.setImage(new Image(__images.getLast().getImage()));
	}
}
