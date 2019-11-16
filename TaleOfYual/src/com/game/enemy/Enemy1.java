package com.game.enemy;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import com.game.Game;
import com.game.graphics.AnimatedSprite;
import com.game.graphics.RenderHandler;
import com.game.graphics.Sprite;
import com.game.graphics.SpriteSheet;
import com.game.input.KeyBoardListener;
import com.game.logic.Map;
import com.game.utils.GameObject;
import com.game.utils.Enemy;
import com.game.utils.Rectangle;

public class Enemy1 implements Enemy {
	
	private Rectangle enemyRectangle;
	private Rectangle collisionRectangle;
	private int speed = 1;
	private int xZoom, yZoom;
	private int spriteNum;
	private Game game;
	private SpriteSheet playerSheet;
	private int spriteWidth, spriteHeight;
	private int xSpeed, ySpeed;
	
	//0 = Right, 1 = Left, 2 = Up, 3 = Down
	private int direction = 0;
	private int layer = 1;
	private Sprite sprite;
	private AnimatedSprite animatedSprite = null;
	private int moveTimer = 0;
	private boolean moving = false;
	
	public Enemy1(File enemyFile, int xSpawn, int ySpawn, Game game) {
		
		this.game = game;
		readInput(enemyFile);
		
		updateDirection();
		enemyRectangle = new Rectangle(xSpawn, ySpawn, spriteWidth, spriteHeight);
		//enemyRectangle.generateGraphics(3, 0xFF00FF90);
	}
	
	public void Spawn(int spawnX, int spawnY) {
		enemyRectangle.x = spawnX;
		enemyRectangle.y = spawnY;
	}
	
	public void Spawn(int spawnX, int spawnY, int direction) {
		Spawn(spawnX, spawnY);
		this.direction = direction;
		updateDirection();
	}
	
	private void updateDirection() {
		if(animatedSprite != null){
			animatedSprite.setAnimationRange(direction * spriteNum, direction * spriteNum + spriteNum-1);
		}
	}

	public void render(RenderHandler renderer) {
		if(animatedSprite != null)
			renderer.renderSprite(animatedSprite, enemyRectangle.x, enemyRectangle.y, xZoom, yZoom, false);
		else if(sprite != null)
			renderer.renderSprite(sprite, enemyRectangle.x, enemyRectangle.y, xZoom, yZoom, false);
		else
			renderer.renderRectangle(enemyRectangle, xZoom, yZoom, false);
	}

	public void update(Game game) {
		
		boolean collided = false;
				
		if(moveTimer>=60) {
			moveTimer = 0;
			if(moving) {
				moving = false;
				animatedSprite.reset();
			}
			else
				moving = true;
		}
		
		if(moveTimer == 0) {
			if(moving) {
				Random rand = new Random();
				direction = rand.nextInt(4);
				updateDirection();
			}
		}
		
		moveTimer ++;
		
		xSpeed = 0;
		ySpeed = 0;
		
		Rectangle collision = new Rectangle(collisionRectangle.x + enemyRectangle.x, collisionRectangle.y + enemyRectangle.y,
				collisionRectangle.w,collisionRectangle.h);
		
		if(moving) {
			if(direction == 1) {
				xSpeed -= speed;
			}
			if(direction == 0) {
				xSpeed += speed;
			}
			if(direction == 2) {
				ySpeed -= speed;
			}
			if(direction == 3) {
				ySpeed += speed;
			}

			//if(didMove) {

			//Check collision with Tiles
			collision.x += xSpeed;
			if(!game.getMap().checkCollision(collision, layer) &&
					!game.getMap().checkCollision(collision, layer - 1)) {
				enemyRectangle.x += xSpeed;
			}else{
				collided = true;
				if(xSpeed > 0)
					enemyRectangle.x = game.getMap().getCollisionRectangle().x - collisionRectangle.x - collisionRectangle.w - 1;
				else if (xSpeed < 0)
					enemyRectangle.x = game.getMap().getCollisionRectangle().x + game.getMap().getCollisionRectangle().w - collisionRectangle.x + 1;
			}

			collision.y += ySpeed;
			collision.x = collisionRectangle.x + enemyRectangle.x;

			if(!game.getMap().checkCollision(collision, layer) &&
					!game.getMap().checkCollision(collision, layer - 1)) {
				enemyRectangle.y += ySpeed;
			}else {
				collided = true;
				if(ySpeed > 0)
					enemyRectangle.y = game.getMap().getCollisionRectangle().y - collisionRectangle.y - collisionRectangle.h - 1;
				else if (ySpeed < 0)
					enemyRectangle.y = game.getMap().getCollisionRectangle().y + game.getMap().getCollisionRectangle().h - collisionRectangle.y + 1;
			}
			collision.y = collisionRectangle.y + enemyRectangle.y;

			animatedSprite.update(game);
			if(collided) {
				moveTimer = 0;
				moving = false;
			}
		}
		
		//Check collision with the map border
		Map map = game.getMap();
		
		int newX = enemyRectangle.x;
		int newY = enemyRectangle.y;
		
		if(newX < 0) {
			collided = true;
			newX = 0;
		}
		if(newX > map.getWidth() - enemyRectangle.w * xZoom) {
			collided = true;
			newX = map.getWidth() - enemyRectangle.w * xZoom;
		}
		if(newY < 0) {
			collided = true;
			newY = 0;
		}
		if(newY > map.getHeight() - enemyRectangle.h * yZoom) {
			collided = true;
			newY = map.getHeight() - enemyRectangle.h * yZoom;
		}
		
		if(collided) {
			moveTimer = 0;
			moving = false;
			animatedSprite.reset();
		}
		
		enemyRectangle.x = newX;
		enemyRectangle.y = newY;
	}
	
	public int getLayer() {
		return layer;
	}
	
	public Rectangle getRectangle() {
		return enemyRectangle;
	}
	
	public int getXZoom() {
		return xZoom;
	}
	
	public int getYZoom() {
		return yZoom;
	}
	
	public int getXSpeed() {
		return xSpeed;
	}
	
	public int getYSpeed() {
		return ySpeed;
	}
	
	public int[] getSpeed() {
		int[] speed = new int[2];
		speed[0] = xSpeed;
		speed[1] = ySpeed;
		return speed;
	}
	
	public Rectangle getCollisionRectangle() {
		Rectangle rect = new Rectangle(enemyRectangle.x + collisionRectangle.x, enemyRectangle.y + collisionRectangle.y,
										collisionRectangle.w, collisionRectangle.h);
		return rect;
	}
	
	public void relocateEnemy(int relocation, Map map) {
		if(relocation==0) {
			
		}else if(relocation==1)
			Spawn(enemyRectangle.w * xZoom, enemyRectangle.y);
		else if(relocation==2) {}
		else if(relocation==3) {
			Spawn(map.getWidth() - (enemyRectangle.w + enemyRectangle.w * xZoom), enemyRectangle.y);
		}
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
