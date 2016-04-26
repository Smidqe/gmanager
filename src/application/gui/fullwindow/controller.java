package application.gui.fullwindow;

import java.net.URL;
import java.util.ResourceBundle;

import application.images.images;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class controller implements Initializable{
	private images __images;
	
	@FXML
	private ImageView image;
	@FXML
	private ScrollPane pn_scroll;
	@FXML
	private AnchorPane pn_anchor_main;
	@FXML
	private Stage __stage;
	
	public void center()
	{
		if (image.getImage().getWidth() > pn_anchor_main.getWidth())
			return;
		
		System.out.println("Width(window): " + pn_anchor_main.getWidth());
	}
	
	public boolean fullscreen()
	{
		return ((Stage) pn_anchor_main.getScene().getWindow()).isFullScreen();
	}
	
	public void listener_fullscreen()
	{
		((Stage) pn_anchor_main.getScene().getWindow()).maximizedProperty().addListener(new ChangeListener<Boolean>() {

		    @Override
		    public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
		        System.out.println("minimized:" + t1.booleanValue());
		    }
		});
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		__images = images.instance();
		
		pn_scroll.setPannable(true);
		
		image.setFitHeight(__images.getLast().getHeight());
		image.setFitWidth(__images.getLast().getWidth());
		
		image.setImage(__images.getLast());
		
		pn_anchor_main.widthProperty().addListener(new ChangeListener<Number>() {
			
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {	
				if (fullscreen())
				{
					image.setTranslateX((pn_scroll.getWidth() - image.getImage().getWidth()) / 2);
					return;
				}
				
				if (image.getImage().getWidth() < pn_anchor_main.getWidth())
					image.setTranslateX((pn_scroll.getWidth() - image.getImage().getWidth()) / 2);
				else
					image.setTranslateX(0);
			}
		});
		
		pn_anchor_main.heightProperty().addListener(new ChangeListener<Number>() {
			
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {		
				if (fullscreen())
				{
					image.setTranslateY((pn_scroll.getHeight() - image.getImage().getHeight()) / 2);
					return;
				}
				
				if (image.getImage().getHeight() < pn_anchor_main.getHeight())
					image.setTranslateY((pn_scroll.getHeight() - image.getImage().getHeight()) / 2);
				else
					image.setTranslateY(0);
			}
		});
	}

}
