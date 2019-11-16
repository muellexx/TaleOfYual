 package com.game;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.lwjgl.openal.AL;

import com.game.enemy.Enemy1;
import com.game.graphics.*;
import com.game.graphics.gui.*;
import com.game.graphics.menu.MenuPage;
import com.game.graphics.menu.bar.EditMenuBar;
import com.game.graphics.menu.sub.GameOverMenu;
import com.game.graphics.menu.sub.MainMenu;
import com.game.input.*;
import com.game.logic.*;
import com.game.utils.*;

public class Game extends JFrame implements Runnable {
	
	private static final long serialVersionUID = -3458034175125080839L;
	
	public static int alpha = 0xFFFF00DC;
		
	private Canvas canvas = new Canvas();
	private RenderHandler renderer;
	
	private SpriteSheet sheet;
	
	private int selectedTileGroup = -1;
	private int selectedTileID = -1;
	private int selectedLayer = 1;
	
	private Rectangle testRectangle = new Rectangle(30, 90, 100, 100);
	
	private File startTiles;
	private File startMap;
	private String startSprite;
	private Tiles tiles;
	private Map map;
	private Map mapNew;
	private File mapFileNew;
	
	//private GameObject[] objects;
	private ArrayList<GameObject> objects = new ArrayList<GameObject>();
	private ArrayList<Enemy> enemys = new ArrayList<Enemy>();
	private GameObject TileGroupsGui;
	private GameObject TilesGui;
	public static boolean showEditGui = false;
	private MenuPage Menu;
	private KeyBoardListener keyListener = new KeyBoardListener(this);
	private MouseEventListener mouseListener = new MouseEventListener(this);
	private Actions actions;
	
	private Player player;
	private Enemy1 enemy1;
	
	private CollisionHandler collisionHandler;
	
	public enum STATE{
		Menu,
		Game,
		GameOver,
		Edit
	};
	
	public static STATE gameState = STATE.Menu;
	public static STATE newGameState = STATE.Menu;
	public static boolean gameLoaded = false;
	
	public Game() {
		
		super("Alex' first RPG Game");
		
		AudioPlayer.load();
		
		//Make the program shut down when we exit out
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setBounds(0,0,1280,720);
		canvas.setBounds(0, 0, 1280,720);
		
		//Put the frame in the middle of the screen
		setLocationRelativeTo(null);
		canvas.setFocusable(true);
		
		//Add Graphics component
		add(canvas);
		pack();
		
		//Make the Frame visible
		setVisible(true);
		
		//Create Object for Buffer Strategy
		canvas.createBufferStrategy(3);
		
		renderer = new RenderHandler(canvas.getWidth(), canvas.getHeight(), this);
		collisionHandler = new CollisionHandler();
		
		//Load assets
		//Set Start Map, Tiles and Sheet
		//startSprite = "/Sprites/16ogaAlpha.png";
		startSprite = "/Sprites/terrain_atlasAlpha.png";
		//startTiles = new File("res/Tiles/Tiles2.txt");
		startTiles = new File("res/Tiles/Tiles3.txt");
		//startMap = new File("res/Maps/Map1.txt");
		startMap = new File("res/Maps/Map3.txt");
		
		
		//BufferedImage sheetImage = loadImage("/Sprites/terrain_atlasAlpha.png");
		BufferedImage sheetImage = loadImage(startSprite);
		sheet = new SpriteSheet(sheetImage);
		//sheet.loadSprites(16, 16);
		sheet.loadSprites(startTiles);
		
		BufferedImage animatedWaterImage = loadImage("/Sprites/Water16Frames8x4.png");
		SpriteSheet animatedWater = new SpriteSheet(animatedWaterImage);
		animatedWater.loadSprites(16, 16);
		
		//Load Tiles
		//tiles = new Tiles(new File("res/Tiles.txt"), sheet);
		tiles = new Tiles(sheet);
		
		//Load Map
		map = new Map(startMap, tiles);
		
		testRectangle.generateGraphics(3, 12234);
				
		if (gameState == STATE.Game) {
			startGame();
		} else if (gameState == STATE.Menu) {
			Menu = new MainMenu(this, renderer);
		}
		//add Listener
		canvas.addKeyListener(keyListener);
		canvas.addFocusListener(keyListener);
		canvas.addMouseListener(mouseListener);
		canvas.addMouseMotionListener(mouseListener);
		
		addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent e) {
				int newWidth = canvas.getWidth();
				int newHeight = canvas.getHeight();
				
				if (newWidth > renderer.getMaxWidth())
					newWidth = renderer.getMaxWidth();
				if (newHeight > renderer.getMaxHeight())
					newHeight = renderer.getMaxHeight();
				renderer.getScreen().w = newWidth;
				renderer.getScreen().h = newHeight;
				canvas.setSize(newWidth, newHeight);
				pack();
			}
			
			public void componentHidden(ComponentEvent e) {}
			public void componentMoved(ComponentEvent e) {}
			public void componentShown(ComponentEvent e) {}
		});
		
		Actions actions = new Actions(this);
		
		AudioPlayer.getMusic("music").loop(1, 0.05f);
	}
	
	public void startGame() {
		//gameLoaded = false;
		//Load Objects
		int xSpawn = 400;
		int ySpawn = 480;
		if(gameLoaded == true) {
			this.setMap(startMap, startTiles, startSprite, xSpawn, ySpawn);
			loadMap();
			player.reset();
		}else {
			objects.clear();
			player = new Player(new File("res/PlayerFiles/wizard.txt"), xSpawn, ySpawn, this);
			objects.add(player);
			enemy1 = new Enemy1(new File("res/PlayerFiles/demon.txt"), 300, 300, this);
			objects.add(enemy1);
			enemys.add(enemy1);
		}
		gameLoaded = true;
		player.updateCamera(map, renderer.getCamera());
	}
	
	public void editGame() {
		//Load SDK GUI
		TileGroupsGui = new GroupGUI(null, 5, 5, true);
		TilesGui = new TileGUI(null, 5, 5, true);
		
		
		//Load Menu Bar
		EditMenuBar menuBar = new EditMenuBar();
		setJMenuBar(menuBar.buildMenuBar());
		setVisible(true);
	}
	
	public void update() {
		GlobalKeys.update(this);
		if(renderer.getFade() == 0) {
			if(gameState == STATE.Game) {
				for(int i = 0; i < objects.size(); i++)
					objects.get(i).update(this);
				//objects[i].update(this);
				player.collision(collisionHandler);
			} else if(gameState == STATE.Edit) {
				TilesGui.update(this);
				TileGroupsGui.update(this);
				for(int i = 0; i < objects.size(); i++)
					objects.get(i).update(this);
				//objects[i].update(this);
				player.collision(collisionHandler);
			} else if(gameState == STATE.Menu || gameState == STATE.GameOver) {
				Menu.update(this);
			}
		}
		
	}
	
	public BufferedImage loadImage(String path) {
		try {
			BufferedImage loadedImage = ImageIO.read(Game.class.getResource(path));
			BufferedImage formattedImage = new BufferedImage(loadedImage.getWidth(),loadedImage.getHeight(),BufferedImage.TYPE_INT_RGB);
			formattedImage.getGraphics().drawImage(loadedImage, 0, 0, null);
			return formattedImage;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void handleCTRL(boolean[] keys) {
		if(keys[KeyEvent.VK_S]) {
			map.saveMap();
		}
	}
	
	public void leftClick(int x, int y) {
		Rectangle mouseRectangle = new Rectangle(x, y, 1, 1);
		boolean stopChecking = false;
		if(gameState == STATE.Edit) {
			if (!stopChecking)
				stopChecking = TilesGui.handleMouseClick(mouseRectangle, renderer.getCamera(), x, y);
			if (!stopChecking)
				stopChecking = TileGroupsGui.handleMouseClick(mouseRectangle, renderer.getCamera(), x, y);
			for(int i = 0; i < objects.size(); i++)
				if(!stopChecking)
					stopChecking = objects.get(i).handleMouseClick(mouseRectangle, renderer.getCamera(), x, y);
			
			if(!stopChecking&&selectedTileID!=-1 && selectedTileGroup != -1) {
				x = (int)Math.floor((x + renderer.getCamera().x) / (tiles.getTileWidth() * tiles.getXZoom()));
				y = (int)Math.floor((y + renderer.getCamera().y) / (tiles.getTileHeight() * tiles.getYZoom()));
				map.setTile(selectedLayer, x, y, selectedTileGroup, selectedTileID);
			}
		} else if(gameState == STATE.Menu || gameState == STATE.GameOver) {
			stopChecking = Menu.handleMouseClick(mouseRectangle, renderer.getCamera());
		}
	}
	
	public void rightClick(int x, int y) {
		if(gameState == STATE.Edit) {
			x = (int)Math.floor((x + renderer.getCamera().x) / (tiles.getTileWidth() * tiles.getXZoom()));
			y = (int)Math.floor((y + renderer.getCamera().y) / (tiles.getTileHeight() * tiles.getYZoom()));
			map.removeTile(selectedLayer, x, y);
		}
	}
	
	public void render() {
		BufferStrategy bufferStrategy = canvas.getBufferStrategy();
		Graphics graphics = bufferStrategy.getDrawGraphics();
		super.paint(graphics);
		
		if(gameState == STATE.Game) {
			map.render(renderer, objects);
		} else if(gameState == STATE.Menu || gameState == STATE.GameOver) {
			Menu.render(renderer);
		} else if(gameState == STATE.Edit) {
			map.render(renderer, objects);
			TilesGui.render(renderer);
			TileGroupsGui.render(renderer);
		}
		renderer.render(graphics);
		
		if(gameState == STATE.Menu || gameState == STATE.GameOver) {
			Menu.renderText(graphics);
		}
		//super.paint(graphics);
		
		graphics.dispose();
		bufferStrategy.show();
		renderer.clear();
	}
	
	public void changeTile(int tileID) {
		selectedTileID = tileID;
	}
	
	public int getSelectedTile() {
		return selectedTileID;
	}
	
	public void changeTileGroup(int groupID) {
		selectedTileGroup = groupID;
	}
	
	public int getSelectedTileGroup() {
		return selectedTileGroup;
	}
	
	public void run() {
		//BufferStrategy bufferStrategy = canvas.getBufferStrategy();
		//int i = 0;
		//int x = 0;
		int frames = 0;
		long timer = System.currentTimeMillis();
		boolean shouldRender;
		long operatingTime, tic, toc;
		operatingTime = 0;
		
		long lastTime = System.nanoTime();
		double nanoSecondConversion = 1000000000.0 / 60; //60 frames per second
		double changeInSeconds = 0;
		
		while(true) {
			long now = System.nanoTime();
			shouldRender = false;
			changeInSeconds += (now - lastTime) / nanoSecondConversion;
			
			if(changeInSeconds < 0.95)
				try {
					Thread.sleep(1);
					//System.out.println("hi");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			tic = System.currentTimeMillis();
			while(changeInSeconds >= 1) {
				update();
				changeInSeconds = 0;
				shouldRender = true;
			}
			
			if(shouldRender) {
				render();
				frames++;
			}
			operatingTime += System.currentTimeMillis() - tic;
			if(System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println("FPS: " + frames + ", Usage: " + operatingTime / 10 + "%");
				frames = 0;
				operatingTime = 0;
			}
			lastTime = now;
		}
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		Thread gameThread = new Thread(game);
		gameThread.start();
	}
	
	public KeyBoardListener getKeyListener() {
		return keyListener;
	}
	
	public MouseEventListener getMouseListener() {
		return mouseListener;
	}
	
	public RenderHandler getRenderer() {
		return renderer;
	}

	public Tiles getTiles() {
		return tiles;
	}

	public ArrayList<GameObject> getObjects() {
		return objects;
	}

	public void setObjects(ArrayList<GameObject> objects) {
		this.objects = objects;
	}

	public GameObject getTilesGui() {
		return TilesGui;
	}

	public void setTilesGui(GameObject tilesGui) {
		TilesGui = tilesGui;
	}
	
	public void setTileGroupsGui(GameObject tileGroupsGui) {
		TileGroupsGui = tileGroupsGui;
	}
	
	public Map getMap() {
		return map;
	}
	
	/*public void setMap(Map map) {
		this.map = map;
	}*/
	
	public void setMap(File mapFile, File tileFile, String spriteFile, int xSpawn, int ySpawn) {
		player.Spawn(xSpawn, ySpawn);
		this.setMap(mapFile, tileFile, spriteFile, -1);
	}
	
	public void setMap(File mapFile, File tileFile, String spriteFile, int relocation) {
		if(tileFile != null && spriteFile != null) {
			BufferedImage newSheetImage = loadImage(spriteFile);
			sheet = new SpriteSheet(newSheetImage);
			sheet.loadSprites(tileFile);

			//Load Tiles
			tiles = new Tiles(sheet);
			
			selectedTileID = -1;
			
			TileGroupsGui = new GroupGUI(null, 5, 5, true);
			TilesGui = new TileGUI(null, 5, 5, true);
			
			setShowEditGui(false);
		}
		renderer.startFade(0, -40f, 1);
		
		mapNew = new Map(mapFile, tiles);
		mapFileNew = mapFile;
		if(relocation != -1)
			player.relocatePlayer(relocation, mapNew);
		
		//Load Map
		if(renderer.getFade() != 1 && renderer.getFade() != 2)
			loadMap();
	}
	
	public void setMap(File mapFile, File tileFile, String spriteFile, int relocation, int spawnX, int spawnY, int direction) {
		if(tileFile != null && spriteFile != null) {
			BufferedImage newSheetImage = loadImage(spriteFile);
			sheet = new SpriteSheet(newSheetImage);
			sheet.loadSprites(tileFile);

			//Load Tiles
			tiles = new Tiles(sheet);
			
			selectedTileID = -1;
			
			TileGroupsGui = new GroupGUI(null, 5, 5, true);
			TilesGui = new TileGUI(null, 5, 5, true);
			
			setShowEditGui(false);
		}
		mapNew = new Map(mapFile, tiles);
		mapFileNew = mapFile;
		renderer.startFade(0, -40f, 1);
		player.relocatePlayer(relocation, mapNew);
		player.Spawn(spawnX*tiles.getXZoom(), spawnY*tiles.getYZoom(), direction);
		
		//Load Map
		if(renderer.getFade() != 1 && renderer.getFade() != 2)
			loadMap();
	}
	
	public void gameOver() {
		setJMenuBar(null);
		setVisible(true);
		player = null;
		enemy1 = null;
		objects.clear();
		enemys.clear();
		gameLoaded = false;
		gameState = STATE.GameOver;
		Menu = new GameOverMenu(this, renderer);
	}
	
	public void loadMap() {
		map = new Map(mapFileNew, tiles);
	}
	
	public void setLayer(int layer) {
		selectedLayer = layer;
	}
	
	public int getLayer() {
		return selectedLayer;
	}
	
	public Actions getActions() {
		return actions;
	}
	
	public MenuPage getMenu() {
		return Menu;
	}
	
	public void setMenu(MenuPage menu) {
		this.Menu = menu;
	}
	
	public void setActions(Actions actions) {
		this.actions = actions;
	}
	
	public void setShowEditGui(boolean showHide) {
		showEditGui = showHide;
	}
	
	public boolean getShowEditGui() {
		return showEditGui;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public ArrayList<Enemy> getEnemys() {
		return enemys;
	}
	
	public void exitGame() {
		AL.destroy();
		System.exit(1);
	}

}
