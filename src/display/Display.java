package display;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import world.World;

/**
 * Main JFrame from which graphics will be displayed. <br>
 * <br>
 * How to use:<br>
 * 	Display d = new Display(world);<br>
 * 	d.init();
 * @author Matthew Zane
 * @version 1.1
 * @since 2017-09-15
 */
public class Display extends JFrame {
	
	private World world;

	public World getWorld() { return world; }

	public void setWorld(World worldIn) { world = worldIn; }

	public Display() {
		super();
	}

	public Display(World worldIn) {
		super();
		world = worldIn;
	}

	public void init() throws Exception {
		//Display is utilizing the MigLayout
		setLayout(new MigLayout());

		//Panel from which the world will be displayed
		if (world == null) {
			throw new Exception("World was not instantiated");
		}
		
		WorldPanel panel = new WorldPanel(world);
		ClockLabel clockLabel = new ClockLabel(world);
		
		panel.createCamera();
		clockLabel.setBorder(BorderFactory.createLineBorder(Color.black));
		
		//WorldPanel centered in JFrame with a height and width
		//equal to a certain percent of the Display
		add(panel, "w " + WorldPanel.PCT_OF_PARENT + "%, h " + 
				WorldPanel.PCT_OF_PARENT + "%, align center");

		//Border Panels placed around World Panel
		//May have future use
		
		JPanel northPanel = new JPanel();
		JPanel southPanel = new JPanel();
		JPanel westPanel = new JPanel();
		JPanel eastPanel = new JPanel();
		
		add(northPanel, "north, h " + (100 - WorldPanel.PCT_OF_PARENT) / 2 + "%");
		add(southPanel, "south, h " + (100 - WorldPanel.PCT_OF_PARENT) / 2 + "%");
		add(westPanel, "west, w " + (100 - WorldPanel.PCT_OF_PARENT) / 2 + "%");
		add(eastPanel, "east, w " + (100 - WorldPanel.PCT_OF_PARENT) / 2 + "%");
		
		northPanel.setLayout(new MigLayout());
		northPanel.add(clockLabel, "h " + 100 + "%, w " + 25 + "%");
		
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1000, 1000);

		panel.requestFocus();
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
