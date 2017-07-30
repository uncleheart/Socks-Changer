package pers.ui.main;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import bibliothek.gui.dock.common.action.predefined.CCloseAction.Action;
import pers.ui.infc.MenuInfc;

public class ActionMenu extends JMenuBar{

	protected MenuInfc menuInfc;
	public ActionMenu(MenuInfc tMenuInfc)
	{
		menuInfc=tMenuInfc;
		showJMenuBar();
	}
	public void showJMenuBar(){
		JMenu menu1=new JMenu("menu1");
		JMenu menu2=new JMenu("menu2");
		this.add(menu1);
		this.add(menu2);
		
		
		
	}
	
	
}
