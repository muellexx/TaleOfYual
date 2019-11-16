package com.game.graphics.menu.sub;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import com.game.Game;
import com.game.Game.STATE;
import com.game.graphics.RenderHandler;
import com.game.graphics.menu.MenuPage;
import com.game.input.KeyBoardListener;
import com.game.utils.Rectangle;


public class GameOverMenu implements MenuPage {
	
	Game game;
	private Rectangle title, back, exit;
	private int selectedButton;
	private int numOfButtons = 2;
	
	public GameOverMenu(Game game, RenderHandler renderer) {
		Rectangle camera = renderer.getCamera();
		this.game = game;
		this.title = new Rectangle(0, 0, camera.w, 40);
		this.back = new Rectangle(80, 60, camera.w-160, 20);
		this.exit = new Rectangle(80, 90, camera.w-160, 20);
		this.selectedButton = 0;
		
		back.generateGraphics(2, 0x67FF3D);
		exit.generateGraphics(1, 0xFFDB3D);
	}
	
	public void render(RenderHandler renderer) {
		
		renderer.renderRectangle(back, 1, 1, true);
				
		renderer.renderRectangle(exit , 1, 1, true);
		
	}
	
	public void renderText(Graphics graphics) {
		Font fnt = new Font("arial", 1, 50);
		Font fnt2 = new Font("arial", 1, 30);
		graphics.setFont(fnt);
		graphics.setColor(Color.white);
		drawCenteredString(graphics, "Game Over", title, fnt);
		drawCenteredString(graphics, "Back", back, fnt2);
		drawCenteredString(graphics, "exit", exit, fnt2);
	}

	public void update(Game game) {
		KeyBoardListener keyListener = game.getKeyListener();
		boolean didChange = false;
		
		if(keyListener.upOnce()) {
			selectedButton -= 1;
			didChange = true;
		}
		if(keyListener.downOnce()) {
			selectedButton += 1;
			didChange = true;
		}
		
		if(selectedButton < 0) selectedButton = 0;
		else if (selectedButton >= numOfButtons) selectedButton = numOfButtons-1;
		
		if(didChange) {
			back.generateGraphics(1, 0xFFDB3D);
			exit.generateGraphics(1, 0xFFDB3D);
			if(selectedButton == 0)
				back.generateGraphics(2, 0x67FF3D);
			else if(selectedButton == 1)
				exit.generateGraphics(2, 0x67FF3D);
		}
		
		if(keyListener.enterOnce()) {
			if(selectedButton == 0) //new Game
				back();
			else if(selectedButton == 1) //continue Game
				exit();
		}
		
	}

	public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera) {
		boolean stopChecking = false;
		
		if(mouseRectangle.intersects(back)) {
			//if(Game.gameLoaded==false)
				//game.startGame();
			
			//Game.gameState = STATE.Game;
			back();
			stopChecking = true;
		}else if(mouseRectangle.intersects(exit)) {
			exit();
			stopChecking = true;
		}
		return stopChecking;
	}

	private void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) {
		Rectangle zoomRect = new Rectangle();
		zoomRect.x = rect.x*game.getRenderer().getScreen().w/game.getRenderer().getCamera().w;
		zoomRect.y = rect.y*game.getRenderer().getScreen().h/game.getRenderer().getCamera().h;
		zoomRect.w = rect.w*game.getRenderer().getScreen().w/game.getRenderer().getCamera().w;
		zoomRect.h = rect.h*game.getRenderer().getScreen().h/game.getRenderer().getCamera().h;
	    // Get the FontMetrics
	    FontMetrics metrics = g.getFontMetrics(font);
	    // Determine the X coordinate for the text
	    int x = zoomRect.x + (zoomRect.w - metrics.stringWidth(text)) / 2;
	    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
	    int y = zoomRect.y + ((zoomRect.h - metrics.getHeight()) / 2) + metrics.getAscent();
	    // Set the font
	    g.setFont(font);
	    // Draw the String
	    g.drawString(text, x, y);
	}
	
	public void back() {
		Game.newGameState = STATE.Menu;
		game.setMenu(new MainMenu(game,game.getRenderer()));
	}
	
	public void exit() {
		game.exitGame();
	}

}
