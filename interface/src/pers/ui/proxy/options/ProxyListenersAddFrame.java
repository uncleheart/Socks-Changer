package pers.ui.proxy.options;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;



public class ProxyListenersAddFrame extends JFrame implements WindowListener, MouseListener{


	Container contentPane ;
	JTabbedPane tabblepane;
	JPanel panelBinding;
	JPanel panelRequestHandling;
	JPanel panleCertificate;
	JButton buttonOK;
	JButton buttonCancel;
	JPanel panelSetButton;
	
	JLabel labelBindingExplain;//说明性文字
	JLabel labelBindingPort;
	JLabel LabelBindingAddress;
	JTextArea textPort;//输入端口的文本域
	JTextArea textAddress;//输入地址的文本域
	JRadioButton radioOnly;
	JRadioButton radioAll;
	JRadioButton radioSpec;
	
	
	PanelOptions panelOptionPointer;
		public ProxyListenersAddFrame(PanelOptions panelOptions){	
		panelOptionPointer=panelOptions;

		
		contentPane = this.getContentPane();// 获取JFrame默认的内容窗格
		contentPane.setLayout(new BorderLayout());
		
		tabblepane=new JTabbedPane(JTabbedPane.TOP);
		panelBinding=new JPanel(new GridLayout(10,1));
		panelRequestHandling=new JPanel(new BorderLayout());
		panleCertificate=new JPanel(new BorderLayout());
		
		Box boxBinding1=Box.createHorizontalBox();
		Box boxBinding2=Box.createHorizontalBox();
		Box boxBinding3=Box.createHorizontalBox();
		Box boxBinding4=Box.createHorizontalBox();
		Box boxBinding5=Box.createHorizontalBox();
		
		
		labelBindingExplain=new JLabel("说明性文字");
		labelBindingPort=new JLabel("Bind to Port");
		LabelBindingAddress=new JLabel("Bing to address");
		textPort=new JTextArea();//输入端口的文本域
		textAddress=new JTextArea();//输入端口的文本域
		radioOnly=new JRadioButton("Loopback only",true);
		radioAll=new JRadioButton("All interfaces");
		radioSpec=new JRadioButton("Specific address:");
		ButtonGroup group=new ButtonGroup();
		group.add(radioOnly);
		group.add(radioAll);
		group.add(radioSpec);
		
		boxBinding1.add(labelBindingExplain);
		
		boxBinding2.add(labelBindingPort);
		boxBinding2.add(Box.createHorizontalStrut(20));
		boxBinding2.add(textPort);
		
		boxBinding3.add(LabelBindingAddress);
		boxBinding3.add(radioOnly);
		
		boxBinding4.add(Box.createHorizontalStrut(100));
		boxBinding4.add(radioAll);
		
		boxBinding5.add(Box.createHorizontalStrut(100));
		boxBinding5.add(radioSpec);
		boxBinding5.add(textAddress);
		
		panelBinding.add(boxBinding1);
		panelBinding.add(boxBinding2);
		panelBinding.add(boxBinding3);
		panelBinding.add(boxBinding4);
		panelBinding.add(boxBinding5);
		
		
		tabblepane.add(panelBinding,"Binding");
		tabblepane.add(panelRequestHandling,"Request Handling");
		tabblepane.add(panleCertificate,"Certificate");
		
		//两个按钮
		panelSetButton=new JPanel();
		buttonOK=new JButton("OK");
		buttonCancel=new JButton("Cancel");
		panelSetButton.add(buttonOK);
		panelSetButton.add(buttonCancel);
		
		buttonOK.addMouseListener(this);//为按钮添加点击监听器
		buttonCancel.addMouseListener(this);
		
		contentPane.add(tabblepane,BorderLayout.CENTER);
		contentPane.add(panelSetButton,BorderLayout.SOUTH);
		

		this.setSize(800, 600);
		this.setLocationRelativeTo(null); // 设置窗口位置在屏幕中间
		this.setVisible(true);
		this.setResizable(false);//设置不可改变大小
	}


	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowClosing(WindowEvent arg0) {//点击关闭窗口时的事件
		// TODO Auto-generated method stub
		System.exit(0);
	}


	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowOpened(WindowEvent arg0) {
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
		

	}
	
}
