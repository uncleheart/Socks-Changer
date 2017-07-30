package pers.ui.repeater;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.JFileChooser;
import javax.swing.filechooser.*;

import org.omg.CORBA.ARG_OUT;

import pers.data.ManageSocketData;
import pers.data.ProtocolType;
import pers.data.ProxyDataStatus;
import pers.data.SocketByteData;
import pers.data.SocketInfo;
import pers.dll.infc.JavaInvokeCPlus;
import pers.dll.infc.NewConData;
import pers.ui.basic.BasicEditor;

public class SplitRepeater extends JSplitPane implements ComponentListener, MouseListener, ActionListener {

	BasicEditor reBasicEditor;
	protected JPopupMenu popupMenuSource;
	protected JMenuItem menuItemSourceMerge;// 整合
	protected JMenuItem menuItemSourceDelete;// 删除
	protected JMenuItem menuItemSourceNew;// 新建

	protected JPopupMenu popupMenuDst;
	protected JMenuItem menuItemDstMerge;// 整合
	protected JMenuItem menuItemDstDelete;// 删除
	protected JMenuItem menuItemDstNew;// 新建

	HashMap<Integer, SocketClient> socketSum;
	int NowKey;
	ManageSocketData manageSocketData;
	
	JToolBar toolBarRepeater;// 创建工具栏
	JPanel panelRepeaterContentEditor;// 为右面部分创建一个内容窗格 包含编辑器与历史
	JButton reSend;// 发送按钮
	JButton rePause;// 开关按钮
	
	JButton reFileImport;// 导入文件
	JButton reFileExport;// 导出文件
	
	JFileChooser fileChooser;//=new JFileChooser();//文件对话框
	
	public SplitRepeater() {
		super(JSplitPane.HORIZONTAL_SPLIT, true);

		fileChooser=new JFileChooser();
		InitSocketStatus();

		socketSum=new HashMap<Integer, SocketClient>();
		NowKey=1;
		manageSocketData=new ManageSocketData();
		reBasicEditor = new BasicEditor(manageSocketData);
		
		toolBarRepeater = new JToolBar(JToolBar.VERTICAL);// 建立工具栏
		reSend = new JButton("发送");
		rePause = new JButton("开关");
		reFileImport = new JButton("导入");
		reFileExport = new JButton("导出");
		
		toolBarRepeater.add(reSend);
		toolBarRepeater.add(rePause);
		toolBarRepeater.add(reFileImport);
		toolBarRepeater.add(reFileExport);
		
		reSend.addActionListener(this);
		rePause.addActionListener(this);
		reFileImport.addActionListener(this);
		reFileExport.addActionListener(this);
		
		reBasicEditor.add(toolBarRepeater, BorderLayout.EAST);
		this.setLeftComponent(panelLeft);
		this.setRightComponent(reBasicEditor);
		this.addComponentListener(this);

		popupMenuSource = new JPopupMenu();// 的弹出式菜单
		menuItemSourceMerge = new JMenuItem("Merge");
		menuItemSourceDelete = new JMenuItem("Delete");
		menuItemSourceNew = new JMenuItem("New");

		popupMenuSource.add(menuItemSourceMerge);
		popupMenuSource.add(menuItemSourceDelete);
		popupMenuSource.add(menuItemSourceNew);

		menuItemSourceMerge.addActionListener(this);
		menuItemSourceNew.addActionListener(this);
		menuItemSourceDelete.addActionListener(this);
				
		reBasicEditor.scollSource.addMouseListener(this);
		reBasicEditor.tableSource.addMouseListener(this);
		reBasicEditor.scollDst.addMouseListener(this);
		reBasicEditor.tableDst.addMouseListener(this);

		popupMenuDst = new JPopupMenu();// 的弹出式菜单
		menuItemDstMerge = new JMenuItem("Merge");
		menuItemDstDelete = new JMenuItem("Delete");
		menuItemDstNew = new JMenuItem("New");
		popupMenuDst.add(menuItemDstMerge);
		popupMenuDst.add(menuItemDstDelete);
		popupMenuDst.add(menuItemDstNew);


	}

	// 套接字状态 的分页

	protected JPanel panelLeft;
	protected JPanel panelIPAndPort;
	protected JLabel labelIP;
	protected JLabel labelPort;
	JButton btnConnect;
	protected JTextField textFieldIP;
	protected JTextField textFieldPort;
	protected JScrollPane scrollGatherList;
	protected Vector vectorDataRepeater = new Vector();
	protected Vector vectorNameRepeater = new Vector();
	protected TableModel tabModelRepeater;
	protected JTable tableRepeater;
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

	protected JPopupMenu popupMenuEstablished;
	protected JPopupMenu popupMenuClose;
	protected JMenuItem menuItemEstablishedSave;// 保存
	protected JMenuItem menuItemEstablishedDelete;// 删除
	protected JMenu menuEstablishedTemp;// 测试、、、、、、、、、、、、、、、、、、、

	private void InitSocketStatus() {
		// tabSocketStatus = new JTabbedPane(JTabbedPane.TOP);
		JPanel panelTemp1 = new JPanel();
		panelIPAndPort = new JPanel();
		labelIP = new JLabel("IP:");
		labelPort = new JLabel("Port:");
		btnConnect = new JButton("connect");
		btnConnect.addActionListener(this);
		textFieldIP = new JTextField();
		textFieldPort = new JTextField();
		GridBagLayout layoutpanelIPAndPort = new GridBagLayout();
		panelIPAndPort.setLayout(layoutpanelIPAndPort);
		GridBagConstraints gConstraintsIPAndPort = new GridBagConstraints();
		gConstraintsIPAndPort.fill = GridBagConstraints.BOTH;

		gConstraintsIPAndPort.insets = new Insets(5, 0, 5, 5);// 设置控件的空白（上，左，下，右）
		gConstraintsIPAndPort.gridwidth = 3;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
		gConstraintsIPAndPort.gridheight = 2;// 占用1列
		gConstraintsIPAndPort.gridx = 0;// 起始点为第1列
		gConstraintsIPAndPort.gridy = 0;// 起始点为第1行

		gConstraintsIPAndPort.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		gConstraintsIPAndPort.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		layoutpanelIPAndPort.setConstraints(panelTemp1, gConstraintsIPAndPort);// 设置组件

		gConstraintsIPAndPort.insets = new Insets(5, 0, 5, 5);// 设置控件的空白（上，左，下，右）
		gConstraintsIPAndPort.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
		gConstraintsIPAndPort.gridheight = 2;// 占用1列
		gConstraintsIPAndPort.gridx = 3;// 起始点为第1列
		gConstraintsIPAndPort.gridy = 0;// 起始点为第1行

		gConstraintsIPAndPort.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		gConstraintsIPAndPort.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		layoutpanelIPAndPort.setConstraints(labelIP, gConstraintsIPAndPort);// 设置组件

		gConstraintsIPAndPort.insets = new Insets(5, 0, 5, 5);// 设置控件的空白
		gConstraintsIPAndPort.gridwidth = 3;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
		gConstraintsIPAndPort.gridheight = 2;// 占用1行
		gConstraintsIPAndPort.gridx = 4;// 起始点为第1列
		gConstraintsIPAndPort.gridy = 0;// 起始点为第1行
		gConstraintsIPAndPort.weightx = 1;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		gConstraintsIPAndPort.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		layoutpanelIPAndPort.setConstraints(textFieldIP, gConstraintsIPAndPort);// 设置组件

		gConstraintsIPAndPort.insets = new Insets(5, 5, 5, 5);// 设置控件的空白
		gConstraintsIPAndPort.gridwidth = 1;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
		gConstraintsIPAndPort.gridheight = 2;// 占用1行
		gConstraintsIPAndPort.gridx = 7;// 起始点为第4列
		gConstraintsIPAndPort.gridy = 0;// 起始点为第1行
		gConstraintsIPAndPort.weightx = 0;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		gConstraintsIPAndPort.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		layoutpanelIPAndPort.setConstraints(labelPort, gConstraintsIPAndPort);// 设置组件

		gConstraintsIPAndPort.insets = new Insets(5, 0, 5, 10);// 设置控件的空白
		gConstraintsIPAndPort.gridwidth = 2;// 该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
		gConstraintsIPAndPort.gridheight = 2;// 占用1行
		gConstraintsIPAndPort.gridx = 10;// 起始点为第5列
		gConstraintsIPAndPort.gridy = 0;// 起始点为第1行
		gConstraintsIPAndPort.weightx = 1;// 该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		gConstraintsIPAndPort.weighty = 0;// 该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		layoutpanelIPAndPort.setConstraints(textFieldPort, gConstraintsIPAndPort);// 设置组件

		gConstraintsIPAndPort.insets = new Insets(5, 0, 5, 10);// 设置控件的空白
		gConstraintsIPAndPort.gridwidth=2;//该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
		gConstraintsIPAndPort.gridheight = 2;// 占用1行
		gConstraintsIPAndPort.gridx = 12;// 起始点为第5列
		gConstraintsIPAndPort.gridy = 0;// 起始点为第1行
		gConstraintsIPAndPort.weightx = 0;//该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		gConstraintsIPAndPort.weighty=0;//该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		layoutpanelIPAndPort.setConstraints(btnConnect, gConstraintsIPAndPort);//设置组件
		
		panelIPAndPort.add(panelTemp1);
		panelIPAndPort.add(labelIP);
		panelIPAndPort.add(textFieldIP);
		panelIPAndPort.add(labelPort);
		panelIPAndPort.add(textFieldPort);
		panelIPAndPort.add(btnConnect);

		// popupMenuEstablished.setBackground(Color.BLACK);

		vectorNameRepeater.add("Index");
		vectorNameRepeater.add("Host");
		vectorNameRepeater.add("IP");
		vectorNameRepeater.add("Port");
		vectorNameRepeater.add("Protocol");

		tabModelRepeater = new DefaultTableModel(vectorDataRepeater, vectorNameRepeater);
		tableRepeater = new JTable(tabModelRepeater) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tableRepeater.setPreferredScrollableViewportSize(new Dimension(600, 100));// 设置表格的大小
		tableRepeater.setRowHeight(30);// 设置每行的高度为20
		tableRepeater.setRowHeight(0, 20);// 设置第1行的高度为15
		tableRepeater.setRowMargin(5);// 设置相邻两行单元格的距离
		tableRepeater.setSelectionBackground(Color.white);// 设置所选择行的背景色
		tableRepeater.setSelectionForeground(Color.red);// 设置所选择行的前景色
		tableRepeater.setGridColor(Color.black);// 设置网格线的颜色
		tableRepeater.setShowHorizontalLines(false);// 是否显示水平的网格线
		tableRepeater.setShowVerticalLines(true);// 是否显示垂直的网格线
		tableRepeater.doLayout();
		tableRepeater.setBackground(Color.lightGray);
		tableRepeater.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		rowSorterEstablished = new TableRowSorter<TableModel>(tabModelRepeater); // 排序
		tableRepeater.setRowSorter(rowSorterEstablished);

		// JTable最好加在JScrollPane上
		scrollGatherList = new JScrollPane(tableRepeater, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panelLeft = new JPanel();
		panelLeft.setLayout(new BorderLayout());

		panelLeft.add(panelIPAndPort, BorderLayout.NORTH);
		panelLeft.add(scrollGatherList, BorderLayout.CENTER);
		panelLeft.setMinimumSize(new Dimension(250, 0));

		tableRepeater.updateUI();

		// 隐藏index
		TableColumnModel columnModelEstablished = tableRepeater.getColumnModel();
		TableColumn columnEstablished = columnModelEstablished.getColumn(0);
		columnEstablished.setMinWidth(0);
		columnEstablished.setMaxWidth(0);

		popupMenuEstablished = new JPopupMenu();// Established的弹出式菜单
		menuItemEstablishedSave = new JMenuItem("Save");
		menuItemEstablishedDelete = new JMenuItem("Delete");
		menuEstablishedTemp = new JMenu("Temp");

		popupMenuEstablished.add(menuItemEstablishedSave);
		popupMenuEstablished.add(menuItemEstablishedDelete);
		popupMenuEstablished.add(menuEstablishedTemp);

		tableRepeater.addMouseListener(this); // 添加监听器
		scrollGatherList.addMouseListener(this); // 添加监听器

	}

	

	public void NoticeNewCon(int tIndex, String hostname, String ipAddress, int port, ProtocolType tProtocolType) {
		Vector vTmp = new Vector();
		vTmp.add(tIndex);
		vTmp.add(hostname);
		vTmp.add(ipAddress);
		vTmp.add(port);
		vTmp.add(tProtocolType.GetString());
		vectorDataRepeater.add(vTmp);
		tableRepeater.updateUI();
	}

	// int NowSocketIndex;
	// SocketInfo NowSocketInfo;
	public int NoticeNewDatafromDst(int tIndex, int tDstIndex, ProxyDataStatus tProxyDataStatus, byte[] tData) {
		if (reBasicEditor.NowSocketIndex == tIndex) {
			Vector vTmp = new Vector();
			vTmp.addElement(tDstIndex);
			vTmp.addElement(tProxyDataStatus);
			String sTmp = new String(tData);
			vTmp.addElement(sTmp);
			reBasicEditor.vectorDataDst.addElement(vTmp);
			reBasicEditor.tableDst.updateUI();
			return 1;
		}

		return 0;
	}

	public int NoticeNewDatafromSource(int tIndex, int tSourceIndex, ProxyDataStatus tProxyDataStatus, byte[] tData) {
		if (reBasicEditor.NowSocketIndex == tIndex) {
			Vector vTmp = new Vector();
			vTmp.addElement(tSourceIndex);
			vTmp.addElement(tProxyDataStatus);
			String sTmp = new String(tData);
			vTmp.addElement(sTmp);
			reBasicEditor.vectorDataSource.addElement(vTmp);
			reBasicEditor.tableSource.updateUI();
			return 1;
		}

		return 0;
	}

	public int NoticeClose(int tIndex) {
		Iterator iter = vectorDataRepeater.iterator();

		while (iter.hasNext()) {

			Vector value = (Vector) iter.next();
			if (value.contains(tIndex)) {
				vectorDataClose.addElement(value);
				vectorDataRepeater.remove(value);
				tableRepeater.updateUI();
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
		if (e.getComponent() == reBasicEditor.splitDetail) {

		}
		// TODO Auto-generated method stub

		reBasicEditor.splitDetail.setDividerLocation(0.3);
		this.setDividerLocation(0.3);
		reBasicEditor.splitSourceDst.setDividerLocation(0.5);

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
		if (arg0.getSource() == reBasicEditor.scollSource || arg0.getSource() == reBasicEditor.tableSource) {// 右键事件
			if (arg0.getButton() == MouseEvent.BUTTON3) {

				popupMenuSource.show(arg0.getComponent(), arg0.getX(), arg0.getY());

			}
		} else if (arg0.getSource() == reBasicEditor.scollDst || arg0.getSource() == reBasicEditor.tableDst) {

			if (arg0.getButton() == MouseEvent.BUTTON3) {

				popupMenuDst.show(arg0.getComponent(), arg0.getX(), arg0.getY());

			}
		} else if (arg0.getSource() == tableRepeater || arg0.getSource() == scrollGatherList) {

			if (arg0.getButton() == MouseEvent.BUTTON3) {// 右键事件

				popupMenuEstablished.show(arg0.getComponent(), arg0.getX(), arg0.getY());
			}
		}
		if(arg0.getSource()==tableRepeater){
			int tIndex = (int) tableRepeater.getValueAt(tableRepeater.getSelectedRow(), 0);
			reBasicEditor.RefreshFromEstablish(tIndex);
//			SendBuffer();
			return;
		}

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getSource() == reSend) {
			SendBuffer();
			return;
		}
		if (arg0.getSource() == rePause) {

			return;
		}
if (arg0.getSource() == reFileImport) {
			
			int select=fileChooser.showOpenDialog(null);
			if(select==fileChooser.APPROVE_OPTION){
				File fileTemp=fileChooser.getSelectedFile();
				
				if(!fileTemp.exists()){
					System.out.println("该文件不存在");
				}
				
				else
				{
					System.out.println("该文件存在");
					
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
						reBasicEditor.editorGUINow.StrEditor.setText(new String(contentInBytes));
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
		if (arg0.getSource() == reFileExport) {
			
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
				
				FileOutputStream outputStream = null;	
				try {
					outputStream=new FileOutputStream(fileTemp);	
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				byte[] contentInBytes = reBasicEditor.editorGUINow.StrEditor.getText().getBytes();
				try {
					outputStream.write(contentInBytes);
					System.out.println("成功导出文件");
					outputStream.flush();
					outputStream.close();
						
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
				
			}else{
				System.out.println("选择了取消，没有打开文件");
			}
		
		}
		
		if(arg0.getSource()==btnConnect){
			
			SocketClient socketClient=new SocketClient(textFieldIP.getText(),Integer.parseInt(textFieldPort.getText()),this);
			socketSum.put(NowKey, socketClient);
			
			NewConData newConData=new NewConData(NowKey,1, "127.0.0.1", "127.0.0.1", 0, textFieldIP.getText(), textFieldIP.getText(), Integer.parseInt(textFieldPort.getText()));
			manageSocketData.NoticeNewCon(newConData,null);
			Vector vTmp = new Vector();
			vTmp.add(NowKey);
			vTmp.add(textFieldIP.getText());
			vTmp.add(textFieldIP.getText());
			vTmp.add(textFieldPort.getText());
			vTmp.add(ProtocolType.TCP);
			vectorDataRepeater.add(vTmp);
			tableRepeater.updateUI();
			NowKey+=1;
			return;
		}
//		if(arg0.getSource()==menuItemDstNew){
//			reBasicEditor.NowSocketInfo.InsertDatafromDst(ProxyDataStatus.NEW_DATA, null);
//			reBasicEditor.RefreshFromEstablish(reBasicEditor.NowSocketIndex);
//			return;
//		}
		if(arg0.getSource()==menuItemSourceNew){
			reBasicEditor.NowSocketInfo.InsertDatafromSource(ProxyDataStatus.NEW_DATA, new byte[0]);
			reBasicEditor.RefreshFromEstablish(reBasicEditor.NowSocketIndex);
			return;
		}
	}

	public void SendBuffer() {
		int dataIndex = 0;
		if (-1 != reBasicEditor.tableSource.getSelectedRow()) {

			if (reBasicEditor.tableSource.getSelectedRowCount() > 1) {// 多选的处理
				boolean bNeedMerge = false;
				int[] selection = reBasicEditor.tableSource.getSelectedRows();
				for (int i = 0; i < selection.length; i++) {
					dataIndex = (int) reBasicEditor.tableSource.getValueAt(selection[i], 0);
					ProxyDataStatus tProxyDataStatus = reBasicEditor.NowSocketInfo.GetStatusFromSource(dataIndex);
					if (tProxyDataStatus != null && tProxyDataStatus == ProxyDataStatus.SENT) {// 有一个被发送的数据，那就是需要合并再发送
						bNeedMerge = true;
						break;
					}
				}
				if (true == bNeedMerge) {// 有已发送的数据，需要先合并成一个新的数据包再发送
					dataIndex = reBasicEditor.NowSocketInfo.MergeDatafromSource(
							reBasicEditor.editorGUINow.GetDataFromEditor().getBytes(),
							reBasicEditor.originStr.getBytes());
				} else {// 可以直接发送
					if (selection[0] - 1 < 0) {
						dataIndex = 0;

					} else {
						dataIndex = (int) reBasicEditor.tableSource.getValueAt(selection[0] - 1, 0);// 获取选中的第一个的前面一个
					}
					int tIndex;
					for (int i = 0; i < selection.length; i++) {
						tIndex = (int) reBasicEditor.tableSource.getValueAt(selection[i], 0);
						reBasicEditor.NowSocketInfo.ModifyStatusFromSource(tIndex, ProxyDataStatus.SENT);
					}

				}

			} else { // 单选的处理
				dataIndex = (int) reBasicEditor.tableSource.getValueAt(reBasicEditor.tableSource.getSelectedRow(), 0);
				ProxyDataStatus tProxyDataStatus = reBasicEditor.NowSocketInfo.GetStatusFromSource(dataIndex);
				if (tProxyDataStatus != null && tProxyDataStatus == ProxyDataStatus.SENT) {// 发送的数据

					int option = JOptionPane.showConfirmDialog(null, "该数据包已发送，是否复制再发送", "确认继续？",
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null);
					switch (option) {
					case JOptionPane.YES_NO_OPTION: {

							dataIndex = reBasicEditor.NowSocketInfo
									.MergeDatafromSource(reBasicEditor.editorGUINow.GetDataFromEditor().getBytes(), null);
						
						break;
					}
					case JOptionPane.NO_OPTION:

					}

				}

				reBasicEditor.NowSocketInfo.ModifyStatusFromSource(dataIndex, ProxyDataStatus.SENT);
			}
			// 开始处理前面的数据，（传数据必须有序，如果发送那么前面未发送的数据都将抛弃）

			for (int i = 1; i <= dataIndex; i++) {
				ProxyDataStatus tProxyDataStatus = reBasicEditor.NowSocketInfo.GetStatusFromSource(i);
				if (tProxyDataStatus != null && (tProxyDataStatus == ProxyDataStatus.NOT_SEND
						|| tProxyDataStatus == ProxyDataStatus.NEW_DATA)) {
					reBasicEditor.NowSocketInfo.ModifyStatusFromSource(i, ProxyDataStatus.ABANDON);
				}
			}
			
			if (socketSum.containsKey(reBasicEditor.NowSocketIndex)) {
				socketSum.get(reBasicEditor.NowSocketIndex).SendData(reBasicEditor.editorGUINow.GetDataFromEditor().getBytes());
			}
			reBasicEditor.RefreshFromEstablish(reBasicEditor.NowSocketIndex);
		}
	}
public void RecvNewData(byte[] tData){
//	int tIndex=
//	int tLocalIndex=manageSocketData.NoticeNewDatafromDst(tIndex, tProxyDataStatus, tData);
//	splitIntercept.NoticeNewDatafromDst(tIndex,tLocalIndex, tProxyDataStatus, tData);
}
}
