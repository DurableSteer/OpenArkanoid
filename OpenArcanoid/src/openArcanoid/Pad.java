package openArcanoid;

import java.util.ArrayList;

import javafx.scene.paint.Color;

public class Pad extends Sprite {
	private ArrayList<Ball> balls;	//todo: turn into queue
	private final double normalSize;
	private boolean isLaser;
	private boolean isSticky;

	public Pad(double xPosition, double yPosition, double xSize, double ySize) {
		super(xPosition, yPosition, xSize, ySize);
		balls = new ArrayList<>();
		normalSize = xSize;
		isLaser = false;
		isSticky = false;
		this.color = Color.web("757574");
	}

	public void addBall(Ball ball) {

		ball.setPosition(getPosition().getX()+getWidth()/2 - ball.getWidth()/2,  getPosition().getY() - ball.getHeight()-1);
		balls.add(ball);
	}
	public void stick(Ball ball) {
		ball.setPosition(ball.getPosition().getX(),  getPosition().getY() - ball.getHeight()-1);
		balls.add(ball);
	}
	public boolean isSticky() {
		return isSticky;
	}
	public boolean isLaser() {
		return isLaser;
	}
	
	@Override
	public void setPosition(double x, double y) {
		for(Ball b : balls) {
			if(isSticky) {
				double newXDiff = Math.abs(pos.getX()-x);
				if(pos.getX() > x){//movement to the left
					b.setPosition(b.getPosition().getX()-newXDiff,  b.getPosition().getY());
				}
				else
					b.setPosition(b.getPosition().getX()+newXDiff,  b.getPosition().getY());
			}
			else
				b.setPosition(x+getWidth()/2-b.getWidth()/2,  y-b.getHeight()-1);
		}
		
		pos.setX(x);
		pos.setY(y);
	}
	public void setWidth(double newWidth) {
		if(isLaser)
			isLaser = false;
		size.setX(newWidth);
	}
	public boolean hasBall() {
		return !balls.isEmpty();
	}
	public Ball getOneBall() {
		Ball tmp = balls.get(0);
		balls.remove(0);
		return tmp;
	}
	public ArrayList<Ball> getBalls(){
		return balls;
	}
	public void setSticky() {
		if(isLaser)
			isLaser = false;
		isSticky = true;
	}
	public void setLaser() {
		if(isSticky)
			isSticky = false;
		size.setX(normalSize);
		isLaser = true;
	}
	public void reset() {
		size.setX(normalSize);
		isSticky = false;
		isLaser = false;
		balls = new ArrayList<Ball>();
	}
}
