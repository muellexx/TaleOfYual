package com.game.logic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import com.game.Game;
import com.game.Game.STATE;
import com.game.graphics.AnimatedSprite;
import com.game.graphics.RenderHandler;
import com.game.graphics.Sprite;
import com.game.graphics.SpriteSheet;
import com.game.input.KeyBoardListener;
import com.game.logic.Map.Teleport;
import com.game.utils.Enemy;
import com.game.utils.GameObject;
import com.game.utils.Rectangle;

public class Player implements GameObject {
	
	private Rectangle playerRectangle;
	private Rectangle playerRectangleNew = new Rectangle();
	private Rectangle collisionRectangle;
	private Rectangle collisionRectangleBefore;
	private int maxSpeed = 3;
	private int xZoom, yZoom;
	private int spriteNum;
	private Game game;
	private SpriteSheet playerSheet;
	private int spriteWidth, spriteHeight;
	
	//0 = Right, 1 = Left, 2 = Up, 3 = Down
	private int direction = 0;
	private int directionNew = 0;
	private int layer = 1;
	private Sprite sprite;
	private AnimatedSprite animatedSprite = null;
	private int hitCounter = 0;
	private int[] speed = new int[2];
	private int[] hitSpeed = new int[2];
	private boolean didMove;
	private boolean hitBounce;
	
	private HUD hud;
	
	public Player(File playerFile, int xSpawn, int ySpawn, Game game) {
		
		this.game = game;
		readInput(playerFile);
		collisionRectangleBefore = new Rectangle(collisionRectangle.x, collisionRectangle.y, collisionRectangle.w, collisionRectangle.h);
		
		updateDirection();
		playerRectangle = new Rectangle(xSpawn, ySpawn, spriteWidth, spriteHeight);
		//playerRectangle.generateGraphics(3, 0xFF00FF90);
		hitCounter = 0;
		speed[0]=0;
		speed[1]=0;
		
		hud = new HUD(3,game);
		hud.fill();
		hud.setCurrentHealth(4);
	}
	
	public void Spawn(int spawnX, int spawnY) {
		playerRectangleNew.x = spawnX;
		playerRectangleNew.y = spawnY;
		if(game.getRenderer().getFade() != 1 && game.getRenderer().getFade() != 2)
			updateSpawn();
	}
	
	public void updateSpawn() {
		playerRectangle.x = playerRectangleNew.x;
		playerRectangle.y = playerRectangleNew.y;
		updateDirection();
		updateCamera(game.getMap(),game.getRenderer().getCamera());
	}
	
	public void Spawn(int spawnX, int spawnY, int direction) {
		Spawn(spawnX, spawnY);
		this.direction = direction;
		if(game.getRenderer().getFade() != 1 && game.getRenderer().getFade() != 2)
			updateDirection();
	}
	
	private void updateDirection() {
		if(animatedSprite != null){
			animatedSprite.setAnimationRange(direction * spriteNum, direction * spriteNum + spriteNum-1);
		}
	}

	public void render(RenderHandler renderer) {
		if(animatedSprite != null)
			renderer.renderSprite(animatedSprite, playerRectangle.x, playerRectangle.y, xZoom, yZoom, false);
		else if(sprite != null)
			renderer.renderSprite(sprite, playerRectangle.x, playerRectangle.y, xZoom, yZoom, false);
		else
			renderer.renderRectangle(playerRectangle, xZoom, yZoom, false);
		hud.render(renderer);
	}
	
	public void renderFront(RenderHandler renderer) {
		hud.render(renderer);
	}

	public void update(Game game) {
		KeyBoardListener keyListener = game.getKeyListener();
		
		didMove = false;
		int newDirection = direction;
		
		
		speed[0] = 0;
		speed[1] = 0;
		
		if(keyListener.left()) {
			newDirection = 1;
			didMove = true;
			speed[0] -= maxSpeed;
		}
		if(keyListener.right()) {
			newDirection = 0;
			didMove = true;
			speed[0] += maxSpeed;
		}
		if(keyListener.up()) {
			newDirection = 2;
			didMove = true;
			speed[1] -= maxSpeed;
		}
		if(keyListener.down()) {
			newDirection = 3;
			didMove = true;
			speed[1] += maxSpeed;
		}
		
		if(speed[1] == 0) {
			if(speed[0] == 0) {
				didMove = false;
				newDirection = direction;
			}
			else if(speed[0] < 0)
				newDirection = 1;
			else if(speed[0] > 0)
				newDirection = 0;
		}
		
		if(newDirection != direction && !hitBounce) {
			direction = newDirection;
			updateDirection();
		}
		
		if(!didMove&&!hitBounce) {
			 animatedSprite.reset();
		}
		
		if(hitBounce) {
			//animatedSprite.reset();
			speed[0] = hitSpeed[0];
			speed[1] = hitSpeed[1];
			didMove = true;
		}
		
		playerRectangle.x += speed[0];
		playerRectangle.y += speed[1];
		collisionRectangleBefore.x = collisionRectangle.x - speed[0];
		collisionRectangleBefore.y = collisionRectangle.y - speed[1];
	}
	
	public void collision(CollisionHandler collisionHandler) {
		Rectangle collision = new Rectangle(collisionRectangle.x + playerRectangle.x, collisionRectangleBefore.y + playerRectangle.y,
				collisionRectangle.w,collisionRectangle.h);
		
		if(didMove) {
			
			//Check collision with Tiles
			collision.x += speed[0];
			if(!game.getMap().checkCollision(collision, layer) &&
					!game.getMap().checkCollision(collision, layer - 1)) {
				//playerRectangle.x += speed[0];
			}else{
				if(speed[0] > 0)
					playerRectangle.x = game.getMap().getCollisionRectangle().x - collisionRectangle.x - collisionRectangle.w - 1;
				else if (speed[0] < 0)
					playerRectangle.x = game.getMap().getCollisionRectangle().x + game.getMap().getCollisionRectangle().w - collisionRectangle.x + 1;
			}
			
			collision.y = collisionRectangle.y + playerRectangle.y;
			collision.x = collisionRectangleBefore.x + playerRectangle.x;
			
			if(!game.getMap().checkCollision(collision, layer) &&
					!game.getMap().checkCollision(collision, layer - 1)) {
				//playerRectangle.y += speed[1];
			}else {
				if(speed[1] > 0)
					playerRectangle.y = game.getMap().getCollisionRectangle().y - collisionRectangle.y - collisionRectangle.h - 1;
				else if (speed[1] < 0)
					playerRectangle.y = game.getMap().getCollisionRectangle().y + game.getMap().getCollisionRectangle().h - collisionRectangle.y + 1;
			}
			collision.x = collisionRectangle.x + playerRectangle.x;
			collision.y = collisionRectangle.y + playerRectangle.y;
			
			//Check collision with Teleports
			int teleportID = game.getMap().checkTeleportCollision(collision);
			
			if(teleportID !=-1) {
				Teleport teleport = game.getMap().getTeleport(teleportID);
				if(teleport.relocation == 10)
					game.setMap(teleport.mapFile, teleport.tilesFile, teleport.spriteFile, teleport.relocation, teleport.spawnX, teleport.spawnY, teleport.direction);
				else
					game.setMap(teleport.mapFile, teleport.tilesFile, teleport.spriteFile, teleport.relocation);
			}
			
			animatedSprite.update(game);
			//updateCamera(game.getRenderer().getCamera());
		}
		
		
		//Objects collision
		ArrayList<Enemy> enemys = game.getEnemys();
		for(int i = 0; i < enemys.size(); i++) {
			Enemy enemy = enemys.get(i);
			Rectangle enemyRectangle = enemy.getCollisionRectangle();
			int[] enemySpeed = enemy.getSpeed();
			boolean[] collisionDirection = collisionHandler.checkCollision(collision, enemyRectangle, speed,enemySpeed);
			if (collisionDirection[0] || collisionDirection[1]) {
				if(hitCounter == 0) {
					hud.setCurrentHealth(hud.getCurrentHealth()-1);
					hitCounter++;
					hitSpeed[0] = 0 - speed[0];
					hitSpeed[1] = 0 - speed[1];
					playerRectangle.x += hitSpeed[0];
					playerRectangle.y += hitSpeed[1];
					hitBounce = true;
					int turn[] = {0,16,8,24};
					//animatedSprite.setAnimationRange(0, /*(direction * spriteNum + spriteNum-1)*4-1*/23,spriteNum);
					animatedSprite.setAnimationRange(turn, 2);
					AudioPlayer.getSound("hit").play(1, 0.1f);
				}else {
					playerRectangle = collisionHandler.blockCollision(playerRectangle, collisionRectangle, enemyRectangle, speed, enemySpeed, collisionDirection);
				}
			}
		}
		
		if(hud.getCurrentHealth() <= 0) {
			Game.gameState = STATE.Menu;
			speed[0] = 0;
			speed[1] = 0;
			game.gameOver();
		}

		//Check collision with the map border
		Map map = game.getMap();

		int newX = playerRectangle.x;
		int newY = playerRectangle.y;

		if(newX < 0)
			newX = 0;
		if(newX > map.getWidth() - playerRectangle.w * xZoom)
			newX = map.getWidth() - playerRectangle.w * xZoom;

		if(newY < 0)
			newY = 0;
		if(newY > map.getHeight() - playerRectangle.h * yZoom)
			newY = map.getHeight() - playerRectangle.h * yZoom;

		playerRectangle.x = newX;
		playerRectangle.y = newY;
		
		updateCamera(map, game.getRenderer().getCamera());
		hud.update(game);
		
		if(hitCounter > 0)
			hitCounter++;
		if(hitCounter > 10) {
			hitBounce = false;
			updateDirection();
		}
		if(hitCounter>=30)
			hitCounter = 0;
	}
	
	public void updateCamera(Map map, Rectangle camera) {
		int x = playerRectangle.x - (camera.w / 2);
		int y = playerRectangle.y - (camera.h / 2);
		
		if(x<0)
			x=0;
		
		if(x + camera.w > map.getWidth())
			x = map.getWidth() - camera.w;
		
		if(y<0)
			y=0;
		
		if(y + camera.h > map.getHeight())
			y = map.getHeight() - camera.h;
		
		camera.x = x;
		camera.y = y;
	}
	
	public int getLayer() {
		return layer;
	}
	
	public Rectangle getRectangle() {
		return playerRectangle;
	}
	
	public int getXZoom() {
		return xZoom;
	}
	
	public int getYZoom() {
		return yZoom;
	}
	
	public void relocatePlayer(int relocation, Map map) {
		if(relocation==0) {
			
		}else if(relocation==1)
			Spawn(playerRectangle.w * xZoom, playerRectangle.y);
		else if(relocation==2) {}
		else if(relocation==3) {
			Spawn(map.getWidth() - (playerRectangle.w + playerRectangle.w * xZoom), playerRectangle.y);
		}
	}
	
	public HUD getHud() {
		return hud;
	}
	
	public void reset() {
		hud.fill();
		animatedSprite.reset();
		hitCounter = 0;
		speed[0] = 0;
		speed[1] = 0;
		hitBounce = false;
	}

	@Override
	public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) {return false;}
	
	private void readInput(File inputFile) {
		BufferedImage sheetImage = null;
		try {
			Scanner scanner = new Scanner(inputFile);
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if(!line.startsWith("//")) {
					if(line.contains(":")) {
						String[] splitString = line.split(":");
						if(splitString[0].equalsIgnoreCase("spriteImage")) {
							sheetImage = game.loadImage(splitString[1]);
						}else if(splitString[0].equalsIgnoreCase("xZoom")) {
							this.xZoom = Integer.parseInt(splitString[1]);
						}else if(splitString[0].equalsIgnoreCase("yZoom")) {
							this.yZoom = Integer.parseInt(splitString[1]);
						}else if(splitString[0].equalsIgnoreCase("spriteWidth")) {
							this.spriteWidth = Integer.parseInt(splitString[1]);
						}else if(splitString[0].equalsIgnoreCase("spriteHeight")) {
							this.spriteHeight = Integer.parseInt(splitString[1]);
						}else if(splitString[0].equalsIgnoreCase("spriteNumber")) {
							this.spriteNum = Integer.parseInt(splitString[1]);
						}else if(splitString[0].equalsIgnoreCase("collisionRectangle")) {
							String[] splitStringTwo = splitString[1].split(",");
							collisionRectangle = new Rectangle(0,0,0,0);
							collisionRectangle.x = Integer.parseInt(splitStringTwo[0]);
							collisionRectangle.y = Integer.parseInt(splitStringTwo[1]);
							collisionRectangle.w = Integer.parseInt(splitStringTwo[2]);
							collisionRectangle.h = Integer.parseInt(splitStringTwo[3]);
						}
					}
				}
			}
			scanner.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//Adjust Collision Rectangle
		collisionRectangle.x *= xZoom;
		collisionRectangle.y *= yZoom;
		collisionRectangle.w *= xZoom;
		collisionRectangle.h *= yZoom;
		
		//Load Player SpriteSheet
		playerSheet = new SpriteSheet(sheetImage);
		playerSheet.loadSprites(spriteWidth, spriteHeight);
		this.sprite = new AnimatedSprite(playerSheet, 6);
		if(sprite != null && sprite instanceof AnimatedSprite)
			animatedSprite = (AnimatedSprite) sprite;
		
	}
}
