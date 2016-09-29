package rewrite.types.interfaces;

public class IRunningState 
{
	static enum State {IDLE, RUNNING, ERROR};
	
	final static State state = State.IDLE;
	
}
