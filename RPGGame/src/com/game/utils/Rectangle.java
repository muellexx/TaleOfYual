package com.game.utils;

import com.game.Game;

public class Rectangle {
	
	public int x, y, w, h;
	private int[] pixels;
	
	public Rectangle(int x, int y, int w, int h){
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	public Rectangle(){
		this(0,0,0,0);
	}
	
	public void generateGraphics(int color) {
		pixels = new int[w*h];
		for(int y = 0; y < h; y++)
			for(int x = 0; x < w; x++)
				pixels[x + y * w] = color;
	}
	
	public boolean intersects(Rectangle otherRectangle) {
		if(x > otherRectangle.x + otherRectangle.w || otherRectangle.x > x + w)
			return false;
		if(y > otherRectangle.y + otherRectangle.h || otherRectangle.y > y + h)
			return false;
		
		return true;
	}
	
	public void generateGraphics(int borderWidth, int color) {
		pixels = new int[w*h];
		
		for (int i = 0; i < pixels.length; i++)
			pixels[i] = Game.alpha;
		
		for (int y = 0; y < borderWidth; y++)
			for (int x = 0; x < w; x++)
				pixels[x + y * w] = color;
		
		for (int y = h - borderWidth; y < h; y++)
			for (int x = 0; x < w; x++)
				pixels[x + y * w] = color;
		
		for (int x = 0; x < borderWidth; x++)
			for (int y = 0; y < h; y++)
				pixels[x + y * w] = color;
		
		for (int x = w - borderWidth; x < w; x++)
			for (int y = 0; y < h; y++)
				pixels[x + y * w] = color;
	}
	
	public int[] getPixels() {
		if(pixels != null)
			return pixels;
		else
			System.out.println("Attempted to retrieve pixels from a Rectangle without generated graphics.");
		
		return null;
	}
	
	public String toString() {
		return "[" + x + ", " + y + ", " + w + ", " + h + "]";
	}
	
}
