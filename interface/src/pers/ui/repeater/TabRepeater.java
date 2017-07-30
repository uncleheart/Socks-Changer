package pers.ui.repeater;

import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import pers.data.ManageSocketData;
import pers.data.ProtocolType;
import pers.data.ProxyDataStatus;
import pers.dll.infc.JavaInvokeCPlus;
import pers.dll.infc.NoticeCallback;
import pers.ui.proxy.main.SplitIntercept;
import pers.ui.proxy.options.PanelOptions;



public class TabRepeater extends ClosableTabbedPane implements ComponentListener, MouseListener{

	Rectangle rect;
	public SplitRepeater splitRepeater;
	JPanel temp;
	int num=1;
	public TabRepeater() {
		//super(JTabbedPane.TOP);// 标签化窗格对象实例化，并将标签位置在顶端显示
		temp=new JPanel();
		temp.addMouseListener(this);

		splitRepeater=new SplitRepeater(); 

		this.add(splitRepeater, "  1  ");//添加Intercep界面
		this.add(temp,"...");
		this.addComponentListener(this);
		this.addMouseListener(this);
		
		this.setEnabledAt(this.getTabCount()-1, false);//是某个位置的选项卡不显示界面

	}

	public boolean tabAboutToClose(int tabIndex) {
		String tab = this.getTabTitleAt(tabIndex);
		int choice = JOptionPane.showConfirmDialog(null,
				"You are about to close '" + tab
						+ "'\nDo you want to proceed ?",
				"Confirmation Dialog", JOptionPane.INFORMATION_MESSAGE);

		return choice == 0; // if returned false tab closing will be
							// canceled
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

		
		if(arg0.getSource()==this){
			if(this.getTabCount()==1){
				this.remove(this.getTabCount()-1);
				this.addTab("  "+Integer.toString(++num)+"  ", new SplitRepeater());
				this.add(temp, "...");
            	this.setEnabledAt(this.getTabCount()-1, false);
				this.setSelectedIndex(0);
			}
			
			  rect = this.getBoundsAt(this.getTabCount()-1); //拿到标签的边界
			  Rectangle rect1 = this.getBoundsAt(this.getTabCount()-2); //拿到标签的边界
                if (rect.contains(arg0.getX(), arg0.getY())) { //判断是否点在边界内
                	
                	this.remove(this.getTabCount()-1);
                	this.addTab("  "+Integer.toString(++num)+"  ", new SplitRepeater());
                	
                	this.add(temp, "...");
                	this.setEnabledAt(this.getTabCount()-1, false);

                   
                }
		}
	}


	
		


}
