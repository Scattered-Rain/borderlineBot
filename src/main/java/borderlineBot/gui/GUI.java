package borderlineBot.gui;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import lombok.Getter;
import borderlineBot.bot.Bot;
import borderlineBot.game.Game;
import borderlineBot.game.GameBoard;
import borderlineBot.game.GameBoard.Move;
import borderlineBot.game.GameBoard.Tile;
import borderlineBot.game.Player;
import borderlineBot.gui.img.ImageUtility;
import borderlineBot.util.Direction;
import borderlineBot.util.Point;

/** GUI for playing/showing the game */
public class GUI extends JPanel implements Runnable, Bot{
	
	/** The Title of the Window */
	public static final String TITLE = "Borderline";
	/** The Size in Pixels every Tile gets to take, determines Window size */
	public static final int TILE_IN_PIXELS = 32;
	
	
	/** The game this GUI represents */
	private Game game;
	
	
	/** If the human player can move, NONE Player if human can't move */
	private Player humanToMove;
	/** The Selected unit by a human */
	private Point humanUnitSelected;
	/** The Move Action (null if not available) */
	private Move humanFinalMoveAction;
	
	
	/** The width of a tile in pixels */
	@Getter private float tileWidth;
	/** The height of a tile in pixels */
	@Getter private float tileHeight;
	
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
		this.humanToMove = Player.NONE;
		this.humanFinalMoveAction = null;
		this.humanUnitSelected = null;
		this.game = game;
		this.frameSize = new Point(TILE_IN_PIXELS*GameBoard.BOARD_SIZE.getX(), TILE_IN_PIXELS*GameBoard.BOARD_SIZE.getY());
		initImages();
		setupFrame();
		setFocusable(true);
		setBackground(Color.BLACK);
		setDoubleBuffered(true);
		addMouseListener(new Mouse(this));
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
		//Reference to the board
		GameBoard currentState = game.getCurrentStateClone();
		//Draw The base tiles
		final Image blueTile = tiles[0];
		final Image neutralTile = tiles[1];
		final Image redTile = tiles[2];
		for(int cy=0; cy<GameBoard.BOARD_SIZE.getY(); cy++){
			Image toDraw = neutralTile;
			if(cy==0 || cy==GameBoard.BOARD_SIZE.getY()-1){
				toDraw = redTile;
				if((currentState.getView().isSame(Player.BLU) && cy==GameBoard.BOARD_SIZE.getY()-1) ||
						(currentState.getView().isSame(Player.RED) && cy==0)){
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
				Tile tile = currentState.getTile(new Point(cx, cy));
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
		this.humanToMove = Player.NONE;
		this.humanFinalMoveAction = null;
		this.humanUnitSelected = null;
		this.game = game;
	}
	
	/** Computes press on Tile x|y */
	public void press(int x, int y, boolean left){
		GameBoard currentState = game.getCurrentStateClone();
		if(left){
			Tile selected = currentState.getTile(new Point(x, y));
			if(humanUnitSelected==null){
				if(!selected.isEmpty() && selected.getPlayer().isSame(this.humanToMove)){
					this.humanUnitSelected = new Point(x, y);
				}
			}
			else{
				for(Move potMove : currentState.generateAllLegalMoves()){
					if(potMove.getUnit(currentState).equals(this.humanUnitSelected) && potMove.getTarget(currentState).equals(new Point(x, y))){
						this.humanFinalMoveAction = potMove;
						return;
					}
				}
				this.humanUnitSelected = null;
			}
		}
		else{
			this.humanUnitSelected = null;
		}
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
	
	/** Interface for Human Player (The request has to be for the Game that is currently presented) */
	public Move move(GameBoard board, Player player) {
		this.humanFinalMoveAction = null;
		this.humanUnitSelected = null;
		this.humanToMove = player;
		while(this.humanFinalMoveAction==null){
			try{
				Thread.sleep(15);
			}catch(Exception ex){}
		}
		Move selectedMove = humanFinalMoveAction;
		this.humanToMove = Player.NONE;
		this.humanFinalMoveAction = null;
		this.humanUnitSelected = null;
		return selectedMove;
	}
	
	
	//--classes--
	/** Mouse Adapter Which is used to recognize inputs */
	public static class Mouse extends MouseAdapter{
		
		/** Reference to the Board Panel */
		private GUI gui;
		
		/** Construct new Mouse */
		public Mouse(GUI gui){
			this.gui = gui;
		}
		
		/** Mouse has been pressed */
		@Override public void mousePressed(MouseEvent e){
			int button = e.getButton();
			if(button == MouseEvent.BUTTON1){
				int x = (int)(e.getX()/gui.getTileWidth());
				int y = (int)(e.getY()/gui.getTileHeight());
				gui.press(x, y, true);
			}
			if(button == MouseEvent.BUTTON2){
				int x = (int)(e.getX()/gui.getTileWidth());
				int y = (int)(e.getY()/gui.getTileHeight());
				gui.press(x, y, false);
			}
		}
		
		/** Mouse has been released */
		@Override public void mouseReleased(MouseEvent e){}
		
	}
	
}
