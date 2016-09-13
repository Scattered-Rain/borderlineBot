package borderlineBot.gui;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;

import borderlineBot.game.Game;
import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Tile;
import borderlineBot.game.Player;
import borderlineBot.gui.img.ImageUtility;
import borderlineBot.util.Point;

/** GUI for playing/showing the game */
public class GUI extends JPanel implements Runnable{
	
	/** The Title of the Window */
	public static final String TITLE = "Borderline";
	/** The Size in Pixels every Tile gets to take, determines Window size */
	public static final int TILE_IN_PIXELS = 32;
	
	
	/** The game this GUI represents */
	private Game game;
	
	/** The width of a tile in pixels */
	private float tileWidth;
	/** The height of a tile in pixels */
	private float tileHeight;
	
	/** The Thread that this Panel gets redrawn by */
	private Thread thread;
	/** Frame for this Panel */
	private JFrame frame;
	private Point frameSize;
	/** The Images used to draw the tiles (blue, neutral, red) */
	private Image[] tiles;
	/** The Images used to draw the units (index 0 = Players, index 1 = Units Types) */
	private Image[][] units;
	
	
	/** Constructs new GUI */
	public GUI(Game game){
		this.game = game;
		this.frameSize = new Point(TILE_IN_PIXELS*GameBoard.BOARD_SIZE.getX(), TILE_IN_PIXELS*GameBoard.BOARD_SIZE.getY());
		initImages();
		setupFrame();
		setFocusable(true);
		setBackground(Color.BLACK);
		setDoubleBuffered(true);
		frame.add(this);
		frame.setVisible(true);
		this.thread = new Thread(this);
		thread.start();
	}
	
	/** Sets up Frame */
	private void setupFrame(){
		//Define Frame on which this Panel draws itself
		this.frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//The Magic Numbers 6 And 28 Represent The Amount Of Pixels Of 
		//Drawing Space That Get Lost Because Of The Actual Frame.
		frame.setSize(frameSize.getX()+6, frameSize.getY()+28);
		frame.setLocationRelativeTo(null);
		frame.setTitle(TITLE);
		frame.setResizable(false);
	}
	
	/** Initializes Images */
	private void initImages(){
		this.tileWidth = frameSize.getX()/GameBoard.BOARD_SIZE.getX();
		this.tileHeight = frameSize.getY()/GameBoard.BOARD_SIZE.getY();
		int width = (int)tileWidth;
		int height = (int)tileHeight;
		this.tiles = new Image[]{
				ImageUtility.scaleImage(ImageUtility.getImage(ImageUtility.Picture.TILE_BLUE.path()), width, height),
				ImageUtility.scaleImage(ImageUtility.getImage(ImageUtility.Picture.TILE_NEUTRAL.path()), width, height),
				ImageUtility.scaleImage(ImageUtility.getImage(ImageUtility.Picture.TILE_RED.path()), width, height)
		};
		this.units = new Image[][]{
				{
					ImageUtility.scaleImage(ImageUtility.getImage(ImageUtility.Picture.RED_ONE.path()), width, height),
					ImageUtility.scaleImage(ImageUtility.getImage(ImageUtility.Picture.RED_TWO.path()), width, height)
				},
				{
					ImageUtility.scaleImage(ImageUtility.getImage(ImageUtility.Picture.BLUE_ONE.path()), width, height),
					ImageUtility.scaleImage(ImageUtility.getImage(ImageUtility.Picture.BLUE_TWO.path()), width, height)
				}
		};
	}
	
	
	/** Actual Painting Process */
	@Override public void paint(Graphics g){
		super.paint(g);
		Graphics2D g2d = (Graphics2D)g;
		//Draw The base tiles
		final Image blueTile = tiles[0];
		final Image neutralTile = tiles[1];
		final Image redTile = tiles[2];
		for(int cy=0; cy<GameBoard.BOARD_SIZE.getY(); cy++){
			Image toDraw = neutralTile;
			if(cy==0 || cy==GameBoard.BOARD_SIZE.getY()-1){
				toDraw = redTile;
				if((game.getCurrentState().getView().isSame(Player.BLU) && cy==GameBoard.BOARD_SIZE.getY()-1) ||
						(game.getCurrentState().getView().isSame(Player.RED) && cy==0)){
					toDraw = blueTile;
				}
			}
			for(int cx=0; cx<GameBoard.BOARD_SIZE.getX(); cx++){
				g2d.drawImage(toDraw, (int)(cx*this.tileWidth), (int)(cy*this.tileHeight), this);
			}
		}
		//Draw Units
		for(int cy=0; cy<GameBoard.BOARD_SIZE.getY(); cy++){
			for(int cx=0; cx<GameBoard.BOARD_SIZE.getX(); cx++){
				Tile tile = game.getCurrentState().getTile(new Point(cx, cy));
				if(tile.getPlayer().isLegalPlayer()){
					int player = tile.getPlayer().getId()-1;
					int unit = tile.getUnit().getId()-1;
					g2d.drawImage(units[player][unit], (int)(cx*this.tileWidth), (int)(cy*this.tileHeight), this);
				}
			}
		}
	}
	
	/** Changes the Game that is shown */
	public void setNewGame(Game game){
		this.game = game;
	}
	
	/** Repaints Panel */
	public void run() {
		while(true){
			this.repaint();
			try{
				Thread.sleep(15);
			}catch(Exception ex){}
		}
	}
	
}
