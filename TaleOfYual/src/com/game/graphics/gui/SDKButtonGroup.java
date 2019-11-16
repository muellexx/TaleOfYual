package com.game.graphics.gui;

import com.game.Game;
import com.game.graphics.RenderHandler;
import com.game.graphics.Sprite;
import com.game.logic.Tiles;
import com.game.utils.Rectangle;

public class SDKButtonGroup extends GUIButton {
	
	private Game game;
	private int groupID;
	private boolean isSelected = false;
	private int xZoom, yZoom;
	private int xBorder, yBorder;
	private Rectangle interfaceRect;
	
	public SDKButtonGroup(Game game, int groupID, Sprite tileSprite, Rectangle rect, int xBorder, int yBorder) {
		super(tileSprite, rect, true);
		this.game = game;
		this.groupID = groupID;
		this.xZoom = game.getTiles().getXZoom();
		this.yZoom = game.getTiles().getYZoom();
		this.xBorder = xBorder;
		this.yBorder = yBorder;
		interfaceRect = new Rectangle(0,0,0,0);
		rect.w = rect.w + 2*xBorder;
		rect.h = rect.h + 2*yBorder;
		rect.generateGraphics(0xFFDB3D);
	}
	
	@Override
	public void update(Game game) {
		if(isSelected) {
			rect.generateGraphics(0x67FF3D);
		}
		if(!isSelected) {
			rect.generateGraphics(0xFFDB3D);
		}
	}

	@Override
	public void render(RenderHandler renderer, int xZoom, int yZoom, Rectangle interfaceRect) {
		xZoom = this.xZoom;
		yZoom = this.yZoom;
		this.interfaceRect = interfaceRect;
		renderer.renderRectangle(rect, interfaceRect, 1, 1, fixed);
		renderer.renderSprite(sprite, 
								//rect.x + interfaceRect.x + (xZoom /*- (xZoom - 1)*/) * rect.w/2/xZoom, 
								//rect.y + interfaceRect.y + (yZoom /*- (yZoom - 1)*/) * rect.h/2/yZoom,
								rect.x + xBorder + interfaceRect.x,
								rect.y + yBorder + interfaceRect.y,
								xZoom,// - 1, 
								yZoom,// - 1, 
								fixed);
	}
	
	public void activate() {
		isSelected = true;
		Tiles tiles = game.getTiles();
		GUIButton[] buttons = new GUIButton[game.getTiles().groupSize(groupID)];
		Sprite[] tileSprites = tiles.getSprites(groupID);
		//int[] groupIDs = tiles.getGroupIDs();
		int j = 0;
		int k = 0;
		int l = 0;
		for(int i = 0; i < tiles.size(groupID); i++) {
			//if(groupIDs[i] == groupID) {
				int x = tiles.getTileWidth()*xZoom + 2*xBorder + 10 + interfaceRect.x + k*(tiles.getTileWidth()*xZoom + 2*xBorder + 3);
				int y = l*(tiles.getTileHeight()*yZoom + 2*yBorder + 3);
				Rectangle tileRectangle = new Rectangle(x, y, tiles.getTileWidth()*xZoom, tiles.getTileHeight()*yZoom);
				buttons[j] = new SDKButtonTile(game, i, tileSprites[i], tileRectangle, xBorder, yBorder);
				j++;
				k++;
				if (x >= game.getRenderer().getCamera().w - 2 * (tiles.getTileWidth()*xZoom + 2*xBorder + 10)) {
					k = 0;
					l++;
				}
			//}
		}
		TileGUI gui = new TileGUI(buttons, 5, 5, true);
		game.setTilesGui(gui);
		//game.getObjects().add(gui);
		//objectIndex = game.getObjects().size() - 1;
		game.changeTileGroup(groupID);
		game.changeTile(-1);
	}

	public boolean deactivate() {
		boolean deactivated = false;
		if(isSelected) {
			isSelected = false;
			//game.getObjects().remove(objectIndex);
			TileGUI gui = new TileGUI(null, 5, 5, true);
			game.setTilesGui(gui);
			deactivated = true;
		}
		return deactivated;
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
