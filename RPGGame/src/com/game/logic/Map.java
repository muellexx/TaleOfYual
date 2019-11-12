package com.game.logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import com.game.graphics.RenderHandler;
import com.game.utils.GameObject;
import com.game.utils.Rectangle;

public class Map {
	
	private Tiles tileSet;
	private int fillTileID = -1;
	private int fillTileGroup = -1;
	private int mapWidth, mapHeight;
	
	private ArrayList<MappedTile> mappedTiles = new ArrayList<MappedTile>();
	private Block[][] blocks;
	private ArrayList<Teleport> teleports = new ArrayList<Teleport>();
	private int blockWidth = 6;
	private int blockHeight = 6;
	private int blockPixelWidth;
	private int blockPixelHeight;
	private HashMap<Integer, String> comments = new HashMap<Integer, String>();
	private Rectangle collisionRectangle;
	
	private File mapFile;
	
	private int numLayers;
	
	public Map(File mapFile, Tiles tileSet) {
		this.mapFile = mapFile;
		this.tileSet = tileSet;
		blockPixelWidth = blockWidth * tileSet.getTileWidth();
		blockPixelHeight = blockHeight * tileSet.getTileHeight();
		Teleport teleport;
		collisionRectangle = new Rectangle(0,0,0,0);
		
		try {
			Scanner scanner = new Scanner(mapFile);
			int currentLine = 0;
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if(!line.startsWith("//")) {
					if(line.contains(":")) {
						String[] splitString = line.split(":");
						if(splitString[0].equalsIgnoreCase("fill")) {
							fillTileGroup = Integer.parseInt(splitString[1]);
							fillTileID = Integer.parseInt(splitString[2]);
							currentLine++;
							continue;
						}else if(splitString[0].equalsIgnoreCase("width")) {
							mapWidth = Integer.parseInt(splitString[1]);
							currentLine++;
							continue;
						}else if(splitString[0].equalsIgnoreCase("height")) {
							mapHeight = Integer.parseInt(splitString[1]);
							currentLine++;
							continue;
						}else if(splitString[0].equalsIgnoreCase("teleport")) { //Teleport:int,int,int,int:MapFileName:relocation(:TilesFile:spriteFile)
							String[] splitString2 = splitString[1].split(",");
							Rectangle rect = new Rectangle(tileSet.getXZoom()*Integer.parseInt(splitString2[0]),tileSet.getYZoom()*Integer.parseInt(splitString2[1]),
									tileSet.getXZoom()*Integer.parseInt(splitString2[2]),tileSet.getYZoom()*Integer.parseInt(splitString2[3]));
							if(Integer.parseInt(splitString[3])==10) {
								if(splitString.length >=9)
									teleport = new Teleport(rect, new File(splitString[2]), Integer.parseInt(splitString[3]), Integer.parseInt(splitString[4]),
											Integer.parseInt(splitString[5]), Integer.parseInt(splitString[6]), new File(splitString[7]), splitString[8]);
								else
									teleport = new Teleport(rect, new File(splitString[2]), Integer.parseInt(splitString[3]), Integer.parseInt(splitString[4]),
											Integer.parseInt(splitString[5]), Integer.parseInt(splitString[6]), null, null);
							} else if(splitString.length >=6)
								teleport = new Teleport(rect,new File(splitString[2]), Integer.parseInt(splitString[3]), new File(splitString[4]), splitString[5]);
							else
								teleport = new Teleport(rect,new File(splitString[2]), Integer.parseInt(splitString[3]), null, null);
							teleports.add(teleport);
							currentLine++;
							continue;
						}
					}
					
					String[] splitString = line.split(",");
					if(splitString.length >= 4) {
						MappedTile mappedTile = new MappedTile(Integer.parseInt(splitString[0]),
																Integer.parseInt(splitString[1]),
																Integer.parseInt(splitString[2]),
																Integer.parseInt(splitString[3]),
																Integer.parseInt(splitString[4]),
																Integer.parseInt(splitString[5]));
												
						if(numLayers <= mappedTile.layer)
							numLayers = mappedTile.layer + 1;
						if(mappedTile.x > - blockWidth && mappedTile.x < mapWidth + blockWidth &&
								mappedTile.y > - blockHeight && mappedTile.y < mapHeight + blockHeight)
							mappedTiles.add(mappedTile);
					}
				}else {
					comments.put(currentLine, line);
				}
				currentLine++;
			}
			
			scanner.close();
			
			int blockSizeX = mapWidth / blockWidth + 2;
			int blockSizeY = mapHeight / blockHeight + 2;
			blocks = new Block[blockSizeX][blockSizeY];
			
			for (int i = 0; i < mappedTiles.size(); i++) {
				MappedTile mappedTile = mappedTiles.get(i);
				int blockX = mappedTile.x/blockWidth + 1;
				int blockY = mappedTile.y/blockHeight + 1;
				assert(blockX >= 0 && blockX < blocks.length && blockY >= 0 && blockY < blocks[0].length);
				
				if(blocks[blockX][blockY] == null)
					blocks[blockX][blockY] = new Block();
				
				blocks[blockX][blockY].addTile(mappedTile);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public MappedTile getTile(int layer, int sublayer, int tileX, int tileY) {
		//int blockX = (tileX - blockStartX)/blockWidth;
		//int blockY = (tileY - blockStartY)/blockHeight;
		int blockX = tileX/blockWidth + 1;
		int blockY = tileY/blockHeight + 1;
		
		if(blockX < 0 || blockX >= blocks.length || blockY < 0 || blockY >= blocks[0].length)
			return null;
		
		Block block = blocks[blockX][blockY];
		
		if(block == null)
			return null;
		
		return block.getTile(layer, sublayer, tileX, tileY);
	}
	
	public boolean checkCollision(Rectangle rect, int layer) {
		if(layer>=numLayers) return false;
		int xZoom = tileSet.getXZoom();
		int yZoom = tileSet.getYZoom();
		int tileWidth = tileSet.getTileWidth() * xZoom;
		int tileHeight = tileSet.getTileHeight() * yZoom;
		
		int topLeftX = (rect.x - 64)/tileWidth;
		int topLeftY = (rect.y - 64)/tileHeight;
		int bottomRightX = (rect.x + rect.w + 64)/tileWidth;
		int bottomRightY = (rect.y + rect.h + 64)/tileWidth;
		boolean collided = false;
		
		collisionRectangle = new Rectangle(Integer.MAX_VALUE,Integer.MAX_VALUE,0,0);
		
		for(int x = topLeftX; x < bottomRightX; x++)
			for(int y = topLeftY; y < bottomRightY; y++) {
				for(int sublayer = 0; sublayer < 5; sublayer++) {
					MappedTile tile = getTile(layer, sublayer, x, y);
					boolean tileCollided = false;
					Rectangle tileCollisionRectangle = new Rectangle(0,0,0,0);

					if(tile != null) {
						int collisionType = tileSet.collisionType(tile.groupID, tile.id);
						if(collisionType == 0) {
							tileCollisionRectangle = new Rectangle(tile.x*tileWidth, tile.y*tileHeight, tileWidth, tileHeight);
							Rectangle tileRectangle = new Rectangle(tile.x*tileWidth, tile.y*tileHeight, tileWidth, tileHeight);
							if(tileCollisionRectangle.intersects(rect)) {
								collided = true;
								tileCollided = true;
							}
						} else if(collisionType == 1) {

						} else if(collisionType == 2) {

						} else if(collisionType == 3) {
							tileCollisionRectangle = new Rectangle(tile.x*tileWidth, tile.y*tileHeight + tileHeight - 8, tileWidth, 8);
							Rectangle adjustedRect = new Rectangle(rect.x, rect.y + rect.h, rect.w, 1);
							if(tileCollisionRectangle.intersects(adjustedRect)) {
								collided = true;
								tileCollided = true;
							}
						} else if(collisionType == 4) {

						} else if(collisionType == 10) {
							ArrayList<Rectangle> collisionRectangle = tileSet.collisionRectangle(tile.groupID, tile.id); //new ArrayList<Rectangle>();

							for (int i = 0; i<collisionRectangle.size(); i++) {
								Rectangle tileRectangle = new Rectangle(0,0,0,0);

								tileRectangle.x = collisionRectangle.get(i).x*xZoom + tile.x*tileWidth;
								tileRectangle.y = collisionRectangle.get(i).y*yZoom + tile.y*tileHeight;
								tileRectangle.w = collisionRectangle.get(i).w*xZoom;
								tileRectangle.h = collisionRectangle.get(i).h*yZoom;

								if(tileRectangle.intersects(rect)) {
									collided = true;
									tileCollided = true;
									tileCollisionRectangle.x = tileRectangle.x;
									tileCollisionRectangle.y = tileRectangle.y;
									tileCollisionRectangle.w = tileRectangle.w;
									tileCollisionRectangle.h = tileRectangle.h;
								}
							}

						}
						if(tileCollided) {

							if(tileCollisionRectangle.x < collisionRectangle.x)
								collisionRectangle.x = tileCollisionRectangle.x;
							if(tileCollisionRectangle.y < collisionRectangle.y)
								collisionRectangle.y = tileCollisionRectangle.y;
							if(tileCollisionRectangle.w + tileCollisionRectangle.x > collisionRectangle.w + collisionRectangle.x)
								collisionRectangle.w = tileCollisionRectangle.w + tileCollisionRectangle.x - collisionRectangle.x;
							if(tileCollisionRectangle.h + tileCollisionRectangle.y > collisionRectangle.h + collisionRectangle.y)
								collisionRectangle.h = tileCollisionRectangle.h + tileCollisionRectangle.y - collisionRectangle.y;


						}
					}
				}
			}
		return collided;
	}
	
	public int checkTeleportCollision(Rectangle rect) {
		
		for(int teleportID=0; teleportID<teleports.size(); teleportID++) {
			if(teleports.get(teleportID).rect.intersects(rect)) {
				return teleportID;
			}
		}
		
		return -1;
	}
	
	public void setTile(int layer, int tileX, int tileY, int tileGroup, int tileID) {
		if (layer >= numLayers)
			numLayers = layer + 1;
		int sublayer = 0;
		for(int i = 0; i < mappedTiles.size(); i++) {
			MappedTile mappedTile = mappedTiles.get(i);
			if(mappedTile.x == tileX && mappedTile.y == tileY && mappedTile.layer == layer) {
				/*mappedTile.id = tileID;
				mappedTile.groupID = tileGroup;
				return;*/
				sublayer++;
			}
		}
		
		MappedTile mappedTile = new MappedTile(layer, sublayer, tileGroup, tileID, tileX, tileY);
		mappedTiles.add(mappedTile);
		
		// Add to blocks
		//int blockX = (tileX - blockStartX)/blockWidth;
		//int blockY = (tileY - blockStartY)/blockHeight;
		int blockX = tileX/blockWidth + 1;
		int blockY = tileY/blockHeight + 1;
		if(blockX >= 0 && blockY >= 0 && blockX < blocks.length && blockY < blocks[0].length) {
			if(blocks[blockX][blockY] == null)
				blocks[blockX][blockY] = new Block();
			blocks[blockX][blockY].addTile(mappedTile);
		} else {
			System.out.println("Warning: Added Tile is outside of the blocks area in Map.java, setTile");
			/*int newMinX = blockStartX;
			int newMinY = blockStartY;
			int newLengthX = blocks.length;
			int newLengthY = blocks[0].length;
			
			if(blockX < 0) {
				int increaseAmount = blockX * -1;
				newMinX = blockStartX - blockWidth*increaseAmount;
				newLengthX = newLengthX + increaseAmount;
			} else if(blockX > blocks.length)
				newLengthX = blocks.length + blockX;
			
			if(blockY < 0) {
				int increaseAmount = blockY * -1;
				newMinY = blockStartY - blockHeight*increaseAmount;
				newLengthY = newLengthY + increaseAmount;
			} else if(blockY > blocks[0].length)
				newLengthY = blocks[0].length + blockY;
			
			
			Block[][] newBlocks = new Block[newLengthX][newLengthY];
			
			for (int x = 0; x < blocks.length; x++)
				for (int y = 0; y < blocks[0].length; y++)
					if (blocks[x][y] != null) {
						newBlocks[x + (blockStartX - newMinX)/blockWidth][y + (blockStartY - newMinY)/blockHeight] = blocks[x][y];
					}
			
			blocks = newBlocks;
			blockStartX = newMinX;
			blockStartY = newMinY;
			blockX = (tileX - blockStartX)/blockWidth;
			blockY = (tileY - blockStartY)/blockHeight;
			if(blocks[blockX][blockY] == null)
				blocks[blockX][blockY] = new Block();
			blocks[blockX][blockY].addTile(mappedTile);
			*/
		}
	}
	
	public void removeTile(int layer, int tileX, int tileY) {
		int sublayer = -1;
		int removeTile = -1;
		MappedTile removeBlockTile = null;
		int blockX = 0;
		int blockY = 0;
		for(int i = 0; i < mappedTiles.size(); i++) {
			MappedTile mappedTile = mappedTiles.get(i);
			if(mappedTile.layer == layer && mappedTile.x == tileX && mappedTile.y == tileY) {
				sublayer += 1;
				System.out.println("hi" + sublayer);
				removeTile = i;
				removeBlockTile = mappedTile;
				//mappedTiles.remove(i);
				
				// Remove from Block
				blockX = tileX/blockWidth + 1;
				blockY = tileY/blockHeight + 1;
				//assert(blockX >= 0 && blockY >= 0 && blockX < blocks.length && blockY < blocks[0].length);
				//blocks[blockX][blockY].removeTile(mappedTile);
			}
		}
		if(removeTile != -1) {
			mappedTiles.remove(removeTile);
			assert(blockX >= 0 && blockY >= 0 && blockX < blocks.length && blockY < blocks[0].length);
			blocks[blockX][blockY].removeTile(removeBlockTile);
		}
	}
	
	public void saveMap() {
		try {
			int currentLine = 0;
			if(mapFile.exists())
				mapFile.delete();
			mapFile.createNewFile();
			
			PrintWriter printWriter = new PrintWriter(mapFile);
			
			if(comments.containsKey(currentLine)) {
				printWriter.println(comments.get(currentLine));
				currentLine++;
			}
			
			if(fillTileID >= 0) {
				printWriter.println("Fill:" + fillTileGroup + ":" + fillTileID);
				currentLine++;
			}
			
			if(comments.containsKey(currentLine)) {
				printWriter.println(comments.get(currentLine));
				currentLine++;
			}
			
			printWriter.println("Width:" + mapWidth);
			currentLine++;
			printWriter.println("Height:" + mapHeight);
			currentLine++;
			if(comments.containsKey(currentLine)) {
				printWriter.println(comments.get(currentLine));
				currentLine++;
			}
			
			for(int i = 0; i < teleports.size(); i++) {
				Teleport teleport = teleports.get(i);
				if(teleport.relocation == 10) {
					if(teleport.tilesFile == null && teleport.spriteFile == null)
						printWriter.println("Teleport:" + teleport.rect.x/tileSet.getXZoom() + "," + teleport.rect.y/tileSet.getYZoom() + "," + teleport.rect.w/tileSet.getXZoom() + "," + 
								teleport.rect.h/tileSet.getYZoom() + ":" + teleport.mapFile + ":" + teleport.relocation + ":" + teleport.spawnX + ":" + teleport.spawnY + ":" + teleport.direction); 
					else
						printWriter.println("Teleport:" + teleport.rect.x/tileSet.getXZoom() + "," + teleport.rect.y/tileSet.getYZoom() + "," + teleport.rect.w/tileSet.getXZoom() + "," + 
								teleport.rect.h/tileSet.getYZoom() + ":" + teleport.mapFile + ":" + teleport.relocation + ":" + teleport.spawnX + ":" + teleport.spawnY + ":" + teleport.direction + ":" + teleport.tilesFile + ":" + teleport.spriteFile);
				}
				else if(teleport.tilesFile == null && teleport.spriteFile == null)
					printWriter.println("Teleport:" + teleport.rect.x/tileSet.getXZoom() + "," + teleport.rect.y/tileSet.getYZoom() + "," + teleport.rect.w/tileSet.getXZoom() + "," + 
							teleport.rect.h/tileSet.getYZoom() + ":" + teleport.mapFile + ":" + teleport.relocation);
				else
					printWriter.println("Teleport:" + teleport.rect.x/tileSet.getXZoom() + "," + teleport.rect.y/tileSet.getYZoom() + "," + teleport.rect.w/tileSet.getXZoom() + "," + 
							teleport.rect.h/tileSet.getYZoom() + ":" + teleport.mapFile + ":" + teleport.relocation + ":" + teleport.tilesFile + ":" + teleport.spriteFile);
				currentLine++;
			}
			
			if(comments.containsKey(currentLine)) {
				printWriter.println(comments.get(currentLine));
				currentLine++;
			}
			
			for(int i = 0; i < mappedTiles.size(); i++) {
				if(comments.containsKey(currentLine)) {
					printWriter.println(comments.get(currentLine));
					currentLine++;
				}
				MappedTile tile = mappedTiles.get(i);
				printWriter.println(tile.layer + "," + tile.sublayer + "," + tile.groupID + "," + tile.id + "," + tile.x + "," + tile.y);
				currentLine++;
			}
			
			printWriter.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void render(RenderHandler renderer, ArrayList<GameObject> objects) {
		renderFillTile(renderer);
		renderBackLayer(renderer,objects);
		renderMiddleLayer(renderer,objects);
		
		if(numLayers==0) {
			for (int i = 0; i < objects.size(); i++)
				objects.get(i).render(renderer);
		}
		
		for (int i = 0; i < objects.size(); i++)
			if(objects.get(i).getLayer() == Integer.MAX_VALUE)
				objects.get(i).render(renderer);
	}
	
	private void renderFillTile(RenderHandler renderer) {
		
		int xZoom = tileSet.getXZoom();
		int yZoom = tileSet.getYZoom();
		int tileWidth = tileSet.getTileWidth() * xZoom;
		int tileHeight = tileSet.getTileHeight() * yZoom;
		
		if(fillTileID >= 0 && fillTileGroup >= 0) {
			Rectangle camera = renderer.getCamera();
			
			for(int x = camera.x - tileWidth - (camera.x % tileWidth); x < camera.x + camera.w; x += tileWidth)
				for(int y = camera.y - tileHeight - (camera.y % tileHeight); y < camera.y + camera.h; y += tileHeight)
					tileSet.renderTile(fillTileGroup, fillTileID, renderer, x, y, xZoom, yZoom);
		}
	}
	
	public void renderBackLayer(RenderHandler renderer, ArrayList<GameObject> objects) {
		
		int xZoom = tileSet.getXZoom();
		int yZoom = tileSet.getYZoom();
		int tileWidth = tileSet.getTileWidth() * xZoom;
		int tileHeight = tileSet.getTileHeight() * yZoom;
		
		int topLeftX = renderer.getCamera().x;
		int topLeftY = renderer.getCamera().y;
		int bottomRightX = renderer.getCamera().x + renderer.getCamera().w + tileWidth*blockWidth+1;
		int bottomRightY = renderer.getCamera().y + renderer.getCamera().h + tileHeight*blockHeight+1;
		int leftBlockX = (topLeftX/tileWidth)/blockWidth + 1;
		for(int sublayer = 0; sublayer < 5; sublayer++) {
			int blockX = leftBlockX;
			int blockY = (topLeftY/tileHeight)/blockHeight + 1;
			int pixelX = topLeftX;
			int pixelY = topLeftY;
			while (pixelX < bottomRightX && pixelY < bottomRightY) {
				if(blockX >= 0 && blockY >= 0 && blockX < blocks.length && blockY < blocks[0].length)

					if(blocks[blockX][blockY] != null) {
						blocks[blockX][blockY].render(renderer, 0, sublayer, tileWidth, tileHeight, xZoom, yZoom);
					}

				blockX++;
				pixelX += blockPixelWidth;

				if(pixelX > bottomRightX) {
					pixelX = topLeftX;
					blockX = leftBlockX;
					blockY++;
					pixelY += blockPixelHeight;
					if(pixelY > bottomRightY)
						break;

				}
			}
		}
		for (int i = 0; i < objects.size(); i++) 
			objects.get(i).render(renderer);
	}
	
	public void renderMiddleLayer(RenderHandler renderer, ArrayList<GameObject> objects) {
		int xZoom = tileSet.getXZoom();
		int yZoom = tileSet.getYZoom();
		int tileWidth = tileSet.getTileWidth() * xZoom;
		int tileHeight = tileSet.getTileHeight() * yZoom;
		
		int topLeftX = renderer.getCamera().x;
		int topLeftY = renderer.getCamera().y;
		int bottomRightX = renderer.getCamera().x + renderer.getCamera().w + blockWidth;
		int bottomRightY = renderer.getCamera().y + renderer.getCamera().h + 2*blockHeight;
		
		int leftBlockX = (topLeftX/tileWidth)/blockWidth + 1;
		
		for (int sublayer = 0; sublayer < 5; sublayer++) {
			int layer = 1;
			int blockX = leftBlockX;
			int blockY = (topLeftY/tileHeight)/blockHeight + 1;
			int pixelX = topLeftX;
			int pixelY = topLeftY;
			
			while (pixelX < bottomRightX && pixelY < bottomRightY) {
				if(blockX >= 0 && blockY >= 0 && blockX < blocks.length && blockY < blocks[0].length)
				
					if(blocks[blockX][blockY] != null) {
						blocks[blockX][blockY].render(renderer, layer, sublayer, tileWidth, tileHeight, xZoom, yZoom);
					}
				
				blockX++;
				pixelX += blockPixelWidth;
				
				if(pixelX > bottomRightX) {
					pixelX = topLeftX;
					blockX = leftBlockX;
					blockY++;
					pixelY += blockPixelHeight;
					if(pixelY > bottomRightY)
						break;
						
				}
			}
			
			for (int i = 0; i < objects.size(); i++) {
				if (objects.get(i).getLayer() == sublayer+2)
					objects.get(i).render(renderer);
				else if(objects.get(i).getLayer() + 1 == sublayer+2) {
					Rectangle rect = objects.get(i).getRectangle();
					
					int tileBelowX = rect.x/tileWidth;
					int tileBelowX2 = (int) Math.floor((rect.x + rect.w/2*objects.get(i).getXZoom()*1.0)/tileWidth);
					int tileBelowX3 = (int) Math.floor((rect.x + rect.w*objects.get(i).getXZoom()*1.0)/tileWidth);
					
					int tileBelowY = (int) Math.floor((rect.y + rect.h*objects.get(i).getYZoom()*1.0)/tileHeight);
					
					if(getTile(layer, sublayer, tileBelowX, tileBelowY) == null &&
							getTile(layer, sublayer, tileBelowX2, tileBelowY) == null &&
							getTile(layer, sublayer, tileBelowX3, tileBelowY) == null)
						objects.get(i).render(renderer);
				}
			}
		}
	}
	
	public File getTeleportFile(int id) {
		return teleports.get(id).mapFile;
	}
	
	public Teleport getTeleport(int id) {
		return teleports.get(id);
	}
	
	public int getTeleportRelocation(int id) {
		return teleports.get(id).relocation;
	}
			
	
	//Block represents a 6x6 block of tiles
	@SuppressWarnings("unchecked")
	private class Block{
		public ArrayList<MappedTile>[][] mappedTilesByLayer;
		
		public Block() {
			mappedTilesByLayer = new ArrayList[5][5];
			for (int i = 0; i < mappedTilesByLayer.length; i++)
				for (int j = 0; j < mappedTilesByLayer[0].length; j++)
					mappedTilesByLayer[i][j] = new ArrayList<MappedTile>();
		}
		
		public void render(RenderHandler renderer, int layer, int sublayer, int tileWidth, int tileHeight, int xZoom, int yZoom) {
			if(mappedTilesByLayer[layer][sublayer] == null)
				return;
			if (mappedTilesByLayer.length > layer && mappedTilesByLayer[layer].length > sublayer) {
				ArrayList<MappedTile> mappedTiles = mappedTilesByLayer[layer][sublayer];
				for(int tileIndex = 0; tileIndex < mappedTiles.size(); tileIndex++) {
					MappedTile mappedTile = mappedTiles.get(tileIndex);
					tileSet.renderTile(mappedTile.groupID, mappedTile.id, renderer, mappedTile.x * tileWidth, mappedTile.y * tileHeight, tileSet.getXZoom(), tileSet.getYZoom());
				}
			}
		}
		
		public void addTile(MappedTile tile) {
			if(mappedTilesByLayer.length <= tile.layer) {
				ArrayList<MappedTile>[][] newTilesByLayer = new ArrayList[tile.layer + 1][tile.sublayer + 1];
				
				int i = 0;
				for (i = 0; i < mappedTilesByLayer.length; i++)
					newTilesByLayer[i] = mappedTilesByLayer[i];
				for (; i < newTilesByLayer.length; i++)
					newTilesByLayer[i][1] = new ArrayList<MappedTile>();
				
				mappedTilesByLayer = newTilesByLayer;
			}
			mappedTilesByLayer[tile.layer][tile.sublayer].add(tile);
		}
		
		public void removeTile(MappedTile tile) {
			mappedTilesByLayer[tile.layer][tile.sublayer].remove(tile);
		}
		
		public MappedTile getTile(int layer, int sublayer, int tileX, int tileY) {
			if(layer >= mappedTilesByLayer.length || sublayer >= mappedTilesByLayer[0].length)
				return null;
			if(mappedTilesByLayer[layer][sublayer] == null)
				return null;
			for(MappedTile tile : mappedTilesByLayer[layer][sublayer]) {
				if(tile.x == tileX && tile.y == tileY)
					return tile;
			}
			return null;
		}
	}
	
	//Tile ID in the TileSet and the Position of the tile in the map
	private class MappedTile {
		
		public int layer, sublayer, id, groupID, x, y;
		
		public MappedTile(int layer, int sublayer, int groupID, int id, int x, int y) {
			this.layer = layer;
			this.sublayer = sublayer;
			this.groupID = groupID;
			this.id = id;
			this.x = x;
			this.y = y;
		}
		
	}
	
	public class Teleport {
		public Rectangle rect;
		public File mapFile;
		public File tilesFile;
		public String spriteFile;
		public int relocation;
		public int spawnX, spawnY, direction;
		
		public Teleport(Rectangle rect, File file, int relocation, File tilesFile, String spriteFile) {
			this.rect = rect;
			this.mapFile = file;
			this.relocation = relocation;
			this.tilesFile = tilesFile;
			this.spriteFile = spriteFile;
			this.spawnX = 0;
		}
		public Teleport(Rectangle rect, File file, int relocation, int spawnX, int spawnY, int direction, File tilesFile, String spriteFile) {
			this.rect = rect;
			this.mapFile = file;
			this.relocation = relocation;
			this.tilesFile = tilesFile;
			this.spriteFile = spriteFile;
			this.spawnX = spawnX;
			this.spawnY = spawnY;
			this.direction = direction;
		}
	}
	
	public int getWidth() {
		return mapWidth*tileSet.getTileWidth()*tileSet.getXZoom();
	}
	
	public int getHeight() {
		return mapHeight*tileSet.getTileHeight()*tileSet.getYZoom();
	}
	
	public int getNumLayers() {
		return numLayers;
	}
	
	public Rectangle getCollisionRectangle() {
		return collisionRectangle;
	}
}
