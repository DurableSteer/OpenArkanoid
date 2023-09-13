package openArcanoid;

import javafx.scene.paint.Color;

public class Ball extends Sprite {
	private int damage;
	private double speedMult;
	private Vector2D direction;

	public Ball() {
		super(0,0,10,10);
		damage = 1;
		speedMult = 1;
		direction = new Vector2D(0.7071,-0.7071);
		color = Color.web("fc9a35");
	}

	public Ball(Vector2D position, double xSize, double ySize, int damage) {
		super(position.getX(), position.getY(), xSize, ySize);
		this.damage = damage;
		speedMult = 1;
		direction = new Vector2D(0.7071,-0.7071);
		color = Color.web("fc9a35");
	}
	public Ball(double xPos,double yPos, double xSize, double ySize, int damage) {
		super(xPos, yPos, xSize, ySize);
		this.damage = damage;
		speedMult = 1;
		direction = new Vector2D(0.7071,-0.7071);
		color = Color.web("fc9a35");
	}

	public Ball(double xPosition, double yPosition, double xSize, double ySize, int damage, double speedMultiplicator,
			Vector2D direction) {
		super(xPosition, yPosition, xSize, ySize);
		this.damage = damage;
		speedMult = speedMultiplicator;
		this.direction = direction;
		color = Color.web("fc9a35");
	}

	public Ball(Ball b) {
		// Creates a copy of a Ball as a new instance
		super(b);
		damage = b.getDamage();
		speedMult = b.getSpeedMult();
		direction = new Vector2D(b.getDirection());
		color = Color.web("fc9a35");
	}

	public void reflect(Side side) {
		direction.mirror(side);
	}
	
	public int getDamage() {
		return damage;
	}

	public double getSpeedMult() {
		return speedMult;
	}
	public void setSpeedMult(double newSpeedMult) {
		speedMult = newSpeedMult;
	}

	public Vector2D getDirection() {
		return direction;
	}
	public void setDirection(double x, double y) {
		direction.setX(x);
		direction.setY(y);
	}
	public void setDirection(Vector2D newDirection) {
		direction = newDirection;
	}
}
