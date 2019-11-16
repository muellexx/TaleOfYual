package com.game.input;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.game.Game;

public class KeyBoardListener implements KeyListener, FocusListener{
	
	public boolean[] keys = new boolean[120];
	public boolean[] pulse = new boolean[120];
	
	private Game game;
	
	public KeyBoardListener(Game game) {
		this.game = game;
		for(int i = 0; i < pulse.length; i++)
			pulse[i] = false;
	}

	public void keyPressed(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if(keyCode < keys.length)
			keys[keyCode] = true;
		
		if(keys[KeyEvent.VK_CONTROL])
			game.handleCTRL(keys);
	}

	public void keyReleased(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if(keyCode < keys.length)
			keys[keyCode] = false;
	}

	public void focusLost(FocusEvent event) {
		for(int i = 0; i < keys.length; i++)
			keys[i] = false;
	}

	public void keyTyped(KeyEvent event) {
		
	}
	
	public void focusGained(FocusEvent event) {}
	
	public boolean up() {
		return keys[KeyEvent.VK_W] || keys[KeyEvent.VK_UP];
	}
	
	public boolean down() {
		if(keys[KeyEvent.VK_CONTROL]) return false;
		else return keys[KeyEvent.VK_S] || keys[KeyEvent.VK_DOWN];
	}
	
	public boolean left() {
		return keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT];
	}
	
	public boolean right() {
		return keys[KeyEvent.VK_D] || keys[KeyEvent.VK_RIGHT];
	}
	
	public boolean escOnce() {
		return pulse(KeyEvent.VK_ESCAPE);
	}
	
	public boolean upOnce() {
		return pulse(KeyEvent.VK_UP);
	}
	
	public boolean downOnce() {
		return pulse(KeyEvent.VK_DOWN);
	}
	
	public boolean enterOnce() {
		return pulse(KeyEvent.VK_ENTER);
	}
	
	
	
	public boolean pulse(int keyCode) {
		if(keyCode >= keys.length)
			return false;
		
		boolean result = keys[keyCode];
		
		if(result == true && pulse[keyCode] == false)
			pulse[keyCode] = true;
		else if(result == true && pulse[keyCode] == true)
			result = false;
		else pulse[keyCode] = false;
		
		return result;
	}
	
}
