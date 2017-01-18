package application.types;

import javafx.scene.image.Image;

public class TCacheJob 
{
	public enum Method {LOAD, SAVE, STOP};
	private String ID, type;
	private Image img;
	private Method method;
	
	public TCacheJob() {
	}
	
	public TCacheJob(String ID, Image img, String type) 
	{
		this.ID = ID;
		this.img = img;
		this.type = type;
	}

	public String getID() {
		return ID;
	}

	public Image getImage() {
		return img;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public void setImage(Image img) {
		this.img = img;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
	
	public void setType(String __type)
	{
		this.type = __type;
	}
	
	public String getType()
	{
		return this.type;
	}
}
