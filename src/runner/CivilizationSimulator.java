package runner;

import java.awt.Dimension;
import java.util.List;

import display.Display;
import helper.FileParser;
import helper.Timer;
import world.World;

public class CivilizationSimulator {
	private String pathname;

	public String getPathname() { return pathname; }

	public void setPathname(String pathnameIn) { pathname = pathnameIn; }

	public static void main(String [] args) {
		//parse command line arguments
		//for now just accept one parameter: config file

		//instantiate gravity simulator
		CivilizationSimulator gs = new CivilizationSimulator();
		//gs.setPathname(args[0]);

		//configure gravity simulator

		//call run()
		gs.run();
	}

	/**
	 * Requires pathname to be set.
	 */
	public void run() {
		try {
			//TODO change input for world
			World world = new World(new Dimension(1000, 1000), 5);
			Display display = new Display(world);
			display.init();

			boolean isDone = false;

			double frameCap = 1.0 / 60.0;

			double frameTime = 0;
			int frames = 0;

			double time = Timer.getTime();
			double unprocessed = 0;
			while (!isDone) {
				boolean canRender = false;

				double time2 = Timer.getTime();
				double passed = time2 - time;
				unprocessed += passed;
				frameTime += passed;

				time = time2;

				while (unprocessed >= frameCap) {
					unprocessed -= frameCap;
					canRender = true;


					if (frameTime >= 1.0) {
						frameTime = 0;
						//System.out.println("FPS: " + frames);
						frames = 0;
					}
				}
				if (canRender) {
					display.repaint();
					frames++;
				}

			}
		} catch(Exception e) {
			System.out.println("There was an error: " + e.getMessage());
		}
	}
}

