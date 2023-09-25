package openArcanoid;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;


import javafx.scene.paint.Color;

public class Engine {
	private final int BASELIVES = 4;
	private final double BASESPEED = 0.75;
	private final double MAXSPEED = 1.75;
	private final double FIELDWIDTH = 600;
	private final double FIELDHEIGHT = 400;
	private boolean isPaused = false;
	private int lives = BASELIVES;
	private int points = 0;
	private BlockXTree blocks = new BlockXTree();
	private ArrayList<Ball> balls = new ArrayList<>();
	private Pad pad;
	private int currLevel = 0;

	public int getPoints() {
		return points;
	}

	public void clearPoints() {
		points = 0;
	}

	public ArrayList<Block> getAllBlocks(){
		return blocks.getAllBlocks();
	}

	public void addPoints(int points) {
		this.points += points;
	}

	public int getLives() {
		return lives;
	}

	public boolean isPaused() {
		return isPaused;
	}

	public void pause() { 
		isPaused = true;
	}
	public void unpause() {
		isPaused = false;
	}

	public boolean levelCleared() {
		if(blocks.isEmpty()) {
			currLevel = (currLevel+1)%36;
			balls = new ArrayList<Ball>();
			return true;
		}
		return false;
	}

	public boolean gameOver() {
		return lives == 0;
	}
	public void reset() {
		//initializes the base values for a new game
		lives = BASELIVES;
		points = 0;
		balls = new ArrayList<Ball>();
		currLevel = 0;
		blocks = new BlockXTree();
		points = 0;
		unpause();
	}
	public void next(boolean aPressed, boolean dPressed, double frameTimeDiffMod) {
		//Moves all the Objects one turn checking for collisions
		if(isPaused() || frameTimeDiffMod > 10)
			return;
		
		movePad(aPressed,dPressed,frameTimeDiffMod);

		//Ball movement
		Iterator<Ball> it = balls.iterator();
		while(it.hasNext()) {
			Ball b = it.next();
			double ballSpeed = BASESPEED*b.getSpeedMult()*frameTimeDiffMod;
			Ball tmp = new Ball(b);
			tmp.getPosition().add(b.getDirection().multBy(ballSpeed));  //create a temporary ball at the next Position

			if(isCollidingWithBorder(tmp)) { //ball hit a Border
				Side collisionSide = getCollisionSide(new Vector2D(0,0), new Vector2D(FIELDWIDTH,FIELDHEIGHT), tmp);
				handleBallBorderCollision(it, b, tmp,collisionSide);
			}
			if( areColliding(pad, tmp))  //ball hit the pad
				handleBallPadCollision(b, tmp);
			else                        
				handleBallBlockCollisions(b,tmp);

			b.getPosition().add(b.getDirection().multBy(ballSpeed));
		}

	}
	
	private void movePad(boolean aPressed, boolean dPressed, double frameTimeDiff) {
		//moves the Pad and checks for collisions with the window Border
		double padSpeed = BASESPEED*2*frameTimeDiff;
		if(aPressed && dPressed) {}
		else if(aPressed && ((pad.getPosition().getX()-padSpeed) > 0))                                //if the new pos is within the canvas
			pad.setPosition(pad.getPosition().getX()-padSpeed,  pad.getPosition().getY()); 
		else if(dPressed && ((pad.getPosition().getX()+pad.getWidth()+padSpeed) < FIELDWIDTH))
			pad.setPosition(pad.getPosition().getX()+padSpeed,  pad.getPosition().getY());
	}
	
	private void handleBallBorderCollision(Iterator<Ball> it,Ball b,Ball tmp,Side side) {
		if(side == Side.BOTTOM) { // ball dropped off the play area
			it.remove();
			this.lives--;
			if(lives > 0)
				pad.addBall(new Ball());
		}
		else if(side == Side.TOP)                  //ball hit the top
			b.reflectBorder(Side.TOP);
		else                			//ball hit a wall
			b.reflectBorder(side);
	}
	private void handleBallPadCollision(Ball b, Ball tmp) {
		Side collisionSide = getCollisionSide(pad,tmp);
		if(collisionSide == Side.TOP) {
			double dist = getDistFromCenter(pad,tmp);
			if(Math.abs(dist) < (pad.getWidth()*0.20)/2)//deadzone in the middle of the pad is hit
				b.reflectSprite(Side.TOP);
			else if(dist > 0) {                               //right side of the pad is hit
				b.setDirection(0,-1);
				b.getDirection().rotate(((dist - (pad.getWidth()*0.20/2))/(pad.getWidth()*0.4))*(Math.PI/3));
			}
			else {											//left side of the pad is hit
				b.setDirection(0,-1);
				b.getDirection().rotate(-((Math.abs(dist) - (pad.getWidth()*0.20/2))/(pad.getWidth()*0.4))*(Math.PI/3));
			}
			if(b.getSpeedMult() < MAXSPEED)
				b.setSpeedMult(b.getSpeedMult()+0.025);
		}
		else if(collisionSide == Side.LEFT){ 
			b.setDirection(0, -1);
			b.getDirection().rotate(-Math.PI/3);

		}
		else{												//ball hit the right of the pad
			b.setDirection(0, -1);
			b.getDirection().rotate(Math.PI/3);
		}
	}
	private void handleBallBlockCollisions(Ball b, Ball tmp) { //improve this logic
		ArrayList<Block> collidingBlocks = blocks.findColliding(tmp);
		if(collidingBlocks.isEmpty())
			return; //no collision, nothing to do
		
		if(collidingBlocks.size() > 1) {
			Block block = collidingBlocks.get(0);
			Block block2 = collidingBlocks.get(1);
			Side collisionSide1 = getCollisionSide(block,tmp);
			Side collisionSide2 = getCollisionSide(block2,tmp);
			if(collisionSide1 == null && collisionSide2 == null) //invalid collision
				return;
			
			if(collisionSide1 == collisionSide2) { // if two blocks of the same orientation are hit
				if(areColliding(block,tmp) || areColliding(block2,tmp)) { 
					b.reflectSprite(collisionSide1);
					block.getHitBy(b);
					if(block.getHitpoints() == 0) {
						points += block.getPoints();
						blocks.delete(block);
					}
				}
			}
			else {
				if(areColliding(block,tmp) && areColliding(block2,tmp)) {
					if(block.getPosition().getY() == block2.getPosition().getY()) //if the vertical slit between blocks is hit
						b.reflectSprite(Side.BOTTOM);
					else if(block.getPosition().getX() == block2.getPosition().getX()) //if the horizontal slit between blocks is hit
						b.reflectSprite(Side.LEFT);
					else {                            //if the ball hit two Blocks in a corner
						b.reflectSprite(Side.BOTTOM);
						b.reflectSprite(Side.LEFT);
					}

					block.getHitBy(b);
					if(block.getHitpoints() == 0) {
						points += block.getPoints();
						blocks.delete(block);
					}
					block2.getHitBy(b);
					if(block2.getHitpoints() == 0) {
						points += block2.getPoints();
						blocks.delete(block2);
					}
				}
			}
		}
		else { // only one block is hit
			Block block = collidingBlocks.get(0);
			if(areColliding(block, tmp)) {				// then apply an accurate collision detection
				Side collisionSide = getCollisionSide(block,tmp);
				if (collisionSide == null)//the side detection of tmp didn~t lead to a viable result
					collisionSide = getCollisionSide(block,b);
				if (collisionSide == null)
					return;
				b.reflectSprite(collisionSide);
				block.getHitBy(b);
				if(block.getHitpoints() == 0) {
					points += block.getPoints();
					blocks.delete(block);
				}
			}
		}
	}
	private void pushOut(Sprite sprite, Ball ball, Side side) { //improve to a direction based approach
		//pushes the Ball out of the Object as close to the entry site as possible
		// the -1/+1 is to stop a second collision to occur
		if(side == Side.TOP)
			ball.setPosition(ball.getPosition().getX(),  sprite.getPosition().getY()-ball.getHeight()-1);
		else if(side == Side.RIGHT)
			ball.setPosition(sprite.getPosition().getX()+sprite.getWidth()+1,  ball.getPosition().getY());
		else if(side == Side.BOTTOM)
			ball.setPosition(ball.getPosition().getX(),  sprite.getPosition().getY()+sprite.getHeight()+1);
		else //side == left
			ball.setPosition(sprite.getPosition().getX()-ball.getWidth()-1,  ball.getPosition().getY());
	}
	
	private double getDistFromCenter(Pad pad, Ball ball) {
		//finds the distance between the balls center and the center of the Pad
		double padCenterX = pad.getPosition().getX()+pad.getWidth()/2;
		double ballCenterX = ball.getPosition().getX()+ball.getWidth()/2;
		return ballCenterX - padCenterX;
	}
	private boolean isCollidingWithBorder(Sprite sprite) {
		return ((sprite.getPosition().getX() < 0) || ((sprite.getPosition().getX()+sprite.getWidth()) > FIELDWIDTH) || 
				(sprite.getPosition().getY() < 0) || ((sprite.getPosition().getY()+sprite.getHeight()) > FIELDHEIGHT));		
	} 
	private boolean areColliding(Pad pad, Ball ball) {
		return areColliding(pad.getPosition(),pad.getSize(),ball);
	}
	private boolean areColliding(Block block, Ball ball) {
		return areColliding(block.getPosition(),block.getSize(), ball);
	}
	private boolean areColliding(Vector2D rectPos,Vector2D rectSize, Ball ball) {
		//thanks to https://stackoverflow.com/questions/401847/circle-rectangle-collision-detection-intersection for the nice algorithm
		//get the center coordinates of the objects
		double padMiddleX = rectPos.getX()+rectSize.getX()/2;
		double padMiddleY = rectPos.getY()+rectSize.getY()/2;
		double ballMiddleX = ball.getPosition().getX()+ball.getWidth()/2;
		double ballMiddleY = ball.getPosition().getY()+ball.getHeight()/2;

		//find the Distance between the center coordinates of the Objects
		double middleDistX = Math.abs(padMiddleX - ballMiddleX);
		double middleDistY = Math.abs(padMiddleY - ballMiddleY);

		//the objects are so far apart, that they can't intersect
		if((middleDistX > (rectSize.getX()/2 + ball.getWidth()/2)) || (middleDistY > (rectSize.getY()/2 + ball.getWidth()/2)))
			return false;

		//the Objects are so close that they have to intersect
		if((middleDistX <= (rectSize.getX()/2)) || (middleDistY <= (rectSize.getY()/2)))
			return true;

		//special case of an intersection with the corner
		double cornerDistance = Math.pow(middleDistX - rectSize.getX()/2,2) + Math.pow(middleDistY - rectSize.getY(),2);
		return (cornerDistance <= Math.pow(ball.getWidth()/2,2));// lightly inacurate, to improve gameplay
	}

	public String loadNextLevel() {
		return loadLevel(System.getProperty("user.dir")+"/level/stage"+currLevel+".txt");
	}
	public String loadLevel(String filename) {   //todo make this more robust
		//reads a new level file into the Blocktree and returns the path to the BackgroundImage
		Block[][] field = new Block[15][11];
		double blockWidth = FIELDWIDTH/11;
		double blockHeight = (FIELDHEIGHT*0.66)/15;
		String backgroundImagePath = "/level/backgrounds/bg.png";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			int lineCounter = 0;
			String line;
			
			String[] args;
			
			while((line = br.readLine()) != null) {
				if(line.startsWith("bg"))
					backgroundImagePath = line.split("=")[1];
					
				if(line.startsWith("(")) { 
					Alignment align = Alignment.CENTER;
					Color color = Color.HOTPINK;
					line = line.replace("(", "").replace(")", "").strip();
					args = line.split(",");
					int nrOfBlocks = 11;
					try {
						color = parseColor(args[0]);
					}catch(IndexOutOfBoundsException e) {}//if no color is specified assume a hotpink color
					try {                                         
						nrOfBlocks = Integer.parseInt(args[1]);
					}
					catch(IndexOutOfBoundsException e) {}//if no parameter is given assume a full line
					try {
						align = parseAlignment(args[2]);
					}
					catch(IndexOutOfBoundsException e) {} //if no parameter is given assume a centered alignment

					if((nrOfBlocks < 0) || (nrOfBlocks > 11))
						continue;
					if(align == Alignment.CENTER) {
					for(int i : new int[] {5,4,6,3,7,2,8,1,9,0,10}) {//add the Blocks centered
						if(nrOfBlocks == 0)
							continue;
						field[lineCounter][i] = new Block(i*blockWidth,lineCounter*blockHeight,blockWidth,blockHeight,color,currLevel);
						nrOfBlocks--;
					}
					}
					else if (align == Alignment.LEFT) {
						for(int i = 0;i<nrOfBlocks;i++) 
							field[lineCounter][i] = new Block(i*blockWidth,lineCounter*blockHeight,blockWidth,blockHeight,color,currLevel);
					}
					else {
						for(int i = 11-nrOfBlocks;i<11;i++) 
							field[lineCounter][i] = new Block(i*blockWidth,lineCounter*blockHeight,blockWidth,blockHeight,color,currLevel);
					}
					lineCounter++;
				}
				else if(line.startsWith("{")) {
					line = line.replace("{", "").replace("}", "").strip();
					args = line.split(",");
					for(int i=0;i<11;i++) {
						try {
							if(args[i].equals("")) //no parameters given defaults to draw an empty line
								continue;
							field[lineCounter][i] = new Block(i*blockWidth,lineCounter*blockHeight,blockWidth,blockHeight,parseColor(args[i]),currLevel);
						}catch(IndexOutOfBoundsException e) {}//don't add a Block if the field is empty
					}
					lineCounter++;
				}
			}
			br.close();
		}catch(IOException e) {
			System.out.println(e.getMessage());
		}
		
		//add the Blocks in a BST friendly manor to the BlockTree
		for(int i : new int[] {7,3,11,1,5,9,13,0,2,4,6,8,10,12,14}) {
			if(field[i] == null)
				continue;
			for(int j : new int[] {5,2,8,1,4,6,9,0,3,7,10}) {
				if(field[i][j] != null)
					blocks.insert(field[i][j]);
			}
		}
		
		//init a pad at the middle of the canvas, 10% above the bottom
		pad = new Pad(FIELDWIDTH/2-blockWidth*1.33/2,FIELDHEIGHT*0.9,blockWidth*1.33,blockHeight*0.66);
		pad.addBall(new Ball());
		
		return backgroundImagePath;
	}

	private Side getCollisionSide(Block block, Ball ball) {
		Side side = getCollisionSide(block.getPosition(),block.getSize(),ball);
		if((side == Side.LEFT && ball.getDirection().getX() < 0) || //filter out false collisionside detection at corners
				(side == Side.RIGHT && ball.getDirection().getX() > 0) ||
				(side == Side.TOP && ball.getDirection().getY() < 0) ||
				(side == Side.BOTTOM && ball.getDirection().getY() > 0))
				return null;
		return side;
	}
	private Side getCollisionSide(Pad pad, Ball ball) {
		return getCollisionSide(pad.getPosition(),pad.getSize(),ball);
	}
	private Side getCollisionSide(Vector2D rectPos, Vector2D rectSize, Sprite s) {
		//returns the side of a rectangle on which a collision with s has occurred
		double ballCenterX = s.getPosition().getX()+s.getWidth()/2;
		double ballCenterY = s.getPosition().getY()+s.getHeight()/2;
		double min = Math.abs(rectPos.getY() - ballCenterY); // topBorderDist
		Side side = Side.TOP;
		
		double bottomBorderDist = Math.abs((rectPos.getY()+rectSize.getY()) - ballCenterY);
		if( bottomBorderDist < min) {
			min = bottomBorderDist;
			side = Side.BOTTOM;
		}
		
		double leftBorderDist = Math.abs(rectPos.getX() - ballCenterX);
		if( leftBorderDist < min ) {
			min = leftBorderDist;
			side = Side.LEFT;
		}
		
		double rightBorderDist = Math.abs((rectPos.getX()+rectSize.getX()) - ballCenterX);
		if( rightBorderDist < min)
			side = Side.RIGHT;
		
		return side;
	}

	private Color parseColor(String color) {
		switch(color) {
		case "white":
			return Color.web("fcfcfd");
		case "orange":
			return Color.web("fd7560");
		case "lightblue":
			return Color.web("3bbdfc");
		case "green":
			return Color.web("82d118");
		case "red":
			return Color.web("d92400");
		case "blue":
			return Color.web("006ce6");
		case "pink":
			return Color.web("fc75b5");
		case "yellow":
			return Color.web("fc9a35");
		case "silver":
			return Color.web("bdbdbd");
		case "gold":
			return Color.web("f1bd3a");
		default:
			return Color.HOTPINK;
		}
	}
	private Alignment parseAlignment(String align) {
		switch(align) {
		case "left":
			return Alignment.LEFT;
		case "center":
			return Alignment.CENTER;
		case "right":
			return Alignment.RIGHT;
		default:
			return Alignment.CENTER;
		}
	}
	public void printTree() {
		blocks.print();
	}
	public Pad getPad() {
		return pad;
	}
	public ArrayList<Ball> getBalls(){
		return balls;
	}
	public void fireBall() {
		if(pad.hasBall()) {
			balls.add(pad.getOneBall());
		}
	}
}
