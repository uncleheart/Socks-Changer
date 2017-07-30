package pers.ui.main;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import pers.ui.infc.MenuInfc;

public class MainFrame extends JFrame implements MenuInfc{

	TabFunc tabFunc;
	ActionMenu myMenuBar ;
	Container contentPane ;
	public MainFrame() {

		this.setTitle("Socks Changer");
		contentPane = this.getContentPane();// 获取JFrame默认的内容窗格
		contentPane.setLayout(null);// 设置内容窗格为空

		myMenuBar = new ActionMenu(this);// 实例化菜单栏
		
		
		//这里先注释掉了，之后加菜单时候取消注释就行了
		//this.setJMenuBar((JMenuBar)myMenuBar);
		
		tabFunc = new TabFunc();

		contentPane.add(tabFunc);

		this.addComponentListener(new ComponentAdapter() {// 添加监听事件监听窗口的变化
			public void componentResized(ComponentEvent e) {

				tabFunc.setBounds(10, 10, getWidth() - 50, getHeight() - 50);// 窗口变化后重新设置大小



			}
		});

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// 设置关闭窗口的动作，若果没有这一句
																// 关闭窗口后内存不会释放
		this.setSize(1250, 800);
		this.setMinimumSize(new Dimension(850,500));
		this.setLocationRelativeTo(null); // 设置窗口位置在屏幕中间
		this.setVisible(true);
		tabFunc.setBounds(10, 10, getWidth() - 50, getHeight() - 50);// 设置大小

	}

	
	
	
}
