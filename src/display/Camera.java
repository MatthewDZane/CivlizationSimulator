package display;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import world.World;

/**
 * Represents the position of the world that the user can see
 * from along with the variables needed to translate and scale
 * the view into the panel. View is always a square portion of
 * World.
 * @author Matthew Zane
 * @version 1.1
 * @since 2017-09-15
 */
public class Camera {

	private World world;
	private WorldPanel panel;

	//Variable used to fit view into panel
	private double scaleX = 1;
	private double scaleY = 1;
	private double xalign = 0;
	private double yalign = 0;

	private Rectangle cameraPosition = new Rectangle();
	
	//Activates the CameraInitialCenterListener every 0.1 sec
	private Timer centerTimer = new Timer(100, new CameraInitialCenterListener());;

	public World getWorld() { return world; }
	public void setWorld(World worldIn) { world = worldIn; }
	public WorldPanel getPanel() { return panel; }
	public void setPanel(WorldPanel panelIn) { panel = panelIn; }
	
	public double getScaleX() { return scaleX; }
	public void setScaleX(double scaleXIn) { scaleX = scaleXIn; }
	public double getScaleY() { return scaleY; }
	public void setScaleY(double scaleYIn) { scaleY = scaleYIn; }
	public double getXalign() { return xalign; }
	public void setXalign(double xalignIn) { xalign = xalignIn;	}
	public double getYalign() { return yalign; }
	public void setYalign(double yalignIn) { yalign = yalignIn;	}

	/**
	 * Creates a camera and uses the World and WorldPanel to center
	 * the camera
	 * @param worldIn
	 * @param panelIn
	 */
	public Camera(World worldIn, WorldPanel panelIn) {
		world = worldIn;
		panel = panelIn;
		//centerTimer.start();
	}

	/**
	 * Centers camera around relevant graphics
	 */
	public void center() {
		//actualPanelWidth = panel.getWidth();
		//actualPanelHeight = panel.getHeight();

		cameraPosition = world.getBounds();

		updateCamera();
	}
	
	/**
	 * Zooms camera in or out
	 * @param zoomPercent - percent change in view size - 
	 * negative is a zoom in and positive is a zoom out
	 */
	public void zoom(double zoomPercent) {
		double percentageChange = (100 + zoomPercent) / 100;
		double newWidth = cameraPosition.width * percentageChange;
		double newHeight = cameraPosition.height * percentageChange;
		double newXPos = cameraPosition.x - (newWidth - cameraPosition.width) / 2;
		double newYPos = cameraPosition.y - (newHeight - cameraPosition.height) / 2;
		Rectangle newPosition = new Rectangle(
				(int)(Math.round(newXPos)), (int)(Math.round(newYPos)), 
				(int)(Math.round(newWidth)), (int)(Math.round(newHeight)));
		cameraPosition = newPosition;
		updateCamera();
	}

	/**
	 * Moves cameraPosition up by 1m
	 */
	public void moveUp() {
		cameraPosition.translate(0, (int)(-4.5 / scaleY));
	}

	/**
	 * Moves cameraPosition down by 1m
	 */
	public void moveDown() {
		cameraPosition.translate(0, (int)(4.5 / scaleY));
	}

	/**
	 * Moves cameraPosition left by 1m
	 */
	public void moveLeft() {
		cameraPosition.translate((int)(-4.5 / scaleX), 0);
	}

	/**
	 * Moves cameraPosition right by 1m
	 */
	public void moveRight() {
		cameraPosition.translate((int)(4.5 / scaleX), 0);
	}
	
	/**
	 * Uses cameraPosition to update scaling and translating variables.
	 * Note: Not sure why there is a width vs. height comparison.
	 */
	public void updateCamera() {
		scaleX = panel.getWidth() / (double) cameraPosition.width;
		scaleY = panel.getHeight() / (double) cameraPosition.height;
		yalign = cameraPosition.y;
		xalign = cameraPosition.x;
	}
	
	/**
	 * ActionListener used to attempt to center the camera after
	 * initialization
	 * @author Matthew Zane
	 * @version 1.1
	 * @since 2017-09-15
	 */
	private class CameraInitialCenterListener implements ActionListener {

		/**
		 * Checks if panel has been created yet, i.e panel width and
		 * height are greater than 0. Stops the timer once centering
		 * has been successful.
		 * @Override
		 */
		public void actionPerformed(ActionEvent e) {
			if (panel.getHeight() > 0 && panel.getWidth() > 0) {
				center();
			}
			centerTimer.stop();
		}
		
	}
}
