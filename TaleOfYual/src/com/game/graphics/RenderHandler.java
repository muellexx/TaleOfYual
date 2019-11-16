package com.game.graphics;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.RescaleOp;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.game.Game;
import com.game.Game.STATE;
import com.game.utils.Rectangle;

public class RenderHandler {
	
	private Game game;
	private BufferedImage view;
	private Rectangle camera;
	private Rectangle screen;
	private int[] pixels;
	private int maxScreenWidth, maxScreenHeight;
	//private BufferedImage fade;
	RescaleOp rescale;
	ImageIcon icon;
	JLabel picLabel=new JLabel();
	int count = 0;
	int fade = 0;
	float fadeOffset = 0f;
	float fadeSpeed = 0f;
	float fadeEvent = 0;
	
	public RenderHandler(int width, int height, Game game) {
		this.game = game;
		GraphicsDevice[] graphicsDevices = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		
		for(int i = 0; i < graphicsDevices.length; i++) {
			if (maxScreenWidth < graphicsDevices[i].getDisplayMode().getWidth())
				maxScreenWidth = graphicsDevices[i].getDisplayMode().getWidth();
			if (maxScreenHeight < graphicsDevices[i].getDisplayMode().getHeight())
				maxScreenHeight = graphicsDevices[i].getDisplayMode().getHeight();
		}
				
		//Create a BufferedImage that will represent our view.
		view = new BufferedImage(maxScreenWidth, maxScreenHeight, BufferedImage.TYPE_INT_RGB);
		
		camera = new Rectangle(0, 0, 384, 216);
		screen = new Rectangle(0, 0, width, height);
		
		//Create an array for pixels
		pixels = ((DataBufferInt) view.getRaster().getDataBuffer()).getData();
		
		//Create Rectangle for FadeEffect
		Rectangle fade = new Rectangle(10,10,30,30);
		fade.generateGraphics(0xffffff);
	}
	
	/**Start fading of the image
	 * fadeType: 0 Fade In and Out; 1 Fade In; 2 Fade Out
	 * fadeSpeed: positive White; negative Black
	 * fadeEvent: 0 MenuFade; 1 inGameFade*/
	public void startFade(int fadeType, float fadeSpeed, int fadeEvent) {
		this.fade = fadeType + 1;
		this.fadeSpeed = fadeSpeed;
		this.fadeEvent = fadeEvent;
		if (fade == 1 || fade == 2) {
			fadeOffset = 0f;
		}else if (fade == 3) {
			if(fadeEvent == 0)
				Game.gameState = Game.newGameState;
			if(fadeSpeed > 0f)
				fadeOffset = 256f;
			else if(fadeSpeed < 0f)
				fadeOffset = -256f;
		}
		
	}
	
	public int getFade() {
		return fade;
	}
	
	public void render(Graphics graphics) {
		if(fade!=0)
			fadeImage(graphics);
		else
			graphics.drawImage(view.getSubimage(0, 0, camera.w, camera.h), 0, 0, screen.w, screen.h, null);
		
	}
	
	public void renderImage(BufferedImage image, int xPosition, int yPosition, boolean fixed) {
		int[] imagePixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		renderArray(imagePixels, image.getWidth(), image.getHeight(), xPosition, yPosition, fixed);	
	}
	
	public void renderImage(BufferedImage image, int xPosition, int yPosition, int xZoom, int yZoom, boolean fixed) {
		int[] imagePixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		renderArray(imagePixels, image.getWidth(), image.getHeight(), xPosition, yPosition, xZoom, yZoom, fixed);						
	}
	
	public void renderSprite(Sprite sprite, int xPosition, int yPosition, int xZoom, int yZoom, boolean fixed) {
		renderArray(sprite.getPixels(), sprite.getWidth(), sprite.getHeight(), xPosition, yPosition, xZoom, yZoom, fixed);
	}
	
	public void renderRectangle(Rectangle rectangle, int xZoom, int yZoom, boolean fixed) {
		int[] rectanglePixels = rectangle.getPixels();
		if(rectanglePixels != null)
			renderArray(rectanglePixels, rectangle.w, rectangle.h, rectangle.x, rectangle.y, xZoom, yZoom, fixed);
	}
	
	public void renderRectangle(Rectangle rectangle, Rectangle offset, int xZoom, int yZoom, boolean fixed) {
		int[] rectanglePixels = rectangle.getPixels();
		if(rectanglePixels != null)
			renderArray(rectanglePixels, rectangle.w, rectangle.h, rectangle.x + offset.x, rectangle.y + offset.y, xZoom, yZoom, fixed);
	}
	
	public void renderArray(int[] renderPixels, int renderWidth, int renderHeight, int xPosition, int yPosition, boolean fixed) {
		for(int y = 0; y < renderHeight; y++)
			for(int x = 0; x < renderWidth; x++)
						setPixel(renderPixels[x + y * renderWidth], x + xPosition, y  + yPosition, fixed);
	}
	
	public void renderArray(int[] renderPixels, int renderWidth, int renderHeight, int xPosition, int yPosition, int xZoom, int yZoom, boolean fixed) {
		for(int y = 0; y < renderHeight; y++)
			for(int x = 0; x < renderWidth; x++)
				for(int yZoomPosition = 0; yZoomPosition < yZoom; yZoomPosition++)
					for(int xZoomPosition = 0; xZoomPosition < xZoom; xZoomPosition++)
						setPixel(renderPixels[x + y * renderWidth], ((x * xZoom) + xPosition + xZoomPosition), ((y * yZoom) + yPosition + yZoomPosition), fixed);
	}
	
	private void setPixel(int pixel, int x, int y, boolean fixed) {
		int pixelIndex = 0;
		if(!fixed) {
			if(x >= camera.x && y >= camera.y && x <= camera.x + camera.w && y <= camera.y + camera.h)
				pixelIndex = (x - camera.x) + (y - camera.y) * view.getWidth();
		}else {
			if(x >= 0 && y >= 0 && x <= camera.w && y <= camera.h)
				pixelIndex = x + y * view.getWidth();
		}
		if(pixels.length > pixelIndex && pixel != Game.alpha)
			pixels[pixelIndex] = pixel;
	}
	
	public Rectangle getCamera() {
		return camera;
	}
	
	public Rectangle getScreen() {
		return screen;
	}
	
	public int getMaxWidth() {
		return maxScreenWidth;
	}
	
	public int getMaxHeight() {
		return maxScreenHeight;
	}
	
	public void clear() {
		for(int i = 0; i < pixels.length; i++)
			pixels[i] = 0;
	}
	
	public void fadeImage(Graphics graphics) {
		if(fade == 1 || fade == 2) {
			rescale = new RescaleOp(1.0f,fadeOffset, null);
			BufferedImage fadeView=rescale.filter(view,null); //(source,destination)
			graphics.drawImage(fadeView.getSubimage(0, 0, camera.w, camera.h), 0, 0, screen.w, screen.h, null);
			count++;
			fadeOffset += fadeSpeed;
			if (fadeOffset >= 256f || fadeOffset <= -256f) {
				fade += 2;
				count = 0;
				if(fadeEvent == 0) {
					Game.gameState = Game.newGameState;
				}
				if(fadeEvent == 1) {
					game.getPlayer().updateSpawn();
					game.loadMap();
				}
			}
		}else if(fade == 3) {
			rescale = new RescaleOp(1.0f,fadeOffset, null);
			BufferedImage fadeView=rescale.filter(view,null);//(sourse,destination)
			graphics.drawImage(fadeView.getSubimage(0, 0, camera.w, camera.h), 0, 0, screen.w, screen.h, null);
			count++;
			fadeOffset -= fadeSpeed;
			if ((fadeSpeed <=0 && fadeOffset >= 0)||(fadeSpeed >= 0 && fadeOffset <= 0)) {
				fade = 0;
				count = 0;
			}
		}
		if (fade >= 4)
			fade = 0;
	}
	
}
