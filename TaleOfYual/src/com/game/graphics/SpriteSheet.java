package com.game.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SpriteSheet {
	
	private int[] pixels;
	private BufferedImage image;
	public final int SIZEX;
	public final int SIZEY;
	private Sprite[] loadedSprites = null;
	private boolean spritesLoaded = false;
	private File tilesFile;
	
	private int spriteSizeX;
	
	public SpriteSheet(BufferedImage sheetImage) {
		image = sheetImage;
		SIZEX = sheetImage.getWidth();
		SIZEY = sheetImage.getHeight();
		
		pixels = new int[SIZEX*SIZEY];
		pixels = sheetImage.getRGB(0, 0, SIZEX, SIZEY, pixels, 0, SIZEX);
	}
	
	public void loadSprites(int spriteSizeX, int spriteSizeY) {
		this.spriteSizeX = spriteSizeX;
		loadedSprites = new Sprite[(SIZEX / spriteSizeX) * (SIZEY / spriteSizeY)];
		
		int spriteID = 0;
		for(int y = 0; y < SIZEY; y += spriteSizeY) {
			for(int x = 0; x < SIZEX; x += spriteSizeX) {
				loadedSprites[spriteID] = new Sprite(this, x, y, spriteSizeX, spriteSizeY);
				spriteID++;
			}
		}
		
		spritesLoaded = true;
	}
	
	public void loadSprites(File tilesFile) {
		this.tilesFile = tilesFile;
		Scanner scanner;
		int numOfTiles = 0;
		int tileWidth = 0, tileHeight = 0;
		int x,y,w,h;
		
		try {
			scanner = new Scanner(tilesFile);
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if(!line.startsWith("//")&&!line.startsWith("!"))
					numOfTiles++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		loadedSprites = new Sprite[numOfTiles];
		int spriteID = 0;
		
		try {
			scanner = new Scanner(tilesFile);
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if(line.startsWith("!TileWidth")) {
					String[] splitString = line.split("-");
					tileWidth = Integer.parseInt(splitString[1]);
				}else if(line.startsWith("!TileHeight")) {
					String[] splitString = line.split("-");
					tileHeight = Integer.parseInt(splitString[1]);
				}else if(!line.startsWith("//")&&!line.startsWith("!")) {
					String[] splitString = line.split("-");
					if(splitString[1].startsWith("t")) {
						String[] splitString2 = splitString[1].split("t");
						x = tileWidth * Integer.parseInt(splitString2[1]);
					} else
					x = Integer.parseInt(splitString[1]);
					if(splitString[2].startsWith("t")) {
						String[] splitString2 = splitString[2].split("t");
						y = tileHeight * Integer.parseInt(splitString2[1]);
					} else
					y = Integer.parseInt(splitString[2]);
					if(splitString[3].startsWith("t")) {
						String[] splitString2 = splitString[3].split("t");
						w = tileWidth * Integer.parseInt(splitString2[1]);
					} else
					w = Integer.parseInt(splitString[3]);
					if(splitString[4].startsWith("t")) {
						String[] splitString2 = splitString[4].split("t");
						h = tileHeight * Integer.parseInt(splitString2[1]);
					} else
					h = Integer.parseInt(splitString[4]);
					loadedSprites[spriteID] = new Sprite(this, x, y, w, h);
					spriteID++;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
				
		spritesLoaded = true;
	}
	
	public Sprite getSprite(int x, int y) {
		if(spritesLoaded) {
			int spriteID = x + y * (SIZEX / spriteSizeX);
			if(spriteID < loadedSprites.length)
				return loadedSprites[spriteID];
			else
				System.out.println("SpriteID of " + spriteID + " is out of the range with a length of " + loadedSprites.length + ".");
		}else {
			System.out.println("SprideSheet could not get a sprite with no loaded sprites.");
		}
		return null;
	}
	
	public Sprite getSprite(int spriteID) {
		if(spritesLoaded) {
			if(spriteID < loadedSprites.length)
				return loadedSprites[spriteID];
			else
				System.out.println("SpriteID of " + spriteID + " is out of the range with a length of " + loadedSprites.length + ".");
		}else {
			System.out.println("SprideSheet could not get a sprite with no loaded sprites.");
		}
		return null;
	}
	
	public Sprite[] getLoadedSprites() {
		return loadedSprites;
	}
	
	public int[] getPixels() {
		return pixels;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public File getTilesFile() {
		return tilesFile;
	}
	
}
