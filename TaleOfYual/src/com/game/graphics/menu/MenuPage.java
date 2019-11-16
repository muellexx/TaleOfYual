package com.game.graphics.menu;

import java.awt.Graphics;

import com.game.Game;
import com.game.graphics.RenderHandler;
import com.game.utils.Rectangle;

public interface MenuPage {
	
	public void render(RenderHandler renderer);
	
	public void update(Game game);
	
	//Call whenever Mouse is clicked on Canvas.
	//Return true to stop checking other clicks.
	public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera);
	
	public void renderText(Graphics graphics);
	
}
