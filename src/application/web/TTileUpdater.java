package application.web;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import application.images.manager.manager;
import javafx.application.Platform;

public class TTileUpdater implements Observer
{
	public void observe(Observable o)
	{
		o.addObserver(this);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		manager __manager = manager.instance();
		
		if (((TParser) o).parsed() == null)
			return;
		
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				try {
					__manager.append(__manager.create(((TParser) o).parsed()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				__manager.refreshTiles();
			}
			
		});
	}

}
