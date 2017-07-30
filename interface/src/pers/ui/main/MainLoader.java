package pers.ui.main;


import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.InsetsUIResource;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;
import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import org.jvnet.substance.*;
import org.jvnet.substance.skin.*;
import org.jvnet.substance.theme.SubstanceLightAquaTheme;
import org.jvnet.substance.watermark.SubstanceBubblesWatermark;

import pers.data.ManageSocketData;
import pers.data.ProtocolType;
import pers.data.SocketInfo;
import pers.dll.infc.*;

//import UncleHeart.DLL.Interface.JavaInvokeCPlus;

public class MainLoader {

	
	private MainFrame mainFrame;
	public MainLoader() {
		// TODO Auto-generated constructor stub


		mainFrame=new MainFrame();
	}
	public static void main(String[] args) throws InterruptedException {
		 
		
		try
	    {
			
			BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.osLookAndFeelDecorated;
	        org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
	        Border bd = new org.jb2011.lnf.beautyeye.ch8_toolbar.BEToolBarUI.ToolBarBorder(
	                UIManager.getColor("ToolBar.shadow")     //Floatable时触点的颜色
	                , UIManager.getColor("ToolBar.highlight")//Floatable时触点的阴影颜色
	                , new Insets(6, 0, 11, 0));              //border的默认insets

	        UIManager.put("RootPane.setupButtonVisible",false);
	      
	        
	        //设置JTabbedPane的缩进
	        UIManager.put("TabbedPane.tabAreaInsets" , new javax.swing.plaf.InsetsUIResource(0,0,0,0));
	        //UIManager.put("TabbedPane.contentBorderInsets", new InsetsUIResource(0,0,2,0));//TabbedPane那里的虚线，上下的空格，最左的空距离，，最右的空距离
	        UIManager.put("TabbedPane.tabInsets", new InsetsUIResource(1,10,9,10));//TabbedPane面板的大小，第1第3个数控制上下的，第2和第4的数控制左右的
	        UIManager.put("FileChooser.useSystemExtensionHiding", false);
	    }
		
	    catch(Exception e)
	    {
	        //TODO exception
	    }
		
		new MainLoader();
		


	}

	
	
	
	
	

	
	
	
	
	
	
	
	
	
}
