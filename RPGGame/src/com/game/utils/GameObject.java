package com.game.utils;

import com.game.Game;
import com.game.graphics.RenderHandler;

public interface GameObject {
	
	public void render(RenderHandler renderer);
	
	public void update(Game game);
	
	//Call whenever Mouse is clicked on Canvas.
	//Return true to stop checking other clicks.
	public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom);
	
	public int getLayer();
	
	public Rectangle getRectangle();
	
	public int getXZoom();
	
	public int getYZoom();
		
}
