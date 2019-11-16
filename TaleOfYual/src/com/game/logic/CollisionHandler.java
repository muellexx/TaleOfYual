package com.game.logic;

import com.game.utils.GameObject;
import com.game.utils.Rectangle;

public class CollisionHandler {
	
	public CollisionHandler() {
		
	}
	
	public void block(GameObject object, Rectangle rect) {
		
	}
	
	public int checkCollision(Rectangle actionRect, Rectangle fixedRect, int[] speed) {
		actionRect.x += speed[0];
		
		
		
		return 0;
	}
	
	public boolean[] checkCollision(Rectangle playerCollision, Rectangle enemyCollision, int[] playerSpeed, int[] enemySpeed) {
		boolean[] collision = new boolean[2];
		
		//check x-Direction for Collision
		Rectangle pCollision = new Rectangle(playerCollision.x, playerCollision.y - playerSpeed[1], playerCollision.w, playerCollision.h);
		Rectangle eCollision = new Rectangle(enemyCollision.x, enemyCollision.y - enemySpeed[1], enemyCollision.w, enemyCollision.h);
		collision[0] = pCollision.intersects(eCollision);
		
		//check y-Direction for Collision
		pCollision = new Rectangle(playerCollision.x - playerSpeed[0], playerCollision.y, playerCollision.w, playerCollision.h);
		eCollision = new Rectangle(enemyCollision.x - enemySpeed[0], enemyCollision.y, enemyCollision.w, enemyCollision.h);
		collision[1] = pCollision.intersects(eCollision);
		
		return collision;
	}
	
	public Rectangle blockCollision(Rectangle playerRect, Rectangle playerCollision, Rectangle enemyCollision,
											int[] playerSpeed, int[] enemySpeed, boolean[] collisionDirection) {
		Rectangle newRect = new Rectangle(playerRect.x, playerRect.y, playerRect.w, playerRect.h);
		int[] totalSpeed = new int[2];
		totalSpeed[0] = playerSpeed[0] - enemySpeed[0];
		totalSpeed[1] = playerSpeed[1] - enemySpeed[1];
		
		if(collisionDirection[0]) {
			if(totalSpeed[0] > 0)
				newRect.x = enemyCollision.x - playerCollision.x - playerCollision.w - 1;
			else if (totalSpeed[0] < 0)
				newRect.x = enemyCollision.x + enemyCollision.w - playerCollision.x + 1;
		}
		
		if(collisionDirection[1]) {
			if(totalSpeed[1] > 0)
				newRect.y = enemyCollision.y - playerCollision.y - playerCollision.h - 1;
			else if (totalSpeed[1] < 0)
				newRect.y = enemyCollision.y + enemyCollision.h - playerCollision.y + 1;
		}
		
		return newRect;
	}
	
	public int[] setSpeed(int[] speed) {
		int[] newSpeed = speed;
		return newSpeed;
	}
}
