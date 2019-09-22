package display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;

import entity.Civilization;
import helper.Direction;
import world.Tile;
import world.World;

/**
 * JPanel from which graphics of the World will be drawn 
 * directly on. Uses keyboard and mouse for input.
 * @author Matthew Zane
 * @version 1.1
 * @since 2017-09-30
 */
public class WorldPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Percentage of the height and width of the parent component
	 * Panel height and width equal parent component height and width
	 * times PCT_OF_PARENT divided by 100, respectively.
	 */
	public static final int PCT_OF_PARENT = 85;

	public static final int PARTICLE_RADIUS = 25;

	private World world;
	private Tile[][] grid;
	private Camera camera;

	//used to avoid rounding to zero logic errors
	private static final int GRID_SCALE = 20;

	/**
	 * The current directions that the camera is moving in
	 */
	private ArrayList<Direction> directions = new ArrayList<Direction>();

	private CameraMovementHandler cameraMovementHandler = new CameraMovementHandler();
	private Timer scrollTimer;

	private int scrollSpeed = 100;

	//Don't know what the units are
	private int zoomSpeed = 2;

	public static int getGridScale() { return GRID_SCALE; }

	/**
	 * Initializes a JPanel with a line border and initializes textures
	 * @param worldIn - World object the panel will be displaying from
	 */
	public WorldPanel(World worldIn) {
		super();

		//add listeners
		addMouseWheelListener(new MouseWheelActionListener());
		addKeyListener(new KeyHandler());
		addMouseListener(new MouseHandler());

		world = worldIn;
		grid = world.getGrid();

		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setBackground(Color.BLACK);

		scrollTimer = new Timer(1000 / scrollSpeed, cameraMovementHandler);

		world.start();
	}

	/**
	 * Used to create the camera after the WorldPanel has been created
	 */
	public void createCamera() {
		camera = new Camera(world, this);
		camera.center();
		scrollTimer.start();
	}

	public void paint(Graphics g) {
		super.paint(g);
		paintNatureTiles(g);
		paintCivilizationTiles(g);
		paintCapitals(g);
		paintBorderLines(g);
		//paintGridLines(g);
	}

	private void paintNatureTiles(Graphics g) {
		for (int y = 0; y < grid.length; y++) {
			for (int x = 0; x < grid[0].length; x++) {
				paintNatureTile(x, y, g);
			}
		}
	}

	private void paintNatureTile(int x, int y, Graphics g) {
		Color color = grid[x][y].getType().getColor();
		g.setColor(color);
		int xCoord = (int)(camera.getScaleX() * (GRID_SCALE * x - camera.getXalign())) + 1;
		int yCoord = (int)(camera.getScaleY() * (GRID_SCALE * y - camera.getYalign())) + 1;
		int width = (int)(camera.getScaleX() * GRID_SCALE) + 1;
		int height = (int)(camera.getScaleY() * GRID_SCALE) + 1;
		g.fillRect(xCoord, yCoord, width, height);
	}

	private void paintCivilizationTiles(Graphics g) {
		for (int y = 0; y < grid.length; y++) {
			for (int x = 0; x < grid[0].length; x++) {
				paintCivilizationTile(x, y, g);
			}
		}
	}

	private void paintCivilizationTile(int x, int y, Graphics g) {
		Color color = grid[x][y].getOwner().getColor();
		if (color != null) {
			g.setColor(color);
			int xCoord = (int)(camera.getScaleX() * (GRID_SCALE * x - camera.getXalign())) + 1;
			int yCoord = (int)(camera.getScaleY() * (GRID_SCALE * y - camera.getYalign())) + 1;
			int width = (int)(camera.getScaleX() * (GRID_SCALE * (x + 1) - camera.getXalign())) + 1 - xCoord;
			int height = (int)(camera.getScaleY() * (GRID_SCALE * (y + 1) - camera.getYalign())) + 1 - yCoord;
			g.fillRect(xCoord, yCoord, width, height);
		}
	}
	
	private void paintCapitals(Graphics g)  {
		for (Civilization temp : world.getCivilizations()) {
			paintCapital(temp, g);
		}
	}
	
	private void paintCapital(Civilization civ, Graphics g) {
		g.setColor(Color.BLACK);
		Tile capital = civ.getCapital();
		int x = (int) capital.getLocation().getX();
		int y = (int) capital.getLocation().getY();
		int xCoord = (int)(camera.getScaleX() * (GRID_SCALE * x - camera.getXalign())) + 1;
		int yCoord = (int)(camera.getScaleY() * (GRID_SCALE * y - camera.getYalign())) + 1;
		int width = (int)(camera.getScaleX() * (GRID_SCALE * (x + 1) - camera.getXalign())) + 1 - xCoord;
		int height = (int)(camera.getScaleY() * (GRID_SCALE * (y + 1) - camera.getYalign())) + 1 - yCoord;
		g.fillRect(xCoord, yCoord, width, height);
	}
	
	private void paintBorderLines(Graphics g) {
		for (Civilization temp : world.getCivilizations()) {
			paintBorderLine(temp, g);
		}
	}
	
	private void paintBorderLine(Civilization civ, Graphics g) {
		g.setColor(civ.getColor().darker());
		HashSet<Tile> borderTiles = null;
		while (borderTiles == null) {
			try {
			borderTiles= (HashSet<Tile>) ((HashSet<Tile>) civ.getBorderTiles()).clone();
			} catch (ConcurrentModificationException e) {}
		}
		
		try {
		for (Tile borderTile : borderTiles) {
			for (Tile neighbor : borderTile.getNeighbors()) {
				if (neighbor.getOwner() != civ) {
					int x = (int) borderTile.getLocation().getX();
					int y = (int) borderTile.getLocation().getY();
					//up
					if (neighbor.getLocation().getY() > 
					borderTile.getLocation().getY()) {
						int x1 = (int)(camera.getScaleX() * (GRID_SCALE * x - camera.getXalign()));
						int y1 = (int)(camera.getScaleY() * (GRID_SCALE * (y + 1) - camera.getYalign()));
						int x2 = (int)(camera.getScaleX() * (GRID_SCALE * (x + 1) - camera.getXalign()));
						g.drawLine(x1, y1, x2, y1);
					}
					//down
					else if (neighbor.getLocation().getY() < 
					borderTile.getLocation().getY()) {
						int x1 = (int)(camera.getScaleX() * (GRID_SCALE * x - camera.getXalign()));
						int y1 = (int)(camera.getScaleY() * (GRID_SCALE * y - camera.getYalign()));
						int x2 = (int)(camera.getScaleX() * (GRID_SCALE * (x + 1) - camera.getXalign()));
						g.drawLine(x1, y1, x2, y1);
					}
					//right
					else if (neighbor.getLocation().getX() > 
					borderTile.getLocation().getX()) {
						int x1 = (int)(camera.getScaleX() * (GRID_SCALE * (x + 1) - camera.getXalign()));
						int y1 = (int)(camera.getScaleY() * (GRID_SCALE * y - camera.getYalign()));
						int y2 = (int)(camera.getScaleY() * (GRID_SCALE * (y + 1) - camera.getYalign()));
						g.drawLine(x1, y1, x1, y2);
					}
					//left
					else /*if (neighbor.getLocation().getX() < 
					borderTile.getLocation().getX())*/ {
						int x1 = (int)(camera.getScaleX() * (GRID_SCALE * x - camera.getXalign()));
						int y1 = (int)(camera.getScaleY() * (GRID_SCALE * y - camera.getYalign()));
						int y2 = (int)(camera.getScaleY() * (GRID_SCALE * (y + 1) - camera.getYalign()));
						g.drawLine(x1, y1, x1, y2);
					}
				}
			}
		}
		} catch (Exception e) {}
	}

	private void paintGridLines(Graphics g) {
		g.setColor(Color.BLACK);
		for (int y = 0; y < grid.length + 1; y++) {
			int x1 = (int)(camera.getScaleX() * (-camera.getXalign()));
			int y1 = (int)(camera.getScaleY() * (GRID_SCALE * y - camera.getYalign()));
			int x2 = (int)(camera.getScaleX() * (GRID_SCALE * grid[0].length - camera.getXalign())) - 1;
			g.drawLine(x1, y1, x2, y1);
		}
		for (int x = 0; x < grid[0].length + 1; x++) {
			int x1 = (int)(camera.getScaleX() * (GRID_SCALE * x - camera.getXalign()));
			int y1 = (int)(camera.getScaleY() * (-camera.getYalign()));
			int y2 = (int)(camera.getScaleY() * (GRID_SCALE * grid.length - camera.getYalign())) + 1;
			g.drawLine(x1, y1, x1, y2);
		}
	}
	/**
	 * Zooms the camera in or out depending scroll direction
	 * @author Matthew Zane
	 * @version 1.1
	 * @since 2017-09-15
	 */
	private class MouseWheelActionListener implements MouseWheelListener {

		/**
		 * Uses mouseWheelEvent to either zoom in or out
		 * @Override
		 */
		public void mouseWheelMoved(MouseWheelEvent e) {
			int notches = e.getWheelRotation();
			if (notches < 0) {
				camera.zoom(-zoomSpeed);
			}
			else {
				camera.zoom(zoomSpeed);
			}
		}
	}

	/**
	 * Moves the camera either up, down, left, or right
	 * @author Matthew Zane
	 * @version 1.1
	 * @since 2017-09-15
	 */
	private class KeyHandler implements KeyListener {

		/**
		 * Adds new direction to direction list if not already
		 * in it using the arrow keys or center the camera, if 
		 * the space bar is pressed
		 * @Override
		 */
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			switch (keyCode) { 
			case KeyEvent.VK_UP:
				if (!directions.contains(Direction.UP)) {
					directions.add(Direction.UP);
				}
				break;
			case KeyEvent.VK_DOWN:
				if (!directions.contains(Direction.DOWN)) {
					directions.add(Direction.DOWN);
				}
				break;
			case KeyEvent.VK_LEFT:
				if (!directions.contains(Direction.LEFT)) {
					directions.add(Direction.LEFT);
				}
				break;
			case KeyEvent.VK_RIGHT :
				if (!directions.contains(Direction.RIGHT)) {
					directions.add(Direction.RIGHT);
				}
				break;
			case KeyEvent.VK_SPACE:
				camera.center();
				break;
			}

			cameraMovementHandler.actionPerformed(null);
		}

		/**
		 * Removes direction from list once key has been released
		 * @Override
		 */
		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();
			//System.out.println("released");
			switch( keyCode ) { 
			case KeyEvent.VK_UP:
				directions.remove(Direction.UP);
				break;
			case KeyEvent.VK_DOWN:
				directions.remove(Direction.DOWN);
				break;
			case KeyEvent.VK_LEFT:
				directions.remove(Direction.LEFT);
				break;
			case KeyEvent.VK_RIGHT :
				directions.remove(Direction.RIGHT);
				break;
			}

			cameraMovementHandler.actionPerformed(null);
		}

		@Override
		public void keyTyped(KeyEvent e) {

		}
	}

	/**
	 * Moves the camera at a set speed using a Timer
	 * @author Matthew Zane
	 * @version 1.1
	 * @since 2017-09-15
	 */
	private class CameraMovementHandler implements ActionListener {

		/**
		 * Moves and updates Camera
		 */
		public void actionPerformed(ActionEvent arg0) {
			move();
			camera.updateCamera();
		}

		/**
		 * Moves camera depending on directions
		 */
		public void move() {
			//System.out.println("Moving");
			if (directions.contains(Direction.UP)) {
				moveUp();
			}
			if (directions.contains(Direction.DOWN)) {
				moveDown();
			}
			if (directions.contains(Direction.LEFT)) {
				moveLeft();
			}
			if (directions.contains(Direction.RIGHT)) {
				moveRight();
			}
		}

		/**
		 * Moves camera up
		 */
		public void moveUp()  {
			camera.moveUp();
		}

		/**
		 * Moves camera down
		 */
		public void moveDown() {
			camera.moveDown();
		}

		/**
		 * Moves camera left
		 */
		public void moveLeft() {
			camera.moveLeft();
		}

		/**
		 * Moves camera right
		 */
		public void moveRight() {
			camera.moveRight();
		}
	}

	/**
	 * Handles mouse clicks on panel
	 * @author Matthew Zane
	 * @version 1.1
	 * @since 2017-09-15
	 */
	public class MouseHandler implements MouseListener {

		/**
		 * Panel requests focus when clicked on
		 * @Override
		 */
		public void mouseClicked(MouseEvent e) {
			if (getMousePosition() != null) {
				requestFocus();
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}
	}
}

