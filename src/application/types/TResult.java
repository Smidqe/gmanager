package application.types;

public class TResult<T> {
	public enum Status {
		PAUSED,
		SUCCESS,
		ERROR,
		BUSY,
		CANCELLED
	}

	public enum Method {
		SAVE,
		LOAD
	}
	
	private String __ID;
	private Status __status;
	private Method __method;
	private T __obj; //make this into a generic one?
	
	public TResult() 
	{
	}
	
	public TResult(Status __status, Method __method, String __image, T obj)
	{
		this.__ID = __image;
		this.__method = __method;
		this.__status = __status;
		this.__obj = obj;
	}
	
	public T get()
	{
		return this.__obj;
	}
	
	public void set(T t)
	{
		this.__obj = t;
	}

	public Status get_status() {
		return __status;
	}

	public Method get_method() {
		return __method;
	}

	public String get_image() {
		return __ID;
	}

	public void set_status(Status __status) {
		this.__status = __status;
	}

	public void set_method(Method __method) {
		this.__method = __method;
	}

	public void set_image(String __image) {
		this.__ID = __image;
	}
	
	
}
