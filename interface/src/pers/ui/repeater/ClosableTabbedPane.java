package pers.ui.repeater;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.Constructor;
import java.text.DateFormat.Field;

import javax.swing.JTabbedPane;

public class ClosableTabbedPane extends JTabbedPane{
	
	Class classRepeater;
	Field fieldIPAddress;
	Field fieldPort;
	Constructor conRepeater;
	Object obj;
	
	
	private TabCloseUI closeUI = new TabCloseUI(this);
	public void paint(Graphics g){
		super.paint(g);
		closeUI.paint(g);
	}
	
	public void addTab(String title, Component component) {
		super.addTab(title+"  ", component);
	}
	
	
	public String getTabTitleAt(int index) {
		return super.getTitleAt(index).trim();
	}
	
	private class TabCloseUI implements MouseListener, MouseMotionListener {
		 Rectangle rect1;
		 Rectangle rect2;
		private ClosableTabbedPane  tabbedPane;
		private int closeX = 0 ,closeY = 0, meX = 0, meY = 0;
		private int selectedTab;
		private final int  width = 8, height = 8;
		private Rectangle rectangle = new Rectangle(0,0,width, height);
		private TabCloseUI(){}
		public TabCloseUI(ClosableTabbedPane pane) {
			
			tabbedPane = pane;
			tabbedPane.addMouseMotionListener(this);
			tabbedPane.addMouseListener(this);
		}
		public void mouseEntered(MouseEvent me) {}
		public void mouseExited(MouseEvent me) {}
		public void mousePressed(MouseEvent me) {}
		public void mouseClicked(MouseEvent me) {}
		public void mouseDragged(MouseEvent me) {}
		
		

		public void mouseReleased(MouseEvent me) {

			Boolean t=false;
			rect1 = tabbedPane.getBoundsAt(tabbedPane.getTabCount()-2); //拿到标签的边界
			
			if(rect1.contains(me.getX(), me.getY())){
				t=true;
			}
			rect1 = tabbedPane.getBoundsAt(tabbedPane.getTabCount()-2); 
			//放在最后一个的时候不显示X
			if((tabbedPane.getTabCount())>1 && (tabbedPane.getBoundsAt(tabbedPane.getTabCount()-1)).contains(me.getX(), me.getY())){
				tabbedPane.setEnabledAt(tabbedPane.getTabCount()-1, false);
				return;
			}
			if(closeUnderMouse(me.getX(), me.getY())){
				boolean isToCloseTab = tabAboutToClose(selectedTab);
				if (isToCloseTab && selectedTab > -1){			
					tabbedPane.removeTabAt(selectedTab);
				}
				if(t && tabbedPane.getTabCount()>=2){
						tabbedPane.setSelectedIndex(tabbedPane.getTabCount()-2);
						tabbedPane.setEnabledAt(tabbedPane.getTabCount()-1, false);
					
				}
				selectedTab = tabbedPane.getSelectedIndex();
			}
			t=false;
		}

		public void mouseMoved(MouseEvent me) {	
			meX = me.getX();
			meY = me.getY();			
			if(mouseOverTab(meX, meY)){
				controlCursor();
				tabbedPane.repaint();
			}
		}

		private void controlCursor() {
			if(tabbedPane.getTabCount()>0)
				if(closeUnderMouse(meX, meY)){
					tabbedPane.setCursor(new Cursor(Cursor.HAND_CURSOR));	
					if(selectedTab > -1)
						tabbedPane.setToolTipTextAt(selectedTab, "Close " +tabbedPane.getTitleAt(selectedTab));
				}
				else{
					tabbedPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					if(selectedTab > -1)
						tabbedPane.setToolTipTextAt(selectedTab,"");
				}	
		}

		private boolean closeUnderMouse(int x, int y) {		
			rectangle.x = closeX;
			rectangle.y = closeY;
			return rectangle.contains(x,y);
		}

		public void paint(Graphics g) {
			
			int tabCount = tabbedPane.getTabCount();
			for(int j = 0; j < tabCount; j++)
				if(tabbedPane.getComponent(j).isShowing()){			
					int x = tabbedPane.getBoundsAt(j).x + tabbedPane.getBoundsAt(j).width -width-5;
					int y = tabbedPane.getBoundsAt(j).y +5;	
					drawClose(g,x,y);
					break;
				}
			if(mouseOverTab(meX, meY)){
				drawClose(g,closeX,closeY);
			}
		}

		private void drawClose(Graphics g, int x, int y) {
			if(tabbedPane != null && tabbedPane.getTabCount() > 0){
				Graphics2D g2 = (Graphics2D)g;				
				drawColored(g2, isUnderMouse(x,y)? Color.RED : Color.WHITE, x, y);
			}
		}

		private void drawColored(Graphics2D g2, Color color, int x, int y) {
			g2.setStroke(new BasicStroke(5,BasicStroke.JOIN_ROUND,BasicStroke.CAP_ROUND));
			g2.setColor(Color.BLACK);
			g2.drawLine(x, y, x + width, y + height);
			g2.drawLine(x + width, y, x, y + height);
			g2.setColor(color);
			g2.setStroke(new BasicStroke(3, BasicStroke.JOIN_ROUND, BasicStroke.CAP_ROUND));
			g2.drawLine(x, y, x + width, y + height);
			g2.drawLine(x + width, y, x, y + height);

		}

		private boolean isUnderMouse(int x, int y) {
			if(Math.abs(x-meX)<width && Math.abs(y-meY)<height )
				return  true;		
			return  false;
		}

		private boolean mouseOverTab(int x, int y) {
			int tabCount = tabbedPane.getTabCount();
			for(int j = 0; j < tabCount-1; j++)
				if(tabbedPane.getBoundsAt(j).contains(meX, meY)){
					selectedTab = j;
					closeX = tabbedPane.getBoundsAt(j).x + tabbedPane.getBoundsAt(j).width -width-5;
					closeY = tabbedPane.getBoundsAt(j).y +5;					
					return true;
				}
			return false;
		}

	}

	public boolean tabAboutToClose(int tabIndex) {
		return true;
	}

	
}