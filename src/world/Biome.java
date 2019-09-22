package world;

import java.util.ArrayList;

public class Biome {
	private Tile.TileType biomeType;
	private ArrayList<Tile> tiles = new ArrayList<Tile>();
	
	private ArrayList<Tile> unconveredNeighbors = new ArrayList<Tile>();
	
	private World world;
	
	public Tile.TileType getType() { return biomeType; }
	public ArrayList<Tile> getTiles() { return tiles; }
	
	public ArrayList<Tile> getUncoveredNeighbors() { return unconveredNeighbors; }
	
	
	public Biome(Tile.TileType tileTypeIn, World worldIn) {
		biomeType = tileTypeIn;
		world = worldIn;
		findStartingLocation();
	}
	
	private void findStartingLocation() {
		boolean foundStartingLocation = false;
		while (!foundStartingLocation) {
			int x = (int) (Math.random() * (world.getGrid()[0].length - 1));
			int y = (int) (Math.random() * (world.getGrid().length - 1));
			foundStartingLocation = coverTile(world.getGrid()[x][y]);
		}
	}
	
	public boolean coverTile(Tile target) {
		if (target.isCovered()) {
			return false;
		}
		else {
			tiles.add(target);
			target.setType(biomeType);
			updateNeighbors(target);
			return true;
		}
	}
	
	private void updateNeighbors(Tile newTile) {
		if (unconveredNeighbors.size() != 0) {
			unconveredNeighbors.remove(newTile);
		}
		
		for (Tile temp : newTile.getNeighbors()) {
			if (!temp.isCovered() && !unconveredNeighbors.contains(temp)) {
				unconveredNeighbors.add(temp);
			}
		}
		
		//update other biomes uncovered Neighbors list
		ArrayList<Biome> otherBiomes = 
				(ArrayList<Biome>) ((ArrayList<Biome>) world.getBiomes()).clone();
		otherBiomes.remove(this);
		for (Biome temp : otherBiomes) {
			temp.getUncoveredNeighbors().remove(newTile);
		}
	}
	
	public void takeTurn() {
		if (unconveredNeighbors.size() != 0) {
			boolean coveredNewTile = false;
			while (!coveredNewTile) {
				coveredNewTile = coverTile(unconveredNeighbors.get(
						(int)(Math.random() * (unconveredNeighbors.size() - 1))));
			}
		}

	}
}
