package entity;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import helper.Move;
import world.Tile;
import world.World;

public class Civilization {
	private String name;
	private Color color;

	private World world;

	public String getName() { return name; }
	public Color getColor() { return color; }

	protected Set<Tile> territory = new HashSet<Tile>();
	private Set<Tile> borderTiles = new HashSet<Tile>();
	private Set<Tile> neighboringTiles = new HashSet<Tile>();

	private Tile capital;

	public Set<Tile> getTerritory() { return territory; }
	public Set<Tile> getBorderTiles() { return borderTiles; }
	public Set<Tile> neighboringTiles() { return neighboringTiles; }

	public Tile getCapital() { return capital; }
	public void setCapital(Tile newCapital) { capital = newCapital; }

	public Civilization(String nameIn, Color colorIn, World worldIn) {
		name = nameIn;
		color = colorIn;
		world = worldIn;
		findStartingLocation();
	}

	protected void findStartingLocation() {
		boolean foundStartingLocation = false;
		while (!foundStartingLocation) {
			int x = (int) (Math.random() * (world.getGrid()[0].length - 1));
			int y = (int) (Math.random() * (world.getGrid().length - 1));
			foundStartingLocation = settleTile(world.getGrid()[x][y]);

			if (foundStartingLocation) {
				capital = world.getGrid()[x][y];
			}
		}
	}

	/**
	 * Every expansion into another space is considered an attack
	 * @return
	 */
	public Move takeTurn() {
		Move move = null;

		while(move == null) {
			int rand = (int)(Math.random() * 100);
			if (rand < 50) {
				move = expandBySettling();
			}
			else {
				move = expandByWar();
			}
		}
		
		return move;
	}

	private Move expandBySettling() {
		ArrayList<Tile> unOwnedNeighboringTiles = getUnownedNeighboringTiles();

		if (unOwnedNeighboringTiles.size() != 0) {
			Move move = new Move(this, findMostValueableTile(unOwnedNeighboringTiles));
			return move;
		}


		return null;
	}

	private Move expandByWar() {
		ArrayList<Tile> ownedNeighboringTiles = getOwnedNeighboringTiles();

		if (ownedNeighboringTiles.size() != 0) {
			Move move = new Move(this, findMostValueableTile(ownedNeighboringTiles));
			return move;
		}
		return null;
	}

	public boolean settleTile(Tile target) {
		if (target.isOwned()) {
			return false;
		}
		else {
			acquireTile(target);
			return true;
		}
	}

	public void acquireTile(Tile target) {
		territory.add(target);
		target.getOwner().concedeTile(target);
		target.setOwner(this);
		updateBorders(target);
	}

	public void concedeTile(Tile target) {
		target.setOwner(world.getNature());
		territory.remove(target);
		updateBorders(target);

		if (target == capital) {
			moveNewCapital();
		}
	}

	public void moveNewCapital() {
		if (territory.size() != 0) {
			int rand = (int) Math.random() * territory.size();
			int i = 0; 
			for (Tile tile : territory) {
				if (i == rand) {
					capital = tile;
					return;
				}
				i++;
			}
		}
	}

	private Tile findMostValueableTile(ArrayList<Tile> tileList) {
		int greatestValue = Integer.MIN_VALUE;
		ArrayList<Tile> bestTiles = new ArrayList<Tile>();

		for (Tile currentTile : tileList) {
			int currentValue = calculateTileValue(currentTile);
			if (currentValue > greatestValue) {
				greatestValue = currentValue;
				bestTiles = new ArrayList<Tile>();
				bestTiles.add(currentTile);
			}
			else if (currentValue == greatestValue) {
				bestTiles.add(currentTile);
			}
		}

		return bestTiles.get((int)(Math.random() * bestTiles.size()));
	}

	/**
	 * Tile value = half of the sum of type value of itself and immediate neighbors
	 * + bonus from neighbors already owned (.5 - a greater bonus causes expansion 
	 * to become more square) - square root distance from capital - the cost of 
	 * declaring a new war. 
	 * TODO (- threat value from other civilization if owned)
	 * @param tile
	 * @return
	 */
	private int calculateTileValue(Tile tile) {
		double totalValue = tile.getType().getValue();

		for (Tile neighbor : tile.getNeighbors()) {
			totalValue += neighbor.getType().getValue();
			if (neighbor.getOwner() == this) {
				totalValue += .5;
			}
		}
		totalValue /= 2;


		totalValue -= (int) Math.sqrt(calculateTileDistanceFromCapital(tile));

		return (int) totalValue;
	}

	/**
	 * Uses D = [(x1 - x2)^2 + (y1 - y2)^2]^(1/2)
	 * @param tile
	 * @return
	 */
	private double calculateTileDistanceFromCapital(Tile tile) {
		if (capital == null) {
			return 0;
		}
		return Math.sqrt(Math.pow(capital.getLocation().getX() -
				tile.getLocation().getX(), 2) + Math.pow(capital.getLocation().getY() -
						tile.getLocation().getY(), 2));
	}


	public void updateBorders(Tile target) {
		//case: target was captured
		if (territory.contains(target)) {
			ArrayList<Tile> tileToDelete = new ArrayList<Tile>();
			for (Tile tile : target.getNeighbors()) {
				if (!tile.isBorderTile()) {
					tileToDelete.add(tile);
				}

				if (!territory.contains(tile)) {
					neighboringTiles.add(tile);
				}
			}

			for (Tile tile : tileToDelete) {
				borderTiles.remove(tile);
			}

			if (target.isBorderTile()) {
				borderTiles.add(target);
			}

			neighboringTiles.remove(target);
		}
		//case: target was lossed
		else {
			for (Tile tile : target.getNeighbors()) {
				if (territory.contains(tile) && tile.isBorderTile()) {
					borderTiles.add(tile);
				}

				if (!tileIsNeighboring(tile)) {
					neighboringTiles.remove(tile);
				}
			}

			borderTiles.remove(target);

			if (tileIsNeighboring(target)) {
				neighboringTiles.add(target);
			}
		}
	}

	/**
	 * Returns a list of tiles neighboring the civilization that
	 * are currently unowned by any other civilization
	 * @return
	 */
	public ArrayList<Tile> getUnownedNeighboringTiles() {
		ArrayList<Tile> unownedNeighboringTiles = new ArrayList<Tile>();

		for (Tile neighboringTile : neighboringTiles) {
			if (!neighboringTile.isOwned()) {
				unownedNeighboringTiles.add(neighboringTile);
			}
		}

		return unownedNeighboringTiles;
	}

	/**
	 * Returns a list of tiles neighboring the civilization that
	 * are currently owned by any other civilization
	 * @return
	 */
	public ArrayList<Tile> getOwnedNeighboringTiles() {
		ArrayList<Tile> ownedNeighboringTiles = new ArrayList<Tile>();

		for (Tile neighboringTile : neighboringTiles) {
			if (neighboringTile.isOwned()) {
				ownedNeighboringTiles.add(neighboringTile);
			}
		}

		return ownedNeighboringTiles;
	}

	/**
	 * Strength depends on the tile that is being fought for.
	 * Strength = size of civ - square root distance from capital
	 * %10 defensive bonus if civ owns square
	 * @param target
	 * @return
	 */
	public int getStrength(Tile target) {
		double strength = (territory.size() - 
				Math.sqrt(calculateTileDistanceFromCapital(target)));
		if (territory.contains(target)) {
			strength += 1.1 * strength;
		}
		return (int) strength;
	}

	public boolean tileIsNeighboring(Tile tile) {
		for (Tile neighbor : tile.getNeighbors()) {
			if (neighbor.getOwner() == this) {
				return true;
			}
		}
		return false;
	}
	
	
	public String toString() {
		return name;
	}
}
