package rewrite.types.listeners;

import java.util.Observable;
import java.util.Observer;


public class LURLListener implements Observer
{
	public void observe(Observable o) {
	    o.addObserver(this);
	  }
	
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
	
}
