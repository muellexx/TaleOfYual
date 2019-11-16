package com.game.graphics.menu.bar;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import com.game.input.Actions;

public class EditMenuBar {
	
	public EditMenuBar() {
		
	}
	
	public JMenuBar buildMenuBar() {
		JMenuBar menuBar;
		JMenu menu, submenu;
		JMenuItem menuItem;
		JRadioButtonMenuItem rbMenuItem;
		//JCheckBoxMenuItem cbMenuItem;
		
		//create the menu bar
		menuBar = new JMenuBar();
		
		
		//-----File-----
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription("File");
        menuBar.add(menu);
        
        menuItem = new JMenuItem(Actions.newFile);
        menu.add(menuItem);
        
        menu.addSeparator();
		
        menuItem = new JMenuItem("Open...", KeyEvent.VK_O);
        menuItem.getAccessibleContext().setAccessibleDescription("Create a new file");
        menu.add(menuItem);
        
        menuItem = new JMenuItem(Actions.save);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Save As...");
        menuItem.getAccessibleContext().setAccessibleDescription("Create a new file");
        menu.add(menuItem);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem(Actions.quitEditor);
        menu.add(menuItem);
        
        //-----Edit-----
        menu = new JMenu ("Edit");
        menu.setMnemonic(KeyEvent.VK_E);
        menu.getAccessibleContext().setAccessibleDescription("Edit");
        menuBar.add(menu);
        
        menuItem = new JMenuItem(Actions.showTiles);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
        menu.add(menuItem);
        menu.addSeparator();
        
        submenu = new JMenu("Layer");
        submenu.setMnemonic(KeyEvent.VK_L);
        ButtonGroup group = new ButtonGroup();
        
        //rbMenuItem = new JRadioButtonMenuItem("Layer 0");
        rbMenuItem = new JRadioButtonMenuItem(Actions.setLayer0);
        group.add(rbMenuItem);
        submenu.add(rbMenuItem);
        rbMenuItem = new JRadioButtonMenuItem(Actions.setLayer1);
        rbMenuItem.setSelected(true);
        group.add(rbMenuItem);
        submenu.add(rbMenuItem);
        rbMenuItem = new JRadioButtonMenuItem(Actions.setLayer2);
        group.add(rbMenuItem);
        submenu.add(rbMenuItem);
        
        menu.add(submenu);
        
        //-----View-----
        menu = new JMenu ("View");
        menu.setMnemonic(KeyEvent.VK_V);
        menu.getAccessibleContext().setAccessibleDescription("View");
        menuBar.add(menu);
        
        menuItem = new JMenuItem(Actions.showLocation);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
        menu.add(menuItem);
        
      //-----Edit-----
        menu = new JMenu ("Edit");
        menu.setMnemonic(KeyEvent.VK_E);
        menu.getAccessibleContext().setAccessibleDescription("Edit");
        menuBar.add(menu);
        
        menuItem = new JMenuItem(Actions.showTiles);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
        menu.add(menuItem);
        menu.addSeparator();
		
		return menuBar;
	}

}
