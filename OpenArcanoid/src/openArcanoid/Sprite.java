package openArcanoid;

import javafx.scene.paint.Color;

public abstract class Sprite {
	// Superclass to all sprites in the game, assumes a Square hitbox
	protected Vector2D pos;
	protected Vector2D size;
	protected Color color;

	public Sprite() {
		pos = new Vector2D();
		size = new Vector2D();
		color = Color.HOTPINK;
	}

	public Sprite(double xPosition, double yPosition, double xSize, double ySize) {
		this.pos = new Vector2D(xPosition,yPosition);
		this.size = new Vector2D(xSize,ySize);
		color = Color.HOTPINK;
	}

	public Sprite(Sprite s) {
		//makes this instance a clone of s
		this.pos = new Vector2D(s.getPosition());
		this.size = new Vector2D(s.getSize());
	}

	public Vector2D getPosition() {
		return pos;
	}
	public Color getColor() {
		return color;
	}

	public double getWidth() {
		return size.getX();
	}
	public double getHeight() {
		return size.getY();
	}
	public Vector2D getSize() {
		return size;
	}
	public void setSize(Vector2D newSize) {
		size = newSize;
	}
	public void setPosition(Vector2D newPos) {
		pos = newPos;
	}
	public void setPosition(double x, double y) {
		pos.setX(x);
		pos.setY(y);
	}
}
