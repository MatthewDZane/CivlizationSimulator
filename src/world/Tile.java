package world;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import entity.Civilization;
import entity.Nature;

public class Tile implements Cloneable {
	public enum TileType {
		PLAINS(1, "Plains", new Color(102, 206, 112)), 
		DESERT(0, "Desert", new Color(239, 215, 138));
		
		private int value;
		
		private String description;
		private Color color;
		
		public int getValue() { return value; }
		public String getDescription() { return description; }
		public Color getColor() { return color; }
		
		private TileType(int valueIn, String descriptionIn, Color colorIn) {
			value = valueIn;
			description = descriptionIn;
			color = colorIn;
		}
	}
	
	private TileType tileType;
	private Point location;
	
	private Civilization owner;
	private ArrayList<Tile> neighbors = new ArrayList<Tile>();
	
	public TileType getType() { return tileType; }
	public Point getLocation() { return location; }
	
	public Civilization getOwner() { return owner; }
	public ArrayList<Tile> getNeighbors() { return neighbors; }
	
	public void setType(TileType tileTypeIn) { tileType = tileTypeIn; }
	
	public void setOwner(Civilization ownerIn) { owner = ownerIn; }
	
	public Tile(Point locationIn, Civilization ownerIn) {
		location = locationIn;
		owner = ownerIn;
	}
	
	public boolean isOwned() { return !(owner instanceof Nature); }
	public boolean isCovered() { return tileType != null; }
	
	public boolean isNeighbor(Tile neighbor) {
		for (Tile temp : neighbors) {
			if (temp == neighbor) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasOpenNeighbors() {
		for (Tile temp : neighbors) {
			if (!temp.isOwned()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isBorderTile() {
		for (Tile neighbor : neighbors) {
			if (neighbor.getOwner() != owner) {
				return true;
			}
		}
		return false;
	}
	
	public Tile clone() throws CloneNotSupportedException {
		return (Tile) super.clone();
	}
	
	public String toString() {
		return "(" + (int)location.getX() + ", " + (int)location.getY() + ")";
	}
}
