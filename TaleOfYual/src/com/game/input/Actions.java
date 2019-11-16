package com.game.input;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.game.Game;
import com.game.Game.STATE;
import com.game.graphics.Sprite;
import com.game.graphics.gui.GUIButton;
import com.game.graphics.gui.GroupGUI;
import com.game.graphics.gui.SDKButtonGroup;
import com.game.graphics.gui.TileGUI;
import com.game.logic.Map;
import com.game.logic.Tiles;
import com.game.utils.GameObject;
import com.game.utils.Rectangle;

public class Actions {
	
	private Game game;
	public static Action newFile, open, save, saveAs, quitEditor; //File
	public static Action showTiles, setLayer0, setLayer1, setLayer2; //Edit
	public static Action showLocation; //View
	
	public Actions(Game game) {
		
		this.game = game;
		
		//File
		newFile =   new NewFile("New", "Create a new file", KeyEvent.VK_N);
		quitEditor = new QuitEditor("Quit Editor", "Quit Editor and go back to Main Menu", KeyEvent.VK_Q);
		
		//Edit
		setLayer0 = new SetLayer("Layer 0", "Set Layer to 0", null, 0);
		setLayer1 = new SetLayer("Layer 1", "Set Layer to 1", null, 1);
		setLayer2 = new SetLayer("Layer 2", "Set Layer to 2", null, 2);
		showTiles = new ShowTiles("Show/Hide Tiles List","Show/Hide available Tiles", KeyEvent.VK_T);
		save = new Save("Save","Save the map",KeyEvent.VK_S);
		
		//View
		showLocation = new ShowLocation("Show Player Location","Show the current Location of the Player", KeyEvent.VK_P);
	}
	
	public class NewFile extends AbstractAction{
		public NewFile(String text, String desc, Integer mnemonic) {
			super(text);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e) {}
	}
	
	public class QuitEditor extends AbstractAction{
		public QuitEditor(String text, String desc, Integer mnemonic) {
			super(text);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e) {
			game.setJMenuBar(null);
			game.setVisible(true);
			Game.gameState = STATE.Menu;
		}
	}
	
	public class SetLayer extends AbstractAction{
		private int layer;
		public SetLayer(String text, String desc, Integer mnemonic, int layer) {
			super(text);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
			this.layer = layer;
		}
		public void actionPerformed(ActionEvent e) {
			game.setLayer(layer);
		}
	}
	
	public class ShowTiles extends AbstractAction{
		public ShowTiles(String text, String desc, Integer mnemonic) {
			super(text);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e) {
			//Load SDK GUI
			Tiles tiles = game.getTiles();
			GameObject tileGroupsGui;
			GameObject tilesGui;
			
			if(!game.getShowEditGui()) {
				GUIButton[] buttons = new GUIButton[tiles.numberOfGroups()];
				Sprite[] tileSprites = tiles.getGroupSprites();
				int xBorder = 6;
				int yBorder = 6;

				for(int i = 0; i < buttons.length; i++) {
					Rectangle tileRectangle = new Rectangle(0, i*(tiles.getTileHeight()*tiles.getYZoom() + 2*yBorder + 3), tiles.getTileWidth()*tiles.getXZoom(),
							tiles.getTileHeight()*tiles.getYZoom());
					buttons[i] = new SDKButtonGroup(game, i, tileSprites[i], tileRectangle, xBorder, yBorder);
				}

				tileGroupsGui = new GroupGUI(buttons, 5, 5, true);
				tilesGui = new TileGUI(null, 5, 5, true);
								
				game.setShowEditGui(true);
			}else {
				tileGroupsGui = new GroupGUI(null, 5, 5, true);
				tilesGui = new TileGUI(null, 5, 5, true);
				
				game.setShowEditGui(false);
			}
			game.setTileGroupsGui(tileGroupsGui);
			game.setTilesGui(tilesGui);
		}
	}
	
	public class ShowLocation extends AbstractAction{
		public ShowLocation(String text, String desc, Integer mnemonic) {
			super(text);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e) {
			Rectangle playerRectangle = game.getPlayer().getRectangle();
			System.out.println("Player Location: x: " + playerRectangle.x + ", y: " + playerRectangle.y);
		}
	}
	
	public class Save extends AbstractAction{
		public Save(String text, String desc, Integer mnemonic) {
			super(text);
			putValue(SHORT_DESCRIPTION, desc);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent e) {
			Map map = game.getMap();
			map.saveMap();
		}
	}
}
