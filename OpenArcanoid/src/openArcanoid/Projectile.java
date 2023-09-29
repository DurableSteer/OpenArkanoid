package openArcanoid;

import javafx.scene.paint.Color;

public class Projectile extends Ball {

	public Projectile(double xPos, double yPos, double width, double height) {
		super(xPos,yPos,width,height,1);
		setDirection(0,1);
		color = Color.WHITE;
	}

}
