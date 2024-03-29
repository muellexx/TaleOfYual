package com.game.logic;

import java.util.HashMap;
import java.util.Map;

import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class AudioPlayer {
	
	public static Map<String, Sound> soundMap = new HashMap<String, Sound>();
	public static Map<String, Music> musicMap = new HashMap<String,  Music>();
	
	public static void load() {
		
		try {
			soundMap.put("hit", new Sound("res/Sound/SF/hit1.wav"));
			musicMap.put("music", new Music("res/Sound/Music/ThemeSong.ogg"));
		} catch (SlickException e) {
			e.printStackTrace();
		}
		
	}
	
	public static Music getMusic(String key) {
		return musicMap.get(key);
	}
	
	public static Sound getSound(String key) {
		return soundMap.get(key);
	}

}