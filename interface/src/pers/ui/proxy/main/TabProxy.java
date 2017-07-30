package pers.ui.proxy.main;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import pers.data.InterceptStatus;
import pers.data.ManageSocketData;
import pers.data.ProtocolType;
import pers.data.ProxyDataStatus;
import pers.dll.infc.JavaInvokeCPlus;
import pers.dll.infc.NewConData;
import pers.dll.infc.NoticeCallback;
import pers.ui.proxy.options.PanelOptions;

public class TabProxy extends JTabbedPane implements ComponentListener, NoticeCallback {

	public JavaInvokeCPlus javaInvokeCPlus;
	private ManageSocketData manageSocketData;
	
	
	public SplitIntercept splitIntercept;// 拦截的界面
	PanelOptions panelInterceptOptions;
	JScrollPane scrollInterceptOptions;
	JToolBar toolBar;// 创建工具栏
	JButton bSend;// 发送按钮
	JButton bPause;// 开关按钮

	public TabProxy() {
		super(JTabbedPane.TOP);// 标签化窗格对象实例化，并将标签位置在顶端显示

		panelInterceptOptions = new PanelOptions();// 为第2项创建内容窗格
		scrollInterceptOptions=new JScrollPane(panelInterceptOptions);
		
		scrollInterceptOptions.getVerticalScrollBar().setUnitIncrement(30);//设置滚轮速度

		manageSocketData=new ManageSocketData();
		javaInvokeCPlus=new JavaInvokeCPlus(this);
		javaInvokeCPlus.StartServer(panelInterceptOptions.textFieldProxyListenersIP.getText(), Integer.parseInt(panelInterceptOptions.textFieldProxyListenersPort.getText()));
		splitIntercept=new SplitIntercept(manageSocketData,javaInvokeCPlus); 
		
		this.add(splitIntercept, "Intercep");//添加Intercep界面
		this.add(scrollInterceptOptions, "Options");// 添加Options界面，功能再说
		this.addComponentListener(this);
		

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


	//接收到新的数据  
	//!!!注意  在调用前需要先设置该函数签名
	@Override
	public int NoticeNewDatafromDst(int tIndex, ProxyDataStatus tProxyDataStatus, byte[] tData) {
		String res = new String(tData);
		String s="index:"+tIndex+" type:"+tProxyDataStatus+"  data:"+res;
//		System.out.println(s);

		byte[] newData=panelInterceptOptions.HandleNewData(tIndex,tProxyDataStatus,tData,manageSocketData.GetSocketInfoEstablish(tIndex));
		int tLocalIndex=manageSocketData.NoticeNewDatafromDst(tIndex, tProxyDataStatus, newData);
		splitIntercept.NoticeNewDatafromDst(tIndex,tLocalIndex, tProxyDataStatus, newData);
		
		return 0;
	}   //char[4]	
	
	//接收到新的数据  
	//!!!注意  在调用前需要先设置该函数签名
	@Override
	public int NoticeNewDatafromSource(int tIndex, ProxyDataStatus tProxyDataStatus, byte[] tData) {
		String res = new String(tData);
		String s="index:"+tIndex+" type:"+tProxyDataStatus+"  data:"+res;
//		System.out.println(s);
		
//		tData=panelInterceptOptions.HandleNewData(tIndex,tProxyDataStatus,tData,manageSocketData.GetSocketInfoEstablish(tIndex));
		int tLocalIndex=manageSocketData.NoticeNewDatafromSource(tIndex, tProxyDataStatus, tData);
		splitIntercept.NoticeNewDatafromSource(tIndex,tLocalIndex, tProxyDataStatus, tData);
		
		return 0;
	}   //char[4]	
	
	
	
	//接收到新的请求，开始建立连接  
	//!!!注意  在调用前需要先设置该函数签名
	@Override
	public int  NoticeNewCon(NewConData newConData) {
		InterceptStatus tInterceptStatus=panelInterceptOptions.HandleNewCon( newConData);
		int res=panelInterceptOptions.HandleChange(newConData);
		manageSocketData.NoticeNewCon(newConData,tInterceptStatus);
		splitIntercept.NoticeNewCon(newConData.Index, newConData.DstHostname, newConData.DstIPAddress, newConData.DstPort, newConData.protocolType);
		return tInterceptStatus.GetStatus()|res;
	}
	@Override
	public int NoticeClose(int tIndex) {
		// TODO Auto-generated method stub
		manageSocketData.NoticeClose(tIndex);
		splitIntercept.NoticeClose(tIndex);
		return 1;
	}

	
		


}
