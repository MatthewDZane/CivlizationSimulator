package display;

import java.awt.Graphics;
import javax.swing.JLabel;

import helper.Utility;
import world.World;

public class ClockLabel extends JLabel {
	private static final long serialVersionUID = 1L;
	
	private World world;

	public ClockLabel(World worldIn) {
		super();
		world = worldIn;
		setHorizontalAlignment(JLabel.CENTER);
	}

	public void paint(Graphics g) {
		super.paint(g);
		double time = world.getTime();
		if (time > 365.25) {
			time /= 365.2422;
			setText("Time:   " + Utility.getEngineeringNotation(time, 3) + "Y");
		}
		else {
			setText("Time:   " + Utility.getEngineeringNotation(time, 3) + "d");
		}
	}
}
