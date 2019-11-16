package com.game.logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import com.game.graphics.RenderHandler;
import com.game.graphics.Sprite;
import com.game.graphics.SpriteSheet;
import com.game.utils.Rectangle;

public class Tiles {
	
	private SpriteSheet spriteSheet;
	private int tileWidth, tileHeight;
	private int xZoom, yZoom;
	private ArrayList<Tile>[] tilesList;// = new ArrayList<Tile>();
	private ArrayList<TileGroup> tileGroupsList = new ArrayList<TileGroup>();
	
	/*public Tiles(File tilesFile, SpriteSheet spriteSheet) {
		this.spriteSheet = spriteSheet;
		
		int groupNumber = -1;
		int tileNumber = 0;
		String groupName = "zero";
		
		Scanner scanner;
		try {
			scanner = new Scanner(tilesFile);
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if(!line.startsWith("//")) {
					if(line.startsWith("!TileWidth")) {
						String[] splitString = line.split("-");
						tileWidth = Integer.parseInt(splitString[1]);
					}else if(line.startsWith("!TileHeight")) {
						String[] splitString = line.split("-");
						tileHeight = Integer.parseInt(splitString[1]);
					}else if(line.startsWith("!xZoom")) {
						String[] splitString = line.split("-");
						xZoom = Integer.parseInt(splitString[1]);
					}else if(line.startsWith("!yZoom")) {
						String[] splitString = line.split("-");
						yZoom = Integer.parseInt(splitString[1]);
					}else if(line.startsWith("!Group")) {
						tileNumber = 0;
						groupNumber++;
						String[] splitString = line.split("-");
						groupName = splitString[1];
					} else {
						String[] splitString = line.split("-");
						String tileName = splitString[0];
						int spriteX = Integer.parseInt(splitString[1]);
						int spriteY = Integer.parseInt(splitString[2]);
						Tile tile = new Tile(tileName, spriteSheet.getSprite(spriteX, spriteY), groupNumber, tileNumber);
						
						if(splitString.length >= 4) {
							tile.collidable = true;
							tile.collisionType = Integer.parseInt(splitString[3]);
						}
							
						
						tilesList.add(tile);
						if (tileNumber == 0){
							TileGroup group = new TileGroup(groupName, groupNumber, spriteSheet.getSprite(spriteX,  spriteY));
							tileGroupsList.add(group);
						}
						tileNumber++;
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}*/
	
	public Tiles(SpriteSheet spriteSheet) {
		tilesList = new ArrayList[100];
		for (int i = 0; i < 100; i++)
			tilesList[i] = new ArrayList<Tile>();
		File tilesFile = spriteSheet.getTilesFile();
		this.spriteSheet = spriteSheet;
		
		int groupNumber = -1;
		int tileNumber = 0;
		int spriteID = 0;
		String groupName = "zero";
		
		Scanner scanner;
		try {
			scanner = new Scanner(tilesFile);
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if(!line.startsWith("//")) {
					if(line.startsWith("!TileWidth")) {
						String[] splitString = line.split("-");
						tileWidth = Integer.parseInt(splitString[1]);
					}else if(line.startsWith("!TileHeight")) {
						String[] splitString = line.split("-");
						tileHeight = Integer.parseInt(splitString[1]);
					}else if(line.startsWith("!xZoom")) {
						String[] splitString = line.split("-");
						xZoom = Integer.parseInt(splitString[1]);
					}else if(line.startsWith("!yZoom")) {
						String[] splitString = line.split("-");
						yZoom = Integer.parseInt(splitString[1]);
					}else if(line.startsWith("!Group")) {
						tileNumber = 0;
						groupNumber++;
						String[] splitString = line.split("-");
						groupName = splitString[1];
					} else {
						if(groupNumber < 0)
							groupNumber = 0;
						String[] splitString = line.split("-");
						String tileName = splitString[0];
						Tile tile = new Tile(tileName, spriteSheet.getSprite(spriteID), groupNumber, tileNumber);
						
						if(splitString.length >= 6) {
							tile.collidable = true;
							if(splitString.length < 9) {
								tile.collisionType = Integer.parseInt(splitString[5]);
							}else {
								tile.collisionType = 10;
								int collisionX = Integer.parseInt(splitString[5]);
								int collisionY = Integer.parseInt(splitString[6]);
								int collisionW = Integer.parseInt(splitString[7]);
								int collisionH = Integer.parseInt(splitString[8]);
								tile.collisionRectangle.add(new Rectangle(collisionX, collisionY, collisionW, collisionH));
							}
							if(splitString.length >=10) {
								tile.collisionType = 10;
								int collisionX = Integer.parseInt(splitString[9]);
								int collisionY = Integer.parseInt(splitString[10]);
								int collisionW = Integer.parseInt(splitString[11]);
								int collisionH = Integer.parseInt(splitString[12]);
								tile.collisionRectangle.add(new Rectangle(collisionX, collisionY, collisionW, collisionH));
							}
						}
						
						tilesList[groupNumber].add(tile);
						if (tileNumber == 0){ //) && groupNumber > 0) {
							TileGroup group = new TileGroup(groupName, groupNumber, spriteSheet.getSprite(spriteID));
							tileGroupsList.add(group);
						}
						tileNumber++;
						spriteID++;
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public void renderTile(int groupNumber, int tileID, RenderHandler renderer, int xPosition, int yPosition, int xZoom, int yZoom) {
		
		if(tileID >= 0 && tilesList[groupNumber].size() > tileID) {
			renderer.renderSprite(tilesList[groupNumber].get(tileID).sprite, xPosition, yPosition, xZoom, yZoom, false);
		}else
			System.out.println("TileID " + tileID + " is not within range " + tilesList[groupNumber].size() + ".");
	}
	
	public int size(int groupID) {
		return tilesList[groupID].size();
	}
	
	public int groupSize(int groupID) {
		int groupSize = 0;
		for (int i = 0; i < tilesList[groupID].size(); i++) {
			if (tilesList[groupID].get(i).groupNumber == groupID)
				groupSize++;				
		}
		return groupSize;
	}
	
	public int numberOfGroups() {
		return tileGroupsList.size();
	}
	
	public Sprite[] getSprites(int groupID) {
		Sprite[] sprites = new Sprite[size(groupID)];
		
		for(int i = 0; i < sprites.length; i++)
			sprites[i] = tilesList[groupID].get(i).sprite;
		
		return sprites;
	}
	
	/*public int[] getGroupIDs() {
		int[] groupIDs = new int[size()];
		
		for(int i = 0; i < groupIDs.length; i++)
			groupIDs[i] = tilesList.get(i).groupNumber;
		
		return groupIDs;
	}*/
	
	public Sprite[] getGroupSprites() {
		Sprite[] sprites = new Sprite[numberOfGroups()];
		
		for(int i = 0; i < sprites.length; i++)
			sprites[i] = tileGroupsList.get(i).sprite;
		
		return sprites;
	}
	
	public int collisionType(int groupID, int tileID) {
		if(tileID >= 0 && tilesList[groupID].size() > tileID) {
			return tilesList[groupID].get(tileID).collisionType;
		}else
			System.out.println("TileID " + tileID + " is not within range " + tilesList[groupID].size() + ".");
		return -1;
	}
	
	public ArrayList collisionRectangle(int groupID, int tileID) {
		if(tileID >= 0 && tilesList[groupID].size() > tileID) {
			return tilesList[groupID].get(tileID).collisionRectangle;
		}else
			System.out.println("TileID " + tileID + " is not within range " + tilesList[groupID].size() + ".");
		return null;
	}
	
	public int getTileWidth() {
		return tileWidth;
	}
	
	public int getTileHeight() {
		return tileHeight;
	}
	
	public int getXZoom() {
		return xZoom;
	}
	
	public int getYZoom() {
		return yZoom;
	}
	
	class Tile{
		public String tileName;
		public Sprite sprite;
		public int groupNumber;
		public int tileNumber;
		public boolean collidable = false;
		public int collisionType = -1;
		//public Rectangle collisionRectangle;
		public ArrayList<Rectangle> collisionRectangle = new ArrayList<Rectangle>();
		
		public Tile(String tileName, Sprite sprite, int groupNumber, int tileNumber) {
			this.tileName = tileName;
			this.sprite = sprite;
			this.groupNumber = groupNumber;
			this.tileNumber = tileNumber;
		}
	}
	
	class TileGroup{
		public String groupName;
		public int groupNumber;
		public Sprite sprite;
		
		public TileGroup (String groupName, int groupNumber, Sprite sprite) {
			this.groupName = groupName;
			this.groupNumber = groupNumber;
			this.sprite = sprite;
		}
	}
	
}
