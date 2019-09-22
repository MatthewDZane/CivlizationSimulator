package helper;

import entity.Civilization;
import world.Tile;

public class Move {
	private Civilization civ;
	private Tile target;
	
	
	public Civilization getCiv() { return civ; }
	public Tile getTarget() { return target; }
	
	public void setCiv(Civilization civ) { this.civ = civ; }
	public void setTarget(Tile target) { this.target = target; }
	
	public Move(Civilization civIn, Tile targetIn) {
		civ = civIn;
		target = targetIn;
	}
	
}
