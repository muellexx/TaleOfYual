package com.game.graphics.gui;

import com.game.Game;
import com.game.graphics.RenderHandler;
import com.game.graphics.Sprite;
import com.game.utils.GameObject;
import com.game.utils.Rectangle;

public abstract class GUIButton implements GameObject {
	
	protected Sprite sprite;
	protected Rectangle rect;
	protected boolean fixed;
	
	public GUIButton(Sprite sprite, Rectangle rect, boolean fixed) {
		this.sprite = sprite;
		this.rect = rect;
		this.fixed = fixed;
	}

	public void render(RenderHandler renderer) {}
	
	public void render(RenderHandler renderer, int xZoom, int yZoom, Rectangle interfaceRect) {
		renderer.renderSprite(sprite, rect.x + interfaceRect.x, rect.y + interfaceRect.y, xZoom, yZoom, fixed);
	}

	public void update(Game game) {}

	public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) {
		//System.out.println("Mouse: " + mouseRectangle);
		//System.out.println("Button: " + rect);
		if(mouseRectangle.intersects(rect)) {
			activate();
			return true;
		}
		
		return false;
	}
	
	public abstract void activate();
	
	public abstract boolean deactivate();

}
