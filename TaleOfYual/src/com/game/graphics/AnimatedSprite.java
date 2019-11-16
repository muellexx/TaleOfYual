package com.game.graphics;

import java.awt.image.BufferedImage;

import com.game.Game;
import com.game.utils.GameObject;
import com.game.utils.Rectangle;

public class AnimatedSprite extends Sprite implements GameObject {
	
	private Sprite[] sprites;
	private int currentSprite = 0;
	private int speed;
	private int standardSpeed;
	private int counter = 0;
	private int increment = 1;
	
	private int startSprite = 0;
	private int endSprite = 0;
	
	private int[] irregularSprites;
	private int irregularLength = 1;
	private int currentIrregular = 0;
	
	public AnimatedSprite(SpriteSheet sheet, Rectangle[] positions, int speed) {
		sprites = new Sprite[positions.length];
		this.speed = speed;
		standardSpeed = speed;
		this.endSprite = positions.length -1;
		irregularSprites = new int[irregularLength];
		
		for(int i = 0; i < positions.length; i++)
			sprites[i] = new Sprite(sheet, positions[i].x, positions[i].y, positions[i].w, positions[i].h);
	}
	
	public AnimatedSprite(SpriteSheet sheet, int speed) {
		sprites = sheet.getLoadedSprites();
		this.speed = speed;
		standardSpeed = speed;
		this.endSprite = sprites.length -1;
		irregularSprites = new int[irregularLength];
	}

	//@param speed represents how many frames pass until the sprite changes.
	public AnimatedSprite(BufferedImage[] images, int speed) {
		sprites = new Sprite[images.length];
		this.speed = speed;
		standardSpeed = speed;
		this.startSprite = images.length - 1;
		irregularSprites = new int[irregularLength];
		
		for(int i = 0; i < images.length; i++)
			sprites[i] = new Sprite(images[i]);
		
		
	}

	//Render is dealt specifically with the Layer class.
	public void render(RenderHandler renderer) {}

	public void update(Game game) {
		counter++;
		if(counter >= speed) {
			counter = 0;
			incrementSprite();
		}
	}
	
	public void reset() {
		counter = 0;
		currentSprite = startSprite;
	}
	
	public void setAnimationRange(int startSprite, int endSprite) {
		setAnimationRange(startSprite, endSprite, 1);
	}
	
	public void setAnimationRange(int startSprite, int endSprite, int increment, int speed) {
		this.startSprite = startSprite;
		this.endSprite = endSprite;
		this.increment = increment;
		this.irregularLength = 1;
		this.speed = speed;
		reset();
	}
	
	public void setAnimationRange(int startSprite, int endSprite, int increment) {
		setAnimationRange(startSprite, endSprite, increment, standardSpeed);
	}
	
	public void setAnimationRange(int[] irregularSprites, int speed) {
		this.irregularLength = irregularSprites.length;
		this.irregularSprites = irregularSprites;
		this.speed = speed;
		startSprite = irregularSprites[0];
		endSprite = irregularSprites[irregularLength-1];
		currentIrregular = 0;
		reset();
	}
	
	public void setAnimationRange(int[] irregularSprites) {
		setAnimationRange(irregularSprites, standardSpeed);
	}
	
	public int getWidth() {
		return sprites[currentSprite].getWidth();
	}
	
	public int getHeight() {
		return sprites[currentSprite].getHeight();
	}
	
	public int[] getPixels() {
		return sprites[currentSprite].getPixels();
	}
	
	public void incrementSprite() {
		if(irregularLength == 1) {
			currentSprite += increment;
			if(currentSprite >= endSprite)
				currentSprite = startSprite;
		}else if (irregularLength > 1) {
			if(currentIrregular >= irregularLength - 1)
				currentIrregular = 0;
			else
				currentIrregular++;
			currentSprite = irregularSprites[currentIrregular];
		}
	}
	
	public int getLayer() {
		System.out.println("Called getLayer() of animated Sprite. This has no meaning here");
		return -1;
	}

	public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) {return false;}
	
	public Rectangle getRectangle() {
		System.out.println("Called getRectangle() in AnimatedSprite! This has no meaning here");
		return null;
	}
	
	public int getXZoom() {
		System.out.println("Method getXZoom in animated Sprite was called, this should not happen.");
		return 1;
	}
	
	public int getYZoom() {
		System.out.println("Method getYZoom in animated Sprite was called, this should not happen.");
		return 1;
	}
	
}
