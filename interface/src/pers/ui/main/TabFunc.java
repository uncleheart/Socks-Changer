package pers.ui.main;


import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import pers.ui.proxy.main.TabProxy;
import pers.ui.repeater.TabRepeater;

public class TabFunc extends JTabbedPane{
	TabProxy tabProxy;
	TabRepeater tabRepeater;
	JPanel panelProxy;
	JPanel panelRepeater;
	public TabFunc()
	{
		super(JTabbedPane.TOP);//标签化窗格对象实例化，并将标签位置在顶端显示
		panelProxy=new JPanel(new GridLayout(1,1));
		panelRepeater=new JPanel(new GridLayout(1,1));
		
		tabProxy=new TabProxy();
		panelProxy.add(tabProxy);
		
		tabRepeater=new TabRepeater();
		panelRepeater.add(tabRepeater);
		
		this.add(panelProxy,"proxy");
		this.add(panelRepeater,"Repeater");
	}

	
}
