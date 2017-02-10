package application.types.custom.gallery.tiles;

import javafx.scene.Node;
import javafx.scene.image.Image;

public class TTileAction
{
	public enum Action {SHOW, HIDE, REMOVE}; //do we need the remove? E: YES, when we switch to a different site
	
	private Action action;
	private Image image;
	private Node node;
	
	public TTileAction(Action action, Image image, Node node) 
	{
		this.setAction(action);
		this.setImage(image);
		this.setNode(node);
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}
}
