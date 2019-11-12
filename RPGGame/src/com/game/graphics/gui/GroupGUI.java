package com.game.graphics.gui;

import com.game.Game;
import com.game.graphics.RenderHandler;
import com.game.graphics.Sprite;
import com.game.utils.GameObject;
import com.game.utils.Rectangle;

public class GroupGUI implements GameObject {
	
	private Sprite backgroundSprite;
	private GUIButton[] buttons;
	private Rectangle rect = new Rectangle();
	private boolean fixed;
	private int xZoom, yZoom;
	
	public GroupGUI(Sprite backgroundSprite, GUIButton[] buttons, int x, int y, boolean fixed) {
		this.backgroundSprite = backgroundSprite;
		this.buttons = buttons;
		this.fixed = fixed;
		this.xZoom = 3;
		this.yZoom = 3;
		rect.x = x;
		rect.y = y;
		
		if(backgroundSprite != null) {
			rect.w = backgroundSprite.getWidth();
			rect.h = backgroundSprite.getHeight();
		}
	}
	
	public GroupGUI(GUIButton[] buttons, int x, int y, boolean fixed) {
		this(null, buttons, x, y, fixed);
	}
	
	public void render(RenderHandler renderer) {
		if(backgroundSprite != null)
			renderer.renderSprite(backgroundSprite, rect.x, rect.y, xZoom, yZoom, fixed);
		
		if(buttons != null)
			for(int i = 0; i < buttons.length; i++)
				buttons[i].render(renderer, xZoom, yZoom, rect);
	}

	public void update(Game game) {
		if(buttons != null)
			for(int i = 0; i < buttons.length; i++)
				buttons[i].update(game);
	}

	public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) {
		xZoom = this.xZoom;
		yZoom = this.yZoom;
		boolean deactivated;
		boolean stopChecking = false;
		int deactivatedID = -1;
		if(!fixed)
			mouseRectangle = new Rectangle(mouseRectangle.x + camera.x, mouseRectangle.y + camera.y, 1, 1);
		else
			mouseRectangle = new Rectangle(mouseRectangle.x, mouseRectangle.y, 1, 1);
		
		if(rect.w == 0 || rect.h == 0 || mouseRectangle.intersects(rect)) {
			mouseRectangle.x -= rect.x;
			mouseRectangle.y -= rect.y;
			if(buttons!=null) {
				for(int i = 0; i < buttons.length; i++) {
					if (mouseRectangle.intersects(buttons[i].rect)) {
						for(int j = 0; j < buttons.length; j++) {
							deactivated = buttons[j].deactivate();
							if (deactivated)
								deactivatedID = j;
						}
					}
				}
				for(int i = 0; i < buttons.length; i++) {
					if (i != deactivatedID) {
						boolean result = buttons[i].handleMouseClick(mouseRectangle, camera, xZoom, yZoom);
						if(stopChecking == false)
							stopChecking = result;
					}
				}
			}
		}
		return stopChecking;
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
