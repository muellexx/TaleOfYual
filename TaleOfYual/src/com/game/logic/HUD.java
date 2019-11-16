package com.game.logic;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.game.Game;
import com.game.graphics.RenderHandler;
import com.game.graphics.SpriteSheet;
import com.game.logic.Tiles.Tile;
import com.game.utils.GameObject;
import com.game.utils.Rectangle;

public class HUD implements GameObject{
	
	private Game game;
	private int maxHealth;
	private int currentHealth;
	private int numHearts;
	private int xZoom, yZoom;
	private int spriteWidth, spriteHeight;
	private boolean fixed;
	private Rectangle rect = new Rectangle();
	SpriteSheet heartsSheet;
	private ArrayList<Heart> heartsList = new ArrayList<Heart>();
	
	
	public HUD(int numHearts, Game game) {
		this.game = game;
		this.maxHealth = numHearts*4;
		this.currentHealth = maxHealth;
		fixed = true;
		xZoom = 1;
		yZoom = 1;
		spriteWidth = 24;
		spriteHeight = 24;
		this.numHearts = numHearts;
		rect.x = game.getRenderer().getCamera().w - spriteWidth - 4;
		rect.y = 4;
		rect.w = spriteWidth;
		rect.h = spriteHeight;
		
		//initialize Hearts
		loadHearts();
		int l = 5;
		
		for(int i = 0; i < numHearts; i++) {
			int j = l - 1 - i%l;
			if(numHearts < l)
				j = j - l + numHearts;
			int x = rect.x - j*(spriteWidth + 4);
			int y = rect.y + (i/l)*(spriteHeight + 4);
			Heart heart = new Heart(4,x,y);
			heartsList.add(heart);
		}
		updateHeartsHealth();
	}
	
	public void loadHearts() {
		BufferedImage sheetImage = game.loadImage("/Sprites/hearts.png");
		
		heartsSheet = new SpriteSheet(sheetImage);
		heartsSheet.loadSprites(spriteWidth, spriteHeight);
	}
	
	public void updateHeartsHealth() {
		int fullHealthHearts = currentHealth / 4;
		boolean currentHeart = false;
		for (int i = 0; i < heartsList.size(); i++) {
			if(i < fullHealthHearts)
				heartsList.get(i).setHealth(4);
			else if(!currentHeart) {
				heartsList.get(i).setHealth(currentHealth%4);
				currentHeart = true;
			}else
				heartsList.get(i).setHealth(0);
		}
	}
	
	public void setCurrentHealth(int currentHealth) {
		if(currentHealth >= 0) {
			this.currentHealth = currentHealth;
		}
		else {
			this.currentHealth = 0;
		}
	}
	
	public int getCurrentHealth() {
		return currentHealth;
	}
	
	public void fill() {
		currentHealth = maxHealth;
	}

	public void render(RenderHandler renderer) {
		//renderer.renderSprite(heartsSheet.getSprite(0), rect.x, rect.y, xZoom, yZoom, fixed);
		for(int i = 0; i < heartsList.size(); i++)
			heartsList.get(i).render(renderer);
	}

	public void update(Game game) {
		updateHeartsHealth();
	}

	public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) {return false;}

	public int getLayer() {return 0;}

	public Rectangle getRectangle() {return null;}

	public int getXZoom() {return xZoom;}

	public int getYZoom() {return yZoom;}
	
	class Heart {
		Rectangle heartRect;
		int heartHealth;
		
		public Heart(int heartHealth, int x, int y) {
			this.heartHealth = heartHealth;
			heartRect = new Rectangle(x,y,spriteWidth,spriteHeight);
		}
		
		public void render(RenderHandler renderer) {
			renderer.renderSprite(heartsSheet.getSprite(4-heartHealth), heartRect.x, heartRect.y, xZoom, yZoom, fixed);
		}
		
		public void setHealth(int heartHealth) {
			this.heartHealth = heartHealth;
		}
	}

}
