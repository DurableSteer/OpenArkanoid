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
	
	private double getDirectionDifference(Side side) {
		//returns the percentage difference between the current direction and the edge along a given side
		if(side == Side.LEFT || side == Side.RIGHT)
			return 1-Math.abs(direction.getY());
		if(side == Side.TOP || side == Side.BOTTOM)
			return 1-Math.abs(direction.getX());
		return 1;
	}

	public void reflectBorder(Side side) {
		//Changes the direction of the ball upon hitting the Border at side 
		if(getDirectionDifference(side) < 0.1) {//set shallow reflections to a minimum angle
			double MINREFLECTIONANGLE = Math.PI/5;
			if(side == Side.LEFT) {
				if(direction.getY() < 0) {
					setDirection(0, -1);
					direction.rotate(MINREFLECTIONANGLE);
				}
				else {
					setDirection(0,1);
					direction.rotate(-MINREFLECTIONANGLE);
				}
			}
			else if(side == Side.RIGHT) {
				if(direction.getY() < 0) {
					setDirection(0, -1);
					direction.rotate(-MINREFLECTIONANGLE);
				}
				else {
					setDirection(0,1);
					direction.rotate(MINREFLECTIONANGLE);
				}
			}
			else if(side == Side.TOP) {
				if(direction.getX() < 0) {
					setDirection(-1, 0);
					direction.rotate(-MINREFLECTIONANGLE);
				}
				else {
					setDirection(1,0);
					direction.rotate(MINREFLECTIONANGLE);
				}
			}
			else { //side is bottom
				if(direction.getX() < 0) {
					setDirection(-1, 0);
					direction.rotate(-MINREFLECTIONANGLE);
				}
				else {
					setDirection(1,0);
					direction.rotate(MINREFLECTIONANGLE);
				}
			}
		}
		else
			direction.mirror(side);
	}
	
	public void reflectSprite(Side side) {
		//reflect the Ball off the Side of the hit rectangular Sprite
		if(side == Side.LEFT)
			reflectBorder(Side.RIGHT);
		else if(side == Side.RIGHT)
			reflectBorder(Side.RIGHT);
		else if(side == Side.TOP)
			reflectBorder(Side.BOTTOM);
		else
			reflectBorder(Side.TOP);
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
