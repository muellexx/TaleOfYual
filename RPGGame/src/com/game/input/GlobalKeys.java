package com.game.input;

import com.game.Game;
import com.game.Game.STATE;

public class GlobalKeys {
	
	public static void update(Game game) {
		KeyBoardListener keyListener = game.getKeyListener();
		if(keyListener.escOnce()) {
			game.setJMenuBar(null);
			game.setVisible(true);
			if(Game.gameState == STATE.Game || Game.gameState == STATE.Edit || Game.gameState == STATE.GameOver) {
				Game.gameState = STATE.Menu;
			}else if(Game.gameState == STATE.Menu) {
				game.exitGame();
			}
		}
	}

}
