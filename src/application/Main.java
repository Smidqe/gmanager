package application;
	
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {
	@Override
	public void start(Stage stage) {
		try {
	        Parent root = FXMLLoader.load(getClass().getResource("gui/gui_new.fxml"));
	        
	        Scene scene = new Scene(root);
	        scene.getStylesheets().add("application/data/resources/gui.css");

	        stage.initStyle(StageStyle.TRANSPARENT);
	        stage.setScene(scene);
	        
	        stage.setOnCloseRequest(e -> Platform.exit());
	        stage.show();
	        
	        
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
