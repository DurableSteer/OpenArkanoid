package openArcanoid;

import java.util.ArrayList;

import javafx.scene.paint.Color;

public class Pad extends Sprite {
	private ArrayList<Ball> balls;

	public Pad() {
		super();
		balls = new ArrayList<>();
	}
	public Pad(double xPosition, double yPosition, double xSize, double ySize) {
		super(xPosition, yPosition, xSize, ySize);
		balls = new ArrayList<Ball>();
		this.color = Color.web("757574");
	}

	public void addBall(Ball ball) {
		ball.setPosition(getPosition().getX()+getWidth()/2 - ball.getWidth()/2,  getPosition().getY() - ball.getHeight()-1);
		balls.add(ball);
	}
	@Override
	public void setPosition(Vector2D vector) {
		super.pos = vector;
		for(Ball b : balls)
			b.setPosition(vector.getX()+getWidth()/2,  vector.getY()-b.getHeight()-1);
	}
	@Override
	public void setPosition(double x, double y) {
		super.setPosition(x, y);
		for(Ball b : balls)
			b.setPosition(x+getWidth()/2 - b.getWidth()/2,  y - b.getHeight()-1);
	}
	public boolean hasBall() {
		return !balls.isEmpty();
	}
	public Ball getOneBall() {
		Ball tmp = balls.get(0);
		balls.remove(0);
		return tmp;
	}
	public Ball getBall() {
		return balls.get(0);
	}
}
