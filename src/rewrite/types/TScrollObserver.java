package rewrite.types;

import java.util.Observable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ScrollPane;

public class TScrollObserver extends Observable implements Runnable
{
	private ScrollPane pane;
	private boolean stop = false;
	
	public TScrollObserver() 
	{
		// TODO Auto-generated constructor stub
	}

	public void bind(ScrollPane pane)
	{
		this.pane = pane;
	}
	
	public void stop()
	{
		this.stop = true;
	}
	
	@Override
	public synchronized void run() {
		ChangeListener<Number> listener = new ChangeListener<Number>(){

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number valueNew) 
			{
				System.out.println("Changed");
				notify();
			}		
		};
		pane.vvalueProperty().addListener(listener);
		
		while (!stop)
		{
			try {
				listener.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("Updated");
			
			setChanged();
			notifyObservers();
		}
	}
}
