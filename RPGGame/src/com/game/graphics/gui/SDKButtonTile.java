package com.game.graphics.gui;

import com.game.Game;
import com.game.graphics.RenderHandler;
import com.game.graphics.Sprite;
import com.game.utils.Rectangle;

public class SDKButtonTile extends GUIButton {
	
	private Game game;
	private int tileID;
	private boolean isGreen = false;
	private int xZoom, yZoom;
	private int xBorder, yBorder;
	
	public SDKButtonTile(Game game, int tileID, Sprite tileSprite, Rectangle rect, int xBorder, int yBorder) {
		super(tileSprite, rect, true);
		this.game = game;
		this.tileID = tileID;
		this.xZoom = game.getTiles().getXZoom();
		this.yZoom = game.getTiles().getYZoom();
		this.xBorder = xBorder;
		this.yBorder = yBorder;
		rect.w = rect.w + 2*xBorder;
		rect.h = rect.h + 2*yBorder;
		rect.generateGraphics(0x88EAE6);
	}
	
	@Override
	public void update(Game game) {
		if(tileID == game.getSelectedTile()) {
			if(!isGreen) {
				rect.generateGraphics(0x00215C);
				isGreen = true;
			}
		}else {
			if(isGreen) {
				rect.generateGraphics(0x88EAE6);
				isGreen = false;
			}
		}
	}

	@Override
	public void render(RenderHandler renderer, int xZoom, int yZoom, Rectangle interfaceRect) {
		xZoom = this.xZoom;
		yZoom = this.yZoom;
		renderer.renderRectangle(rect, interfaceRect, 1, 1, fixed);
		renderer.renderSprite(sprite, 
								//rect.x + interfaceRect.x + (xZoom - (xZoom - 1)) * rect.w/2/xZoom, 
								//rect.y + interfaceRect.y + (yZoom - (yZoom - 1)) * rect.h/2/yZoom, 
								rect.x + xBorder + interfaceRect.x,
								rect.y + yBorder + interfaceRect.y,
								xZoom, 
								yZoom, 
								fixed);
	}
	
	public void activate() {
		game.changeTile(tileID);
	}

	public boolean deactivate() {	
		return false;
	}
	
	public int getLayer() {
		return Integer.MAX_VALUE;
	}
	
	public Rectangle getRectangle() {
		return rect;
	}
	
	public int getXZoom() {
		return xZoom;
	}
	
	public int getYZoom() {
		return yZoom;
	}

}
