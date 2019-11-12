package com.game.utils;

public interface Enemy extends GameObject {
	
	public Rectangle getCollisionRectangle();
	
	public int[] getSpeed();
}
