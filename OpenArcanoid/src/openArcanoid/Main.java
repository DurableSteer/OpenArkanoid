package openArcanoid;


import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.MotionBlur;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class Main extends Application {
	private static volatile boolean aPressed = false;
	private static volatile boolean dPressed = false;
	private final double WINDOWWIDTH = 600;
	private final double WINDOWHEIGHT = 400;
	private Canvas canvas = new Canvas(WINDOWWIDTH,WINDOWHEIGHT);
	protected static Engine engine = new Engine();
	private AnimationTimer loop;
	private final String FONTTYPE = "Unispace-bold";
	private BackgroundImage background;
	private final int FONTSIZE = 47;
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("OpenArkanoid");
		primaryStage.setOnCloseRequest(event -> loop.stop());
		drawMenu(primaryStage);
		loop = new AnimationTimer() {
			@Override
			public void handle(long now) {
				engine.next(aPressed,dPressed,WINDOWWIDTH,WINDOWHEIGHT);
				if(engine.gameOver()) 
					drawGameOver(primaryStage, canvas.getGraphicsContext2D());
				else if (engine.levelCleared())
					drawStageCleared(primaryStage,canvas.getGraphicsContext2D());
				else
					drawStage(primaryStage);
			}
		};
	}
	public void drawMenu(Stage primaryStage) {
		StackPane root  = new StackPane();
		root.setAlignment(Pos.CENTER);
		Scene scene = new Scene(root, WINDOWWIDTH, WINDOWHEIGHT);
		root.setBackground(new Background(new BackgroundFill(Color.LIGHTCYAN,CornerRadii.EMPTY,Insets.EMPTY)));
		VBox MenuBox = new VBox();
		MenuBox.setBackground(new Background(new BackgroundFill(Color.ANTIQUEWHITE,CornerRadii.EMPTY,Insets.EMPTY)));
		MenuBox.setAlignment(Pos.CENTER);
		Label play = new Label("Play");
		Label quit = new Label("Quit");
		play.setFont(new Font(FONTTYPE,FONTSIZE));
		quit.setFont(new Font(FONTTYPE,FONTSIZE));
		play.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				engine.reset();
				String bgPath = engine.loadNextLevel(canvas.getWidth()/11,(canvas.getHeight()*0.66)/15);
				try {
					background = new BackgroundImage(new Image(new FileInputStream(System.getProperty("user.dir")+bgPath)),BackgroundRepeat.REPEAT,BackgroundRepeat.REPEAT,BackgroundPosition.CENTER,new BackgroundSize(1024,1024,false,false,true,false));
				} catch (FileNotFoundException e1) {
					System.out.println(e1.getMessage());
				}
				loop.start();
				drawStage(primaryStage);
			}
		});
		quit.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				System.exit(0);
			}
		});

		play.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				play.setScaleX(1.5);
				play.setScaleY(1.5);
			}
		});
		quit.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				quit.setScaleX(1.5);
				quit.setScaleY(1.5);
			}
		});
		play.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				play.setScaleX(1);
				play.setScaleY(1);
			}
		});
		quit.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				quit.setScaleX(1);
				quit.setScaleY(1);
			}
		});
		MenuBox.getChildren().add(play);
		MenuBox.getChildren().add(quit);
		MenuBox.setMaxSize(WINDOWWIDTH*0.8, WINDOWHEIGHT*0.9);
		root.getChildren().add(MenuBox);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	public void drawStage(Stage primaryStage) {
		StackPane root  = new StackPane();
		root.setAlignment(Pos.CENTER);
		root.setBackground(new Background(background));
		Scene scene = new Scene(root, WINDOWWIDTH, WINDOWHEIGHT);
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				switch(e.getCode()) {
				case A:{
					aPressed = true;
					break;
				}
				case D:{
					dPressed = true;
					break;
				}
				case SPACE:{
					engine.fireBall();
					break;
				}
				case ESCAPE:{
					engine.pause();
				}
				default:{break;}
				}
			}
			});
		scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				switch(e.getCode()) {
				case A :{
					aPressed = false;
					break;
				}
				case D:{
					dPressed = false;
					break;
				}
				default:{break;}
				}
			}
			});
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		drawBlocks(gc);
		drawMovables(gc);
		root.getChildren().add(canvas);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	private void drawBlocks(GraphicsContext gc) {
		for(Block b : engine.getAllBlocks()) {
			gc.setFill(b.getColor());
			gc.fillRect(b.getPosition().getX(), b.getPosition().getY(), b.getWidth(), b.getHeight());
			gc.setStroke(Color.BLACK);
			gc.strokeRect(b.getPosition().getX(), b.getPosition().getY(), b.getWidth(), b.getHeight());
			if(b.getHitpoints()>1)
				gc.strokeRect(b.getPosition().getX()+b.getWidth()*0.02, b.getPosition().getY()+b.getHeight()*0.05, b.getWidth()*0.96, b.getHeight()*0.9);
		}
	}
	private void drawMovables(GraphicsContext gc) {
		gc.setFill(engine.getPad().getColor());
		gc.fillRect(engine.getPad().getPosition().getX(), engine.getPad().getPosition().getY(), engine.getPad().getWidth(), engine.getPad().getHeight());
		gc.setStroke(Color.BLACK);
		gc.strokeRect(engine.getPad().getPosition().getX(), engine.getPad().getPosition().getY(), engine.getPad().getWidth(), engine.getPad().getHeight());
		for(Ball b : engine.getBalls()) {
			gc.setFill(b.getColor());
			gc.fillOval(b.getPosition().getX(), b.getPosition().getY(), b.getWidth(), b.getHeight());
			gc.setStroke(Color.WHITE);
			gc.strokeOval(b.getPosition().getX(), b.getPosition().getY(), b.getWidth(), b.getHeight());
		}
		if(engine.getPad().hasBall()) {
			Ball b = engine.getPad().getBall();
			gc.setFill(b.getColor());
			gc.fillOval(b.getPosition().getX(), b.getPosition().getY(), b.getWidth(), b.getHeight());
			gc.setStroke(Color.WHITE);
			gc.strokeOval(b.getPosition().getX(), b.getPosition().getY(), b.getWidth(), b.getHeight());
		}
	}
	private void drawGameOver(Stage primaryStage,GraphicsContext gc) {
		loop.stop();
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setFont(new Font(FONTTYPE,FONTSIZE));
		gc.fillText("Game Over!", WINDOWWIDTH*0.5, WINDOWHEIGHT*0.4);//centered
		gc.setFont(new Font(FONTTYPE,FONTSIZE-20));
		gc.fillText("Your Score: "+engine.getPoints(),WINDOWWIDTH*0.5,WINDOWHEIGHT*0.48);
		gc.setFont(new Font(FONTTYPE,FONTSIZE-35));
		gc.fillText("Press any key to continue.",WINDOWWIDTH*0.5,WINDOWHEIGHT*0.87);
		canvas.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				canvas.getScene().removeEventFilter(KeyEvent.KEY_PRESSED, this);
				drawMenu(primaryStage);
			}
		});
		
	}
	private void drawStageCleared(Stage primaryStage,GraphicsContext gc) {
		loop.stop();
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setFont(new Font(FONTTYPE,FONTSIZE));
		gc.fillText("Stage Clear!", WINDOWWIDTH*0.5, WINDOWHEIGHT*0.4);
		gc.setFont(new Font(FONTTYPE,FONTSIZE-35));
		gc.fillText("Press any key to continue.",WINDOWWIDTH*0.5,WINDOWHEIGHT*0.87);
		canvas.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				canvas.getScene().removeEventFilter(KeyEvent.KEY_PRESSED, this);
				engine.loadNextLevel(canvas.getWidth()/11,(canvas.getHeight()*0.66)/15);
				loop.start();
				drawStage(primaryStage);
			}
		});
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
