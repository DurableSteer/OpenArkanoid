package openArcanoid;

import javafx.scene.paint.Color;

public class Block extends Sprite {
	private int hp;
	private int points;

	private final int BASEHP = 1;


	public Block(double xPosition, double yPosition, double xSize, double ySize, Color color,int roundNr) {
		super(xPosition, yPosition, xSize, ySize);
		this.color = color;
		hp = parseHp(color);
		points = parsePoints(color, roundNr);
	}
	private int parseHp(Color color) {
		if(color.equals(color.web("bdbdbd")))//silver
			return 2;
		if(color.equals(Color.web("f1bd3a")))//gold
			return 999;
		return 1;
	}
	private int parsePoints(Color color, int roundNr) {
		if(color.equals(Color.web("fcfcfd")))//white
			return 50;
		if(color.equals(Color.web("fd7560")))//orange
			return 60;
		if(color.equals(Color.web("3bbdfc")))//lightblue
			return 70;
		if(color.equals(Color.web("82d118")))//green
			return 80;
		if(color.equals(Color.web("d92400")))//red
			return 90;
		if(color.equals(Color.web("006ce6")))//blue
			return 100;
		if(color.equals(Color.web("fc75b5")))//pink
			return 110;
		if(color.equals(Color.web("fc9a35")))//yellow
			return 120;
		if(color.equals(Color.web("bdbdbd")))//silver
			return 50*roundNr;
		return 0;//gold or error
	}
	
	public int getPoints() {
		return points;
	}

	public int getHitpoints() {
		return hp;
	}

	public void getHitBy(Ball b) {
		if(color.equals(Color.web("f1bd3a")))
			return;
		hp -= b.getDamage();
	}
}
