package entity;

import helper.Move;
import world.Tile;
import world.World;

public class Nature extends Civilization{

	public Nature(World worldIn) {
		super("Nature", null, worldIn);
	}

	
	protected void findStartingLocation() {}
	
	@Override
	public Move takeTurn() {
		return null;
	}
	
	public void update() { }
	
	public int getStrength(Tile target) {
		return Integer.MIN_VALUE;
	}
}
