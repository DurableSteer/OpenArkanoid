package openArcanoid;

import javafx.scene.paint.Color;

public class PowerUp extends Sprite {
		private final PowerUpType powerUp;
		private String text;
		private Color textColor;
		public PowerUp(double xPos,double yPos,double xSize,double ySize,PowerUpType powerUp) {

			super(xPos,yPos,xSize,ySize);
			this.powerUp = powerUp;
			textColor = Color.GOLD;
			text = "";
			switch(powerUp) {
			case TRIPLE:{
				color = Color.web("#57b7d8");
				text = "D";
				break;
			}
			case SIZEUP:{
				color = Color.web("#050799");
				text = "E";
				break;
			}
			case LAZERS:{
				color = Color.web("#be3410");
				text = "L";
				break;
			}
			case SIZEDOWN:{
				color = Color.web("#727877");
				textColor = Color.CYAN;
				text = "P";
				break;
			}
			case SPEEDDWN:{
				color = Color.web("#ef765e");
				text = "P";
				break;
			}
			case STICKY:{
				color = Color.web("#93c420");
				text = "C";
				break;
			}
			case PLAYER:{
				color = Color.web("#20c472");
				text = "X";
				break;
			}
			case BREAK:{
				color = Color.web("#e883a0");
				text = "B";
				break;
			}
			}
		}

		public PowerUpType getPowerUpType() {
			return powerUp;
		}
		public Color getTextColor() {
			return textColor;
		}
		public String getText() {
			return text;
		}
}
