package application.gui;

import java.net.*;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;


public class c_pagechange implements Initializable{

	@FXML private Button btn_imgur, btn_derpibooru;
	
	@FXML
	private void change(ActionEvent event)
	{
		event.getSource().toString().equals("");
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}

}
