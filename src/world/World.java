package world;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.net.Authenticator.RequestorType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import display.WorldPanel;
import entity.*;
import helper.Move;
import helper.RandomGenerator;
import world.Tile.TileType;

public class World extends Thread {
	/**
	 * Shortest possible time length in the world in days (s)
	 */
	public static final double TIME_INTERVAL = 1;

	private double time = 0;
	
	private Nature nature; 
	
	//square shaped. Inner arraylists are horizontal
	private Tile[][] grid;
	
	private List<Biome> biomes = new ArrayList<Biome>();
	private List<Civilization> civs = new ArrayList<Civilization>();

	public double getTime() { return time; }
	
	public Nature getNature() { return nature; }
	
	public Tile[][] getGrid() { return grid; }

	public List<Biome> getBiomes() { return biomes; }
	public List<Civilization> getCivilizations() { return civs; }

	public World(Dimension gridSize, int numCivs) {
		System.out.println("Instantiating Grid...");
		instantiateGrid(gridSize);

		System.out.println("Instantiating Tile Neighbors...");
		instantiateTileNeighbors(gridSize);		

		System.out.println("Intantiating Tile Types...");
		instantiateTileTypes();

		System.out.println("Instantiating Civilizations...");
		instantiateCivilizations(numCivs);

		System.out.println("Done!");
	}

	private void instantiateGrid(Dimension gridSize) {
		nature = new Nature(this);
		grid = new Tile[(int) gridSize.getWidth()][(int) gridSize.getHeight()];
		for (int y = 0; y < gridSize.getHeight(); y++) {
			for (int x = 0; x < gridSize.getWidth(); x++) {
				grid[x][y] = new Tile(new Point(x, y), nature);
				nature.getTerritory().add(grid[x][y]);
			}
		}
	}

	private void instantiateTileNeighbors(Dimension gridSize) {
		for (int y = 0; y < gridSize.getHeight(); y++) {
			for (int x = 0; x < gridSize.getWidth(); x++) {
				ArrayList<Tile> neighbors = grid[x][y].getNeighbors();
				try {
					neighbors.add(grid[x - 1][y]);
				} catch (ArrayIndexOutOfBoundsException e) {}
				try {
					neighbors.add(grid[x + 1][y]);
				} catch (ArrayIndexOutOfBoundsException e) {}
				try {
					neighbors.add(grid[x][y - 1]);
				} catch (ArrayIndexOutOfBoundsException e) {}
				try {
					neighbors.add(grid[x][y + 1]);
				} catch (ArrayIndexOutOfBoundsException e) {}
			}
		}
	}

	private void instantiateTileTypes() {
		TileType[] tileTypes = Tile.TileType.values();
		int numTiles = grid[0].length * grid.length;
		int numBiomes = (int) Math.round(Math.sqrt(numTiles) / 5);
		for (int i = 0; i < numBiomes; i++) {
			biomes.add(new Biome(
					tileTypes[(int) (Math.random() * (tileTypes.length))], this));
		}

		expandBiomes();
	}

	private void expandBiomes() {
		while(!isFullWithBiomes()) {
			runSingleBiomeTurn();
		}
	}

	private void runSingleBiomeTurn() {
		for (Biome temp : biomes) {
			temp.takeTurn();
		}
	}

	private boolean isFullWithBiomes() {
		for (int y = 0; y < grid.length; y++) {
			for (int x = 0; x < grid[0].length; x++) {
				if (!grid[x][y].isCovered()) {
					return false;
				}
			}
		}
		return true;
	}

	private void instantiateCivilizations(int numCivs) {
		for (int i = 0; i < numCivs; i++) {
			//TODO change how name and color are chosen
			Color color = RandomGenerator.randColor();
			color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 60);
			civs.add(new Civilization("" + i, color, this));
		}
	}

	public void run() {
		while(true) {
			runSingleTurn();
			time += TIME_INTERVAL;
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void runSingleTurn() {
		updateCivilizations();
		//System.out.println("\nTurn " + (int) time);
		//register civ moves
		ArrayList<Move> moves = new ArrayList<Move>();
		for (Civilization temp : civs) {
			
			Move move = temp.takeTurn();
			if (move != null) {
				moves.add(move);
			}
			
		}

		//TODO: give nature a better part
		/**Move move = nature.takeTurn();
		if (move != null) {
			moves.add(move);
		}*/

		resolveMoves(moves);
	}

	/**
	 * conflict when two or more civs try to settle the same tile.
	 * resolved by combat
	 * When two or more civs attack a tile, the attacker together fight the
	 * defender, then if the defender loses, the attacker fight eachother
	 * @param moves
	 */
	public void resolveMoves(ArrayList<Move> moves) {
		Map<Tile, List<Civilization>> map = new HashMap<Tile, List<Civilization>>();

		for (Move move : moves) {
			List<Civilization> civs = map.get(move.getTarget());
			if (civs == null) {
				civs = new ArrayList<Civilization>();
				map.put(move.getTarget(), civs);
			}
			civs.add(move.getCiv());
		}

		for (Tile key : map.keySet()) {
			resolveAttack(key, (ArrayList<Civilization>)map.get(key));
		}
	}

	public void resolveAttack(Tile target, ArrayList<Civilization> attackers) {
		ArrayList<Civilization> fighters = (ArrayList<Civilization>) attackers.clone();
		fighters.add(target.getOwner());
		Civilization winner = resolveCombat(target, fighters);

		if (winner != target.getOwner()) {
			//System.out.println("Civ " + winner + " captured " + target + " from " + target.getOwner());
			winner.acquireTile(target);
		}
		else {
			//System.out.println("Civ " + winner + " successfully defended " + target);
		}
	}

	/**
	 * Recursively resolves combat between 2 or more civs
	 * @param fightersIn
	 * @return
	 */
	private Civilization resolveCombat(Tile target, ArrayList<Civilization> fightersIn) {
		ArrayList<Civilization> fighters = (ArrayList<Civilization>) fightersIn.clone();
		//return if only 1 left
		if (fighters.size() == 1) {
			return fighters.get(0);
		}

		//else: next fighter becomes "defender"
		Civilization defender = fighters.get(0);
		int defenderStrength = defender.getStrength(target);

		int attackersStrength = 0;
		for (int i = 1; i < fighters.size(); i++) {
			attackersStrength += fighters.get(i).getStrength(target);
		}

		//attackers vs. defender
		int rand = (int)(Math.random() * (defenderStrength + attackersStrength) + 1);

		//case: attackers victorious, so remove defender from list
		if (rand > defenderStrength) {
			fighters.remove(defender);
			return resolveCombat(target, fighters);
		}
		//else: return defender as victorious
		return defender;
	}

	private void updateCivilizations() {
		List<Civilization> defeatedCivs = new ArrayList<Civilization>();
		for (Civilization civ : civs) {
			if (civ.getTerritory().size() == 0) {
				defeatedCivs.add(civ);
			}
			else {
				
			}
		}
		
		for (Civilization defeatedCiv : defeatedCivs) {
			civs.remove(defeatedCiv);
			System.out.println("\nTurn: " + (int) time + " - Civilization: " + 
			defeatedCiv.getName() + " has been defeated!");
		}
	}

	public Rectangle getBounds() {
		return new Rectangle(0, 0, WorldPanel.getGridScale() * grid[0].length, 
				WorldPanel.getGridScale() * grid.length);
	}
}
