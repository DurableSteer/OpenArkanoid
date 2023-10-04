package openArcanoid;


import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
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
	private double RENDERWIDTH = 600;
	private double RENDERHEIGHT = 400;
	private double windowWidthMod = 1;
	private double windowHeightMod = 1;
	private Canvas canvas = new Canvas(RENDERWIDTH*windowWidthMod,RENDERHEIGHT*windowHeightMod);
	protected static Engine engine = new Engine();
	private AnimationTimer loop;
	private final String FONTPATH = getClass().getResource("/main/resources/WestEnglandRegular.ttf").toString();
	private BackgroundImage background;
	private final int FONTSIZE = 47;
	private final Color FONTCOLOR = Color.WHITE;
	public final Label cursor = new Label("🢒 ");
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("OpenArkanoid");
		primaryStage.setOnCloseRequest(event -> loop.stop());
		primaryStage.setResizable(false);
		cursor.setTextFill(FONTCOLOR);
		cursor.setFont(new Font("",FONTSIZE));
		cursor.setPadding(new Insets(0,0,8,0));
		primaryStage.fullScreenProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> prop, Boolean wasIconified, Boolean isIconified) {
            }
        });
		loop = new AnimationTimer() {
			private long lastCallTime = 0;
			private long lastDrawTime = 0;
			@Override
			public void handle(long now) {
					engine.next(aPressed, dPressed, ((double)(now-lastCallTime))/3000000);
					if(engine.gameOver())
						drawGameOver(primaryStage, canvas.getGraphicsContext2D());
					else if (engine.levelCleared()) {
						drawStageCleared(primaryStage,canvas.getGraphicsContext2D());
					}
					else if(engine.isPaused()) {
						loop.stop();
						drawMenu(primaryStage);
					}
					else if(now-lastDrawTime > 15384615 ) { //redraw only at ca 65hz to save recources(javafx is capped at 60fps anyways)
						drawStage(primaryStage);
						lastDrawTime = now;
					}

				lastCallTime = now;
			}
		};
		drawMenu(primaryStage);
	}
	public void drawMenu(Stage primaryStage) {
		VBox root  = new VBox();
		root.setAlignment(Pos.CENTER);
		Scene scene = new Scene(root, RENDERWIDTH*windowWidthMod, RENDERHEIGHT*windowHeightMod);
		root.setBackground(new Background(new BackgroundFill(Color.BLACK,CornerRadii.EMPTY,Insets.EMPTY)));
		GridPane menuGrid = new GridPane();
		menuGrid.getColumnConstraints().add(new ColumnConstraints(30));
		ColumnConstraints menuColConst = new ColumnConstraints();
		menuColConst.setHalignment(HPos.CENTER);
		menuGrid.getColumnConstraints().add(menuColConst);
		for(int i=0;i<4;i++)
			menuGrid.getRowConstraints().add(new RowConstraints(35));
		menuGrid.setAlignment(Pos.BASELINE_CENTER);
		LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,new Stop[] {new Stop(0,Color.web("#27198b")), new Stop(0.5,Color.web("#a66fd7")), new Stop(1,Color.WHITE)});
		Label title = new Label("OpenArkanoid");
		title.setTextFill(gradient);
		title.setFont(Font.loadFont(FONTPATH, FONTSIZE+15));
		title.setPadding(new Insets(0,0,40,0));
		root.getChildren().add(title);
		Label newGame = new Label("New Game");
		Label quit = new Label("Quit");
		newGame.setFont(Font.loadFont(FONTPATH, FONTSIZE));
		newGame.setTextFill(FONTCOLOR);
		quit.setFont(Font.loadFont(FONTPATH, FONTSIZE));
		quit.setTextFill(FONTCOLOR);
		newGame.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				engine.reset();
				String bgPath = engine.loadNextLevel();
				try {
					background = new BackgroundImage(new Image(getClass().getResourceAsStream(bgPath)),BackgroundRepeat.REPEAT,BackgroundRepeat.REPEAT,BackgroundPosition.CENTER,new BackgroundSize(1024,1024,false,false,true,false));
				} catch (Exception e1) {
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
		newGame.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				menuGrid.add(cursor,0,1);
			}
		});
		quit.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				menuGrid.add(cursor,0,3);
			}
		});
		newGame.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				menuGrid.getChildren().remove(cursor);
			}
		});
		quit.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				menuGrid.getChildren().remove(cursor);
			}
		});

		if(engine.isPaused()) {
			Label continueGame = new Label("continue");
			continueGame.setFont(Font.loadFont(FONTPATH, FONTSIZE));
			continueGame.setTextFill(FONTCOLOR);
			continueGame.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					engine.unpause();
					loop.start();
				}
			});
			continueGame.setOnMouseEntered(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					menuGrid.add(cursor,0,0);

				}
			});
			continueGame.setOnMouseExited(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					menuGrid.getChildren().remove(cursor);
				}
			});
			menuGrid.add(continueGame,1,0);
		}
		menuGrid.add(newGame,1,1);
		if(!engine.isPaused()) {
			Label settings = new Label("Settings");
			settings.setFont(Font.loadFont(FONTPATH, FONTSIZE));
			settings.setTextFill(FONTCOLOR);
			settings.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					drawSettings(primaryStage);
				}
			});
			settings.setOnMouseEntered(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					menuGrid.add(cursor,0,2);
				}
			});
			settings.setOnMouseExited(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					menuGrid.getChildren().remove(cursor);
				}
			});
			menuGrid.add(settings,1,2);
		}
		menuGrid.add(quit,1,3);
		menuGrid.setMaxSize(RENDERWIDTH*windowWidthMod*0.8, RENDERHEIGHT*windowHeightMod*0.9);
		menuGrid.setPadding(new Insets(0,menuGrid.getColumnConstraints().get(0).getPrefWidth(),0,0));
		root.getChildren().add(menuGrid);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void drawSettings(Stage primaryStage) {
		StackPane root  = new StackPane();
		root.setAlignment(Pos.CENTER);
		Scene scene = new Scene(root, RENDERWIDTH*windowWidthMod, RENDERHEIGHT*windowHeightMod);
		root.setBackground(new Background(new BackgroundFill(Color.BLACK,CornerRadii.EMPTY,Insets.EMPTY)));
		GridPane menuGrid = new GridPane(2,2);
		menuGrid.getColumnConstraints().add(new ColumnConstraints(30));
		ColumnConstraints menuColConst = new ColumnConstraints();
		menuColConst.setHalignment(HPos.CENTER);
		menuGrid.getColumnConstraints().add(menuColConst);
		for(int i=0;i<2;i++)
			menuGrid.getRowConstraints().add(new RowConstraints(35));
		menuGrid.setAlignment(Pos.CENTER);
		TextField size = new TextField(RENDERWIDTH*windowWidthMod+"x"+RENDERHEIGHT*windowHeightMod);
		Label setSize = new Label("Set Size");
		setSize.setFont(Font.loadFont(FONTPATH, FONTSIZE));
		setSize.setTextFill(FONTCOLOR);
		setSize.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				try {
					String[] args = size.getText().strip().split("x");
					windowWidthMod = Double.parseDouble(args[0])/RENDERWIDTH;
					windowHeightMod = Double.parseDouble(args[1])/RENDERHEIGHT;
					canvas = new Canvas(RENDERWIDTH*windowWidthMod,RENDERHEIGHT*windowHeightMod);
					drawMenu(primaryStage);
				}catch(IndexOutOfBoundsException ex) {
					System.out.println("Please input the new windowsize in the following format: 1280x720 .");
				}
			}
		});
		setSize.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				menuGrid.add(cursor, 0, 1);
			}
		});
		setSize.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				menuGrid.getChildren().remove(cursor);
			}
		});
		menuGrid.add(size,1,0);
		menuGrid.add(setSize,1,1);
		menuGrid.setPadding(new Insets(0,menuGrid.getColumnConstraints().get(0).getPrefWidth(),0,0));//center only the right column to the window
		menuGrid.setMaxSize(RENDERWIDTH*windowWidthMod*0.8, RENDERHEIGHT*windowHeightMod*0.9);
		root.getChildren().add(menuGrid);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void drawStage(Stage primaryStage) {
		StackPane root  = new StackPane();
		Canvas nextFrame = new Canvas(RENDERWIDTH*windowWidthMod,RENDERHEIGHT*windowHeightMod);
		root.setBackground(new Background(background));
		Scene scene = new Scene(root, RENDERWIDTH*windowWidthMod+24, RENDERHEIGHT*windowHeightMod+22);
		Canvas borderCanvas = new Canvas(RENDERWIDTH*windowWidthMod+24,RENDERHEIGHT*windowHeightMod+22);
		drawBorder(borderCanvas.getGraphicsContext2D());
		root.getChildren().add(borderCanvas);
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
					if(engine.getPad().isLaser())
						engine.fireLaser();
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
		GraphicsContext gc = nextFrame.getGraphicsContext2D();
		drawPowerUps(gc);
		drawMovables(gc);
		drawBlocks(gc);
		canvas = nextFrame;
		root.getChildren().add(canvas);

		primaryStage.setScene(scene);
		primaryStage.show();
	}
	private void drawBlocks(GraphicsContext gc) {
		for(Block b : engine.getAllBlocks()) {
			double xPos = b.getPosition().getX()*windowWidthMod;
			double yPos = b.getPosition().getY()*windowHeightMod;
			double width = b.getWidth()*windowWidthMod;
			double height = b.getHeight()*windowHeightMod;
			gc.setLineWidth(2);
			gc.setFill(b.getColor());
			gc.fillRect(xPos,yPos,width,height);
			gc.setStroke(Color.BLACK);
			gc.setLineWidth(2);
			gc.strokeRect(xPos,yPos,width,height);
			if(b.getHitpoints() > 1) {
				double linewidth = width*0.04;
				double offset = linewidth/2+1;
				gc.setLineWidth(linewidth);
				gc.strokeLine(xPos+offset+linewidth, yPos+height-offset, xPos+width-offset, yPos+height-offset);
				gc.strokeLine(xPos+width-offset,yPos+height-offset,xPos+width-offset,yPos+offset+linewidth);
			}
		}
	}
	private void drawPowerUps(GraphicsContext gc) {
		for(PowerUp p : engine.getPowerUps()) {
			double xPos = p.getPosition().getX()*windowWidthMod;
			double yPos = p.getPosition().getY()*windowHeightMod;
			double width = p.getWidth()*windowWidthMod;
			double height = p.getHeight()*windowHeightMod;
			gc.setFill(p.getColor());
			gc.fillRoundRect(xPos, yPos, width, height, 10, 10);

		}
	}
	private void drawMovables(GraphicsContext gc) {
		MotionBlur mb = new MotionBlur();
		mb.setRadius(4);
		for(Ball b : engine.getBalls()) {
			double xPos = b.getPosition().getX()*windowWidthMod;
			double yPos = b.getPosition().getY()*windowHeightMod;
			double width = b.getWidth()*windowWidthMod;
			double height = b.getHeight()*windowHeightMod;
			gc.setFill(b.getColor());
			gc.fillOval(xPos, yPos, width+relX(1), height);//warum +1?
			gc.setStroke(Color.WHITE);
			gc.strokeOval(xPos, yPos, width, height);
		}
		gc.applyEffect(mb);
		if(engine.getPad().hasBall()) {
			for(Ball b : engine.getPad().getBalls()) {
				double xPos = b.getPosition().getX()*windowWidthMod;
				double yPos = b.getPosition().getY()*windowHeightMod;
				double width = b.getWidth()*windowWidthMod;
				double height = b.getHeight()*windowHeightMod;
				gc.setFill(b.getColor());
				gc.fillOval(xPos, yPos, width+relX(1), height);//warum +1?
				gc.setStroke(Color.WHITE);
				gc.strokeOval(xPos, yPos, width, height);
			}
		}
		Pad p = engine.getPad();
		double xPos = p.getPosition().getX()*windowWidthMod;
		double yPos = p.getPosition().getY()*windowHeightMod;
		double width = p.getWidth()*windowWidthMod;
		double height = p.getHeight()*windowHeightMod;
		gc.setFill(p.getColor());
		gc.setLineWidth(2);
		if(engine.getPad().isLaser()) {
			gc.fillRect(xPos, yPos, width, height);
			gc.setFill(Color.CYAN);
			gc.fillRect(xPos+relX(2), yPos, width*0.05, height);
			gc.fillRect(xPos+width-relX(6), yPos, width*0.05, height);
			gc.setStroke(Color.WHITE);
			gc.strokeRect(xPos-relX(1), yPos, width+relX(2), height);
			gc.setStroke(Color.ORANGE);
			gc.strokeLine(xPos, yPos+height, xPos+width,yPos+height);
			gc.setFill(p.getColor());
			gc.fillPolygon(new double[]{xPos-relX(2),xPos-relY(2),xPos-relX(2)-width*0.14,xPos-relX(2)-width*0.18}, new double[]{yPos-relY(1),yPos+relY(2)+height,yPos+height+relY(1),yPos+height*0.7}, 4);
			gc.fillPolygon(new double[]{xPos+width+relX(2),xPos+width+relX(2),xPos+width+relX(2)+width*0.14,xPos+width+relX(2)+width*0.18}, new double[]{yPos-relY(1),yPos+relY(2)+height,yPos+height+relY(1),yPos+height*0.7}, 4);
			gc.setStroke(Color.ORANGERED);
			gc.strokePolygon(new double[]{xPos-relX(2),xPos-relX(2),xPos-relX(2)-width*0.14,xPos-relX(2)-width*0.18}, new double[]{yPos-relY(1),yPos+relY(2)+height,yPos+height+relY(1),yPos+height*0.7}, 4);
			gc.strokePolygon(new double[]{xPos+width+relX(2),xPos+width+relX(2),xPos+width+relX(2)+width*0.14,xPos+width+relX(2)+width*0.18}, new double[]{yPos-relY(1),yPos+relY(2)+height,yPos+height+relY(1),yPos+height*0.7},4);

		}
		else {
			double padWidth = width*0.6;
			double bouncerWidth = (width-padWidth)/2;
			gc.setFill(Color.web("d82800"));
			gc.fillRoundRect(xPos+relX(3),yPos-relY(1),bouncerWidth,height+relY(2),relX(12),relY(15));
			gc.fillRoundRect(xPos-relX(3)+width-bouncerWidth,yPos-relY(1),bouncerWidth,height+relY(2),relX(12),relY(15));
			gc.strokeLine(xPos+relX(5), yPos+relY(2), xPos+relX(5), yPos+height-relY(2));
			gc.strokeLine(xPos+width-relX(5), yPos+relY(2), xPos+width-relX(5), yPos+height-relY(2));
			gc.setStroke(Color.CYAN);
			gc.strokeLine(xPos+relX(4), yPos+relY(3), xPos+relX(4), yPos+height-relY(3));
			gc.strokeLine(xPos+width-relX(4), yPos+relY(3), xPos+width-relX(4), yPos+height-relY(3));

			gc.setFill(p.getColor());
			gc.fillRect(xPos+bouncerWidth, yPos, padWidth, height);
			gc.setStroke(Color.WHITE);
			gc.strokeRect(bouncerWidth+xPos, yPos, padWidth, height);
			gc.setStroke(p.getColor());
			gc.strokeLine(xPos+bouncerWidth+relX(2), yPos+height, xPos+padWidth+bouncerWidth-relX(2), yPos+height);


		}
		for(Projectile shot : engine.getShots()) {
			xPos = shot.getPosition().getX()*windowWidthMod;
			yPos = shot.getPosition().getY()*windowHeightMod;
			width = shot.getWidth()*windowWidthMod;
			height = shot.getHeight()*windowHeightMod;
			gc.setFill(shot.getColor());
			gc.fillRect(xPos, yPos, width, height);
		}
	}
	private void drawGameOver(Stage primaryStage,GraphicsContext gc) {
		loop.stop();
		gc.setFill(Color.WHITE);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setFont(Font.loadFont(FONTPATH, FONTSIZE));
		gc.fillText("Game Over!", RENDERWIDTH*windowWidthMod*0.5, RENDERHEIGHT*windowHeightMod*0.4);//centered
		gc.setFont(Font.loadFont(FONTPATH, FONTSIZE-20));
		gc.fillText("Your Score: "+engine.getPoints(), RENDERWIDTH*windowWidthMod*0.5, RENDERHEIGHT*windowHeightMod*0.48);
		gc.setFont(Font.loadFont(FONTPATH, FONTSIZE-35));
		gc.fillText("Press any key to continue.", RENDERWIDTH*windowWidthMod*0.5, RENDERHEIGHT*windowHeightMod*0.87);
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
		gc.setFont(Font.loadFont(FONTPATH, FONTSIZE));
		gc.setFill(FONTCOLOR);
		gc.fillText("Stage Clear!", RENDERWIDTH*windowWidthMod*0.5, RENDERHEIGHT*windowHeightMod*0.4);
		gc.setFont(Font.loadFont(FONTPATH, FONTSIZE-35));
		gc.fillText("Press any key to continue.",RENDERWIDTH*windowWidthMod*0.5,RENDERHEIGHT*windowHeightMod*0.87);
		canvas.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				canvas.getScene().removeEventFilter(KeyEvent.KEY_PRESSED, this);
				String bgPath = engine.loadNextLevel();
				try {
					background = new BackgroundImage(new Image(getClass().getResourceAsStream(bgPath)),BackgroundRepeat.REPEAT,BackgroundRepeat.REPEAT,BackgroundPosition.CENTER,new BackgroundSize(1024,1024,false,false,true,false));
				} catch (Exception e1) {
					System.out.println(e1.getMessage());
				}
				loop.start();
				drawStage(primaryStage);
			}
		});
	}
	private void drawBorder(GraphicsContext gc) {
		Canvas borderCanvas = gc.getCanvas();
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, 12, borderCanvas.getHeight());
		gc.fillRect(borderCanvas.getWidth()-12, 0, 12, borderCanvas.getHeight());
		gc.fillRect(0, 0, borderCanvas.getWidth(), 12);
		gc.setFill(Color.web("#bdbdbb"));
		gc.fillRoundRect(2, 2, 8, borderCanvas.getHeight(), 7, 7);
		gc.fillRoundRect(borderCanvas.getWidth()-10, 2, 8, borderCanvas.getHeight(), 7, 7);
		gc.fillRoundRect(2, 2, borderCanvas.getWidth()-4, 8, 7, 7);
		//gc.fillRoundRect(2, borderCanvas.getHeight()-10, borderCanvas.getWidth()-4, 8, 7, 7);

		for(int i=0;i < ((borderCanvas.getWidth())/100);i++) {
			drawVent(gc,35+i*100,0,true);
			//drawVent(gc,35+i*100,borderCanvas.getHeight()-12,true);
		}
		for(int i=0;i < ((borderCanvas.getHeight())/100);i++) {
			drawVent(gc,0,35+i*100,false);
			drawVent(gc,borderCanvas.getWidth()-12,35+i*100,false);
		}

	}
	private void drawVent(GraphicsContext gc, double x, double y, boolean horizontal) {
		gc.setFill(Color.web("#bdbdbb"));
		double xSize = 50;
		double ySize = 12;
		if(!horizontal) {
			xSize = 12;
			ySize = 50;
		}
		gc.fillRoundRect(x, y, xSize, ySize, 7, 7);
		for(int i=0;i<5;i++) {
			if(horizontal)
				gc.strokeRect(x+xSize/3+i*4,y,1,ySize);
			else
				gc.strokeRect(x,y+ySize/3+i*4,xSize,1);
		}
		gc.strokeRoundRect(x, y, xSize, ySize, 7, 7);
	}

	private double relX(double abs) {
		//returns the relative length to abs
		return abs*windowWidthMod;
	}
	private double relY(double abs) {
		//returns the relative length to abs
		return abs*windowHeightMod;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
