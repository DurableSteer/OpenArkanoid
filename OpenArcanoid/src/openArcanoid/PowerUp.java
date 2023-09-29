package openArcanoid;

import javafx.scene.paint.Color;

public class PowerUp extends Sprite {
		private final PowerUpType powerUp;
		public PowerUp(double xPos,double yPos,double xSize,double ySize,PowerUpType powerUp) {
			super(xPos,yPos,xSize,ySize);
			this.powerUp = powerUp;
			
			switch(powerUp) {
			case TRIPLE:{
				color = Color.web("#57b7d8");
				break;
			}
			case SIZEUP:{
				color = Color.web("#050799");
				break;
			}
			case LAZERS:{
				color = Color.web("#be3410");
				break;
			}
			case SIZEDOWN:{
				color = Color.web("#727877");
				break;
			}
			case SPEEDDWN:{
				color = Color.web("#ef765e");
				break;
			}
			case STICKY:{
				color = Color.web("#93c420");
				break;
			}
			case PLAYER:{
				color = Color.web("#20c472");
				break;
			}
			case BREAK:{
				color = Color.web("#e883a0");
				break;
			}
			}
		}

		public PowerUpType getPowerUpType() {
			return powerUp;
		}
}
