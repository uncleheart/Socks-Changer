package pers.ui.proxy.main;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.BasicIconFactory;
import javax.swing.table.*;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import org.omg.CORBA.ARG_OUT;

import pers.data.*;
import pers.dll.infc.JavaInvokeCPlus;
import pers.ui.basic.BasicEditor;
import pers.ui.basic.EditorGUI;

public class SplitIntercept extends JSplitPane implements ComponentListener, MouseListener, ActionListener {
	ManageSocketData manageSocketData;
	JavaInvokeCPlus javaInvokeCPlus;
	
	BasicEditor basicEditor;
	protected JPopupMenu popupMenuSource;
	protected JMenuItem menuItemSourceMerge;//整合
	protected JMenuItem menuItemSourceDelete;//删除
	protected JMenuItem menuItemSourceNew;//新建
	protected JMenuItem menuItemSourceSend;//发送
	
	protected JPopupMenu popupMenuDst;
	protected JMenuItem menuItemDstMerge;//整合
	protected JMenuItem menuItemDstDelete;//删除
	protected JMenuItem menuItemDstNew;//新建
	protected JMenuItem menuItemDstSend;//发送
	
	protected JPopupMenu popupMenuEstablished;
	protected JPopupMenu popupMenuClose;
	protected JMenuItem menuItemEstablishedClose;
	JCheckBoxMenuItem menuItemEstablishedInterceptSend;
	JCheckBoxMenuItem menuItemEstablishedInterceptRecv;
	protected JMenuItem menuItemCloseClose;
	
	JToolBar toolBar;// 创建工具栏
	JPanel panelContentEditor;// 为右面部分创建一个内容窗格 包含编辑器与历史
	JButton bSend;// 发送按钮
	JButton bPause;// 开关按钮
	JButton bFileImport;// 导入文件
	JButton bFileExport;// 导出文件
	
	JFileChooser fileChooser;//=new JFileChooser();//文件对话框
	
	public SplitIntercept(ManageSocketData tManageSocketData,JavaInvokeCPlus tJavaInvokeCPlus) {
		super(JSplitPane.HORIZONTAL_SPLIT, true);
		manageSocketData = tManageSocketData;
		javaInvokeCPlus=tJavaInvokeCPlus;
		InitSocketStatus();
		basicEditor=new BasicEditor(tManageSocketData);
		toolBar = new JToolBar(JToolBar.VERTICAL);// 建立工具栏
		fileChooser=new JFileChooser();
		bSend = new JButton("发送");
		bFileImport = new JButton("导入");
		bFileExport = new JButton("导出");
		
		toolBar.add(bSend);
		toolBar.add(bFileImport);
		toolBar.add(bFileExport);
		
		bSend.addActionListener(this);
		bFileImport.addActionListener(this);
		bFileExport.addActionListener(this);
		
		
		basicEditor.add(toolBar, BorderLayout.EAST);
		this.setLeftComponent(tabSocketStatus);
		this.setRightComponent(basicEditor);
		this.addComponentListener(this);


		popupMenuSource=new JPopupMenu();//的弹出式菜单
		menuItemSourceMerge=new JMenuItem("Merge");
		menuItemSourceDelete=new JMenuItem("Delete");
		menuItemSourceNew=new JMenuItem("New");
		menuItemSourceSend=new JMenuItem("Send");
		
		menuItemSourceMerge.addActionListener(this);
		menuItemSourceDelete.addActionListener(this);
		menuItemSourceNew.addActionListener(this);
		menuItemSourceSend.addActionListener(this);
		
		popupMenuSource.add(menuItemSourceSend);
		popupMenuSource.add(menuItemSourceMerge);
		popupMenuSource.add(menuItemSourceDelete);
		popupMenuSource.add(menuItemSourceNew);

		
		basicEditor.scollSource.addMouseListener( this);
		basicEditor.tableSource.addMouseListener( this);
		basicEditor.scollDst.addMouseListener( this);
		basicEditor.tableDst.addMouseListener( this);
		
		popupMenuDst=new JPopupMenu();//的弹出式菜单
		menuItemDstMerge=new JMenuItem("Merge");
		menuItemDstDelete=new JMenuItem("Delete");
		menuItemDstNew=new JMenuItem("New");
		menuItemDstSend=new JMenuItem("Send");
		
		popupMenuDst.add(menuItemDstSend);
		popupMenuDst.add(menuItemDstMerge);
		popupMenuDst.add(menuItemDstDelete);
		popupMenuDst.add(menuItemDstNew);

		
		menuItemDstMerge.addActionListener(this);
		menuItemDstDelete.addActionListener(this);
		menuItemDstNew.addActionListener(this);
		menuItemDstSend.addActionListener(this);
		
	}

	// 套接字状态 的分页
	private JTabbedPane tabSocketStatus;

	protected JPanel panelEstablished; // 进行中的socket列表
	protected JScrollPane scrollEstablished;
	protected Vector vectorDataEstablished = new Vector();
	protected Vector vectorNameEstablished = new Vector();
	protected TableModel tabModelEstablished;
	protected JTable tableEstablished;
	protected RowSorter<TableModel> rowSorterEstablished;

	protected JPanel panelClose; // 已经关闭的socket列表
	protected DefaultListModel<String> listModelClose;
	protected JScrollPane scrollClose;
	protected JList listClose;
	protected Vector vectorDataClose = new Vector();
	protected Vector vectorNameClose = new Vector();
	protected TableModel tabModelClose;
	protected JTable tableClose;
	protected RowSorter<TableModel> rowSorterClose;


	
	private void InitSocketStatus() {
		tabSocketStatus = new JTabbedPane(JTabbedPane.TOP);

		vectorNameEstablished.add("Index");
		vectorNameEstablished.add("Host");
		vectorNameEstablished.add("IP");
		vectorNameEstablished.add("Port");
		vectorNameEstablished.add("Protocol");

		tabModelEstablished = new DefaultTableModel(vectorDataEstablished, vectorNameEstablished);
		tableEstablished = new JTable(tabModelEstablished) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tableEstablished.setPreferredScrollableViewportSize(new Dimension(600, 100));// 设置表格的大小
		tableEstablished.setRowHeight(30);// 设置每行的高度为20
		tableEstablished.setRowHeight(0, 20);// 设置第1行的高度为15
		tableEstablished.setRowMargin(5);// 设置相邻两行单元格的距离
		tableEstablished.setSelectionBackground(Color.white);// 设置所选择行的背景色
		tableEstablished.setSelectionForeground(Color.red);// 设置所选择行的前景色
		tableEstablished.setGridColor(Color.black);// 设置网格线的颜色
		tableEstablished.setShowHorizontalLines(false);// 是否显示水平的网格线
		tableEstablished.setShowVerticalLines(true);// 是否显示垂直的网格线
		tableEstablished.doLayout();
		tableEstablished.setBackground(Color.lightGray);
		tableEstablished.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rowSorterEstablished = new TableRowSorter<TableModel>(tabModelEstablished); // 排序
		tableEstablished.setRowSorter(rowSorterEstablished);

		
		// JTable最好加在JScrollPane上
		scrollEstablished = new JScrollPane(tableEstablished, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panelEstablished = new JPanel();
		panelEstablished.setLayout(new BorderLayout());
		panelEstablished.add(scrollEstablished, BorderLayout.CENTER);
		tabSocketStatus.add(panelEstablished, "Establish");

		tableEstablished.updateUI();

		// 隐藏index
		TableColumnModel columnModelEstablished = tableEstablished.getColumnModel();
		TableColumn columnEstablished = columnModelEstablished.getColumn(0);
		columnEstablished.setMinWidth(0);
		columnEstablished.setMaxWidth(0);
		

		
		tableEstablished.addMouseListener(this); // 添加监听器
		scrollEstablished.addMouseListener(this); // 添加监听器
		

		// 开始close的界面

		vectorNameClose.add("Index");
		vectorNameClose.add("Host");
		vectorNameClose.add("IP");
		vectorNameClose.add("Port");
		vectorNameClose.add("Protocol");

		tabModelClose = new DefaultTableModel(vectorDataClose, vectorNameClose);
		tableClose = new JTable(tabModelClose) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			public Class getColumnClass(int column) {
				Class returnValue;
				if ((column >= 0) && (column < getColumnCount())) {
					returnValue = getValueAt(0, column).getClass();
				} else {
					returnValue = Object.class;
				}
				return returnValue;
			}
		};

		tableClose.setPreferredScrollableViewportSize(new Dimension(600, 100));// 设置表格的大小
		tableClose.setRowHeight(30);// 设置每行的高度为20
		tableClose.setRowHeight(0, 20);// 设置第1行的高度为15
		tableClose.setRowMargin(5);// 设置相邻两行单元格的距离
		tableClose.setSelectionBackground(Color.white);// 设置所选择行的背景色
		tableClose.setSelectionForeground(Color.red);// 设置所选择行的前景色
		tableClose.setGridColor(Color.black);// 设置网格线的颜色
		tableClose.setShowHorizontalLines(false);// 是否显示水平的网格线
		tableClose.setShowVerticalLines(true);// 是否显示垂直的网格线
		tableClose.doLayout();
		tableClose.setBackground(Color.lightGray);
		tableClose.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rowSorterClose = new TableRowSorter<TableModel>(tabModelClose); // 排序
		tableClose.setRowSorter(rowSorterClose);

		tableClose.addMouseListener(this); // 添加监听器
		// JTable最好加在JScrollPane上
		scrollClose = new JScrollPane(tableClose, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panelClose = new JPanel();
		panelClose.setLayout(new BorderLayout());
		panelClose.add(scrollClose, BorderLayout.CENTER);

		TableColumnModel columnModelClose = tableClose.getColumnModel();
		TableColumn columnClose = columnModelClose.getColumn(0);
		columnClose.setMinWidth(0);
		columnClose.setMaxWidth(0);

		
		tabSocketStatus.add(panelClose, "Close");
		tabSocketStatus.setMinimumSize(new Dimension(200,0));
		
		tableClose.addMouseListener(this);
		scrollClose.addMouseListener(this);

		popupMenuEstablished=new JPopupMenu();//Established的弹出式菜单
		menuItemEstablishedClose=new JMenuItem("Close");
		menuItemEstablishedInterceptSend=new JCheckBoxMenuItem("Intercept Send",true);
		menuItemEstablishedInterceptRecv=new JCheckBoxMenuItem("Intercept Recv",true);
		popupMenuEstablished.add(menuItemEstablishedClose);
		popupMenuEstablished.add(menuItemEstablishedInterceptSend);
		popupMenuEstablished.add(menuItemEstablishedInterceptRecv);
		menuItemEstablishedClose.addActionListener(this);
		menuItemEstablishedInterceptSend.addActionListener(this);
		menuItemEstablishedInterceptRecv.addActionListener(this);
		
		popupMenuClose=new JPopupMenu();//Established的弹出式菜单
		menuItemCloseClose=new JMenuItem("Close");
		popupMenuClose.add(menuItemCloseClose);
		
	}

	public void NoticeNewCon(int tIndex, String hostname, String ipAddress, int port, ProtocolType tProtocolType) {
		Vector vTmp = new Vector();
		vTmp.add(tIndex);
		vTmp.add(hostname);
		vTmp.add(ipAddress);
		vTmp.add(port);
		vTmp.add(tProtocolType.GetString());
		vectorDataEstablished.add(vTmp);
		tableEstablished.updateUI();
	}
	
	public int NoticeNewDatafromDst(int tIndex, int tDstIndex, ProxyDataStatus tProxyDataStatus, byte[] tData) {
		if (basicEditor.NowSocketIndex == tIndex) {
			Vector vTmp = new Vector();
			vTmp.addElement(tDstIndex);
			vTmp.addElement(tProxyDataStatus);
			String sTmp=new String(tData);
			vTmp.addElement(sTmp);
			basicEditor.vectorDataDst.addElement(vTmp);
			basicEditor.tableDst.updateUI();
			return 1;
		}

		return 0;
	}

	@SuppressWarnings("unchecked")
	public int NoticeNewDatafromSource(int tIndex, int tSourceIndex, ProxyDataStatus tProxyDataStatus, byte[] tData) {
		if (basicEditor.NowSocketIndex == tIndex) {
			Vector vTmp = new Vector();
			vTmp.addElement(tSourceIndex);
			vTmp.addElement(tProxyDataStatus);
			String sTmp=new String(tData);
			vTmp.addElement(sTmp);
			basicEditor.vectorDataSource.addElement(vTmp);
			basicEditor.tableSource.updateUI();
			return 1;
		}

		return 0;
	}

	public int NoticeClose(int tIndex) {
		Iterator iter = vectorDataEstablished.iterator();

		while (iter.hasNext()) {

			Vector value = (Vector) iter.next();
			if (value.contains(tIndex)) {
				vectorDataClose.addElement(value);
				vectorDataEstablished.remove(value);
				tableEstablished.updateUI();
				tableClose.updateUI();
				break;
			}
			System.out.println(value);

		}
		return 1;
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
		if (e.getComponent() == this) {

		}
		// TODO Auto-generated method stub

		basicEditor.splitDetail.setDividerLocation(0.3);
		this.setDividerLocation(0.3);
		basicEditor.splitSourceDst.setDividerLocation(0.5);

	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		String tempStr = "";// 计算文本区原始长度使用
		if (arg0.getSource() == basicEditor.tableDst && arg0.getButton() == MouseEvent.BUTTON1) {

			int[] selection = basicEditor.tableDst.getSelectedRows();
			for (int i = 0; i < selection.length; i++) {
				int tIndex=(int) basicEditor.tableDst.getValueAt(selection[i], 0);
				String sTmp=new String(basicEditor.NowSocketInfo.byteDataDst.get(tIndex).data);
				tempStr+=sTmp;
//				tempStr += tableSource.getValueAt(selection[i], 0);

			}
			basicEditor.editorGUINow.SetData(tempStr);
			basicEditor.tableSource.clearSelection();  //取消选择
			return;
		}
		if (arg0.getSource() == basicEditor.tableSource && arg0.getButton() == MouseEvent.BUTTON1) {

			int[] selection = basicEditor.tableSource.getSelectedRows();
			for (int i = 0; i < selection.length; i++) {
				int tIndex=(int) basicEditor.tableSource.getValueAt(selection[i], 0);
				String sTmp=new String(basicEditor.NowSocketInfo.byteDataSource.get(tIndex).data);
				tempStr+=sTmp;

			}
			basicEditor.editorGUINow.SetData(tempStr);
			basicEditor.tableDst.clearSelection();  //取消选择
			return;
		}

		if (arg0.getSource() == tableEstablished) {
			

			if(arg0.getButton() == MouseEvent.BUTTON3)//右键事件
			{
				return;
		}
			
			tableClose.clearSelection();  //取消选择
			int tIndex = (int) tableEstablished.getValueAt(tableEstablished.getSelectedRow(), 0);
			basicEditor.RefreshFromEstablish(tIndex);
//			
			return;
		}
		if (arg0.getSource() == tableClose) {
			tableEstablished.clearSelection();  //取消选择
			int tIndex = (int) tableClose.getValueAt(tableClose.getSelectedRow(), 0);
			basicEditor.RefreshFromClose(tIndex);
			
			return;
		}

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
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==basicEditor.scollSource ||e.getSource()==basicEditor.tableSource){//右键事件
			if(e.getButton() == MouseEvent.BUTTON3){
				
				popupMenuSource.show(e.getComponent(),e.getX(),e.getY());
			}
		}
		else if(e.getSource()==basicEditor.scollDst || e.getSource()==basicEditor.tableDst){
			
			if(e.getButton() == MouseEvent.BUTTON3){
					
				popupMenuDst.show(e.getComponent(),e.getX(),e.getY());
					
			}
		}
		else if(e.getSource()==tableEstablished || e.getSource()==scrollEstablished){
			if(e.getButton() == MouseEvent.BUTTON3){
				
			popupMenuEstablished.show(e.getComponent(),e.getX(),e.getY());
			}
		}

		else if(e.getSource()==tableClose || e.getSource()==scrollClose){
			if(e.getButton() == MouseEvent.BUTTON3){
				
				popupMenuClose.show(e.getComponent(),e.getX(),e.getY());
			}
			
		}
		
	}

	private void SendBuffer(){

		int dataIndex=0;
		if (-1!=basicEditor.tableDst.getSelectedRow()) {

			if (basicEditor.tableDst.getSelectedRowCount()>1) {//多选的处理
				boolean bNeedMerge=false;
				int[] selection = basicEditor.tableDst.getSelectedRows();
				for (int i = 0; i < selection.length; i++) {
					dataIndex = (int) basicEditor.tableDst.getValueAt(selection[i], 0);
					ProxyDataStatus tProxyDataStatus=basicEditor.NowSocketInfo.GetStatusFromDst(dataIndex);
					if (tProxyDataStatus!=null && tProxyDataStatus==ProxyDataStatus.SENT) {//有一个被发送的数据，那就是需要合并再发送
						bNeedMerge=true;
						break;
					}
				}
				if (true==bNeedMerge) {//有已发送的数据，需要先合并成一个新的数据包再发送
					dataIndex=basicEditor.NowSocketInfo.MergeDatafromDst(basicEditor.editorGUINow.GetDataFromEditor().getBytes(), basicEditor.originStr.getBytes());
				}
				else{//可以直接发送
					if (selection[0]-1<0) {
						dataIndex=0;

					}
					else {
					dataIndex = (int) basicEditor.tableDst.getValueAt(selection[0]-1, 0);//获取选中的第一个的前面一个
					}
					int tIndex;
					for (int i = 0; i < selection.length; i++) {
					tIndex	= (int) basicEditor.tableDst.getValueAt(selection[i], 0);
						basicEditor.NowSocketInfo.ModifyStatusFromDst(tIndex, ProxyDataStatus.SENT);
					}
					
				}
				
				
			}
			else{ //单选的处理
				dataIndex = (int) basicEditor.tableDst.getValueAt(basicEditor.tableDst.getSelectedRow(), 0);
				ProxyDataStatus tProxyDataStatus=basicEditor.NowSocketInfo.GetStatusFromDst(dataIndex);
				if (tProxyDataStatus!=null && tProxyDataStatus==ProxyDataStatus.NOT_SEND) {//没有发送的数据
					if (basicEditor.editorGUINow.ChangeData()) {
						
						basicEditor.NowSocketInfo.ModifyDatafromDst(dataIndex,
								basicEditor.editorGUINow.GetDataFromEditor().getBytes());
					}
				} else {// 已经发送或者放弃的数据，那就只能新建一个再发送 ，其实就和合并了一样

					int option = JOptionPane.showConfirmDialog(null, "该数据包已发送，是否复制再发送", "确认继续？",
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null);
					switch (option) {
					case JOptionPane.YES_NO_OPTION: {
						if (basicEditor.editorGUINow.ChangeData()) {
							dataIndex = basicEditor.NowSocketInfo.MergeDatafromDst(
									basicEditor.editorGUINow.GetDataFromEditor().getBytes(),
									basicEditor.originStr.getBytes());
						} else {
							dataIndex = basicEditor.NowSocketInfo
									.MergeDatafromDst(basicEditor.editorGUINow.GetDataFromEditor().getBytes(), null);
						}
						break;
					}
					case JOptionPane.NO_OPTION:

					}

				}

				basicEditor.NowSocketInfo.ModifyStatusFromDst(dataIndex, ProxyDataStatus.SENT);
			}
			//开始处理前面的数据，（传数据必须有序，如果发送那么前面未发送的数据都将抛弃）
			for (int i = 1; i <= dataIndex; i++) {
				ProxyDataStatus tProxyDataStatus=basicEditor.NowSocketInfo.GetStatusFromDst(i);
				if (tProxyDataStatus!=null && (tProxyDataStatus==ProxyDataStatus.NOT_SEND||tProxyDataStatus==ProxyDataStatus.NEW_DATA)) {
					basicEditor.NowSocketInfo.ModifyStatusFromDst(i, ProxyDataStatus.ABANDON);
				}
			}
			
			
			javaInvokeCPlus.AddSendtoSourceBuffer(basicEditor.NowSocketIndex, basicEditor.editorGUINow.GetDataFromEditor().getBytes());
		}
		else if (-1!=basicEditor.tableSource.getSelectedRow()) {
			if (basicEditor.tableSource.getSelectedRowCount()>1) {//多选的处理
				
				dataIndex=basicEditor.NowSocketInfo.MergeDatafromSource(basicEditor.editorGUINow.GetDataFromEditor().getBytes(), basicEditor.originStr.getBytes());
				
				int[] selection = basicEditor.tableSource.getSelectedRows();
				dataIndex = (int) basicEditor.tableSource.getValueAt(selection[selection.length-1], 0);//获取选中的最后一个
			
			}
			else{//单选的处理
				dataIndex = (int) basicEditor.tableSource.getValueAt(basicEditor.tableSource.getSelectedRow(), 0);
				if (basicEditor.editorGUINow.ChangeData()) {
					basicEditor.NowSocketInfo.ModifyDatafromSource(dataIndex, basicEditor.editorGUINow.GetDataFromEditor().getBytes());
				}
				basicEditor.NowSocketInfo.ModifyStatusFromSource(dataIndex, ProxyDataStatus.SENT);
			}
			//开始处理前面的数据，（传数据必须有序，如果发送那么前面未发送的数据都将抛弃）
			for (int i = 1; i <= dataIndex; i++) {
				ProxyDataStatus tProxyDataStatus=basicEditor.NowSocketInfo.GetStatusFromSource(i);
				if (tProxyDataStatus!=null && (tProxyDataStatus==ProxyDataStatus.NOT_SEND||tProxyDataStatus==ProxyDataStatus.NEW_DATA)) {
					basicEditor.NowSocketInfo.ModifyStatusFromSource(i, ProxyDataStatus.ABANDON);
				}
			}
			
			javaInvokeCPlus.AddSendtoDstBuffer(basicEditor.NowSocketIndex, basicEditor.editorGUINow.GetDataFromEditor().getBytes());
		}
		basicEditor.RefreshFromEstablish(basicEditor.NowSocketIndex);
		return ;
	
	}
	
	 public static String codeString(String fileName) throws Exception {
	       BufferedInputStream bin = new BufferedInputStream(new FileInputStream(
	               fileName));
	       int p = (bin.read() << 8) + bin.read();
	       System.out.println(p);
	       String code = null;

	       switch (p) {
	       case 0xefbb:
	           code = "UTF-8";
	           break;
	       case 0xfffe:
	           code = "Unicode";
	           break;
	       case 0xfeff:
	           code = "UTF-16BE";
	           break;
	       default:
	           code = "GBK";
	       }

	       return code;
	   }
		
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			if (arg0.getSource() == bSend) {
				SendBuffer();
			} 
			if (arg0.getSource() == bPause) {

				return ;
			}
			if (arg0.getSource() == bFileImport) {
				
				int select=fileChooser.showOpenDialog(null);
				String encodingType=null;//记录编码类型
				
				if(select==fileChooser.APPROVE_OPTION){
					File fileTemp=fileChooser.getSelectedFile();
					
					
					
					if(!fileTemp.exists()){
						System.out.println("该文件不存在");
					}
					
					else
					{
						//System.out.println("该文件存在");
						try {
							encodingType=codeString(fileTemp.toString());
							System.out.println("hhhhhhhhhhhh"+encodingType);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						 FileInputStream inputStream = null;
						 byte[] contentInBytes = null;
						 try {
							inputStream=new FileInputStream(fileTemp);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						 try {
							contentInBytes=new byte[inputStream.available()];//可能会出现内存溢出（读大文件时候）d待改
							inputStream.read(contentInBytes);
							basicEditor.editorGUINow.StrEditor.setText(new String(contentInBytes,encodingType));
							System.out.println("成功导入文件");
							inputStream.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
					
			
					
				}else{
					System.out.println("选择了取消，没有打开文件");
				}
				
			} 
			if (arg0.getSource() == bFileExport) {
				
				int select=fileChooser.showSaveDialog(null);
				if(select==fileChooser.APPROVE_OPTION){
					File fileTemp=fileChooser.getSelectedFile();
					

					
					System.out.println(fileTemp.getName());
					
					if(!fileTemp.exists()){
						System.out.println("该文件不存在");
						try {
							fileTemp.createNewFile();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					
					{
						
//						System.out.println("该文件存在");
						 FileOutputStream outputStream = null;	
						 try {
							outputStream=new FileOutputStream(fileTemp);
							
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						 byte[] contentInBytes = basicEditor.editorGUINow.StrEditor.getText().getBytes();
						 try {
							BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "GBK")); 
							writer.write(basicEditor.editorGUINow.StrEditor.getText().toString());
							
							//outputStream.write(contentInBytes);
							System.out.println("成功导出文件");
							writer.flush();
							writer.close();
							//outputStream.flush();
							//outputStream.close();
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally{
							try {
								if(outputStream!=null){
									outputStream.close();
								} 
								
							}catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
						}
						 
					}
					
					
					
				}else{
					System.out.println("选择了取消，没有打开文件");
				}
			
			}
			
		
			if(arg0.getSource()==menuItemSourceMerge)
			{
				if(basicEditor.tableSource.getSelectedRowCount()>1){
					if (basicEditor.editorGUINow.ChangeData()) {
						 basicEditor.NowSocketInfo.MergeDatafromSource(
								basicEditor.editorGUINow.GetDataFromEditor().getBytes(),
								basicEditor.originStr.getBytes());
					} else {
						 basicEditor.NowSocketInfo
								.MergeDatafromSource(basicEditor.editorGUINow.GetDataFromEditor().getBytes(), null);
					}
					basicEditor.RefreshFromEstablish(basicEditor.NowSocketIndex);
				}
				
				
				return;
			}
			if(arg0.getSource()==menuItemSourceSend)
			{
				SendBuffer();
					
				return;
			}
			if(arg0.getSource()==menuItemSourceDelete)
			{
				int[] selection = basicEditor.tableSource.getSelectedRows();
				for (int i = 0; i < selection.length; i++) {
					int dataIndex = (int) basicEditor.tableSource.getValueAt(selection[i], 0);
					basicEditor.NowSocketInfo.DelFromSource(dataIndex);
				}
				basicEditor.RefreshFromEstablish(basicEditor.NowSocketIndex); 
					
				return;
			}
			if(arg0.getSource()==menuItemDstMerge)
			{
				if(basicEditor.tableDst.getSelectedRowCount()>1){
					if (basicEditor.editorGUINow.ChangeData()) {
						 basicEditor.NowSocketInfo.MergeDatafromDst(
								basicEditor.editorGUINow.GetDataFromEditor().getBytes(),
								basicEditor.originStr.getBytes());
					} else {
						 basicEditor.NowSocketInfo
								.MergeDatafromDst(basicEditor.editorGUINow.GetDataFromEditor().getBytes(), null);
					}
					basicEditor.RefreshFromEstablish(basicEditor.NowSocketIndex);
				}
				
				
				return;
			}
			if(arg0.getSource()==menuItemDstSend)
			{
				SendBuffer();
					
				return;
			}
			if(arg0.getSource()==menuItemDstNew){
				basicEditor.NowSocketInfo.InsertDatafromDst(ProxyDataStatus.NEW_DATA, null);
				basicEditor.RefreshFromEstablish(basicEditor.NowSocketIndex);
				return;
			}
			if(arg0.getSource()==menuItemSourceNew){
				basicEditor.NowSocketInfo.InsertDatafromSource(ProxyDataStatus.NEW_DATA, null);
				basicEditor.RefreshFromEstablish(basicEditor.NowSocketIndex);
				return;
			}
			if(arg0.getSource()==menuItemDstDelete)
			{
				int[] selection = basicEditor.tableDst.getSelectedRows();
				for (int i = 0; i < selection.length; i++) {
					int dataIndex = (int) basicEditor.tableDst.getValueAt(selection[i], 0);
					basicEditor.NowSocketInfo.DelFromDst(dataIndex);
				}
				 
					
				return;
			}
			if(arg0.getSource()==menuItemEstablishedInterceptSend){
				if(tableEstablished.getSelectedColumnCount()==1){
				int index= (int) tableEstablished.getValueAt(tableEstablished.getSelectedRow(), 0);
				InterceptStatus interceptStatus=manageSocketData.GetSocketInfoEstablish(index).interceptStatus;
				if (InterceptStatus.INTERCEPT_SEND==interceptStatus) {
					interceptStatus=InterceptStatus.INTERCEPT_NONE;
				}
				else if (InterceptStatus.INTERCEPT_NONE==interceptStatus) {
					interceptStatus=InterceptStatus.INTERCEPT_SEND;
				}
				else if (InterceptStatus.INTERCEPT_RECV==interceptStatus) {
					interceptStatus=InterceptStatus.INTERCEPT_ALL;
				}
				else if (InterceptStatus.INTERCEPT_ALL==interceptStatus) {
					interceptStatus=InterceptStatus.INTERCEPT_RECV;
				}
				manageSocketData.GetSocketInfoEstablish(index).interceptStatus=interceptStatus;
				javaInvokeCPlus.SetInterceptStatus(index, interceptStatus.GetStatus());
				}
				return;
			}
		}

	}