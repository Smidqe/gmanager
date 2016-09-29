package rewrite.gui.listeners;

import java.util.Observable;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class scrollListener extends Observable
{
	private DoubleProperty value;
	private ChangeListener<Number> listener;
	
	public scrollListener(DoubleProperty scrollPosition) 
	{
		this.value = scrollPosition;
		
		listener = new ChangeListener<Number>()
		{

			@Override
			public synchronized void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				update();
			}
		};
		
		this.value.addListener(listener);
	}
	
	public synchronized void update()
	{
		System.out.println("Value changed");
		
		notify();
		notifyObservers();
	}
}
