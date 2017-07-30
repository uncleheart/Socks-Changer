package pers.ui.basic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import bibliothek.gui.dock.themes.basic.TabDecorator;
import pers.data.ManageSocketData;
import pers.data.SocketByteData;
import pers.data.SocketInfo;
import pers.dll.infc.JavaInvokeCPlus;

public  class BasicEditor extends JPanel implements MouseListener {
	
	public int NowSocketIndex;
	ManageSocketData manageSocketData;
	JavaInvokeCPlus javaInvokeCPlus;
	
	public SocketInfo NowSocketInfo;
	public String originStr = "";//用来记录原始的数据（没有被修改的）
	
//	JToolBar toolBar;// 创建工具栏
////	JPanel panelContentEditor;// 为右面部分创建一个内容窗格 包含编辑器与历史
//	JButton bSend;// 发送按钮
//	JButton bPause;// 开关按钮
	
	
	public JSplitPane splitDetail; // 显示远端和本地的发送详情 然后还有编辑器

	JPanel panelLeft;
	JPanel panelRight;
	public JSplitPane splitSourceDst;
	public EditorGUI editorGUINow;
	public EditorGUI editorGUIHistory;
	// local的界面
	public JScrollPane scollSource;
	public JPanel panelSource;
	public JList listSource;
	public JLabel labelSource;
	String newItem;// 用来添加新的项
	// DefaultListModel<String> listModelSource;

	public Vector vectorDataSource = new Vector();
	public Vector vectorSourceName = new Vector();
	protected TableModel tabModelSource;
	public JTable tableSource;
	protected RowSorter<TableModel> rowSorterSource;
	private JTabbedPane tabNowAndHistory;
	//private JTabbedPane tabHistory;
	private JPanel panelNow;
	private JPanel panelHistory;
	
	
	
	// Dst的界面
	public JScrollPane scollDst;
	public JPanel panelDst;
	public JList listDst;
	public JLabel labelDst;
	// DefaultListModel<String> listModelDst;

	public Vector vectorDataDst = new Vector();
	protected Vector vectorDstName = new Vector();
	protected TableModel tabModelDst;
	public JTable tableDst;
	protected RowSorter<TableModel> rowSorterDst;


	
//	public BasicEditor(ManageSocketData tManageSocketData,JavaInvokeCPlus tJavaInvokeCPlus) {
	public BasicEditor(ManageSocketData tManageSocketData) {
		super(new BorderLayout());
		manageSocketData = tManageSocketData;
//		javaInvokeCPlus=tJavaInvokeCPlus;
		

		splitDetail = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);

		vectorSourceName.add("Index");// 序号
		vectorSourceName.add("state");// 状态
		vectorSourceName.add("Data");// 数据

		tabModelSource = new DefaultTableModel(vectorDataSource, vectorSourceName);

		tableSource = new JTable(tabModelSource) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}


		};
		// int a[]=new int[100];
		tableSource.setPreferredScrollableViewportSize(new Dimension(600, 100));// 设置表格的大小
		tableSource.setRowHeight(30);// 设置每行的高度为20
		// localTableEstablished.
		// setColumnModel(getColumn(localTableEstablished, a));
		// 设置每行的高度为20
		tableSource.setRowHeight(0, 20);// 设置第1行的高度为15
		tableSource.setRowMargin(5);// 设置相邻两行单元格的距离
		// friends.setRowSelectionAllowed (true);//设置可否被选择.默认为false
		tableSource.setSelectionBackground(Color.white);// 设置所选择行的背景色
		tableSource.setSelectionForeground(Color.red);// 设置所选择行的前景色
		tableSource.setGridColor(Color.black);// 设置网格线的颜色
		// friends.selectAll ();//选择所有行
		// friends.setRowSelectionInterval (0,2);//设置初始的选择行,这里是1到3行都处于选择状态
		// friends.clearSelection ();//取消选择
		// friends.setDragEnabled (false);//不懂这个
		// friends.setShowGrid (false);//是否显示网格线
		tableSource.setShowHorizontalLines(false);// 是否显示水平的网格线
		tableSource.setShowVerticalLines(true);// 是否显示垂直的网格线
		// friends.setValueAt ("tt", 0, 0);//设置某个单元格的值,这个值是一个对象
		tableSource.doLayout();
		tableSource.setBackground(Color.lightGray);
		tableSource.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		rowSorterSource = new TableRowSorter<TableModel>(tabModelSource); // 排序
		tableSource.setRowSorter(rowSorterSource);

		// 隐藏lacalIndex
		// TableColumnModel localColumnModelEstablished =
		// tableEstablished.getColumnModel();
		// TableColumn localColumnEstablished =
		// localColumnModelEstablished.getColumn(0);
		// localColumnEstablished.setMinWidth(0);
		// localColumnEstablished.setMaxWidth(0);

//		 Vector<Comparable> q1 = new Vector();
//		 q1.add("this is a test");
//		 q1.add("1");
//		 q1.add("1");
//		 vectorDataSource.add(q1);
		//
		// Vector q2 = new Vector();
		// q2.add("this is a test aaaaaaaaaaa");
		// q2.add("2");
		// q2.add("2");
		// vectorSourceData.add(q2);
		// Vector q3 = new Vector();
		// q3.add("this is a test aaaa");
		// q3.add("3");
		// q3.add("3");
		// vectorSourceData.add(q3);
		tableSource.updateUI();

		// local的界面
		panelSource = new JPanel();
		labelSource = new JLabel("From Source IP", JLabel.LEFT);
		labelSource.setFont(new Font("",1,14));//设置字体及字号
		labelSource.setForeground(Color.BLACK);//设置字体颜色
		
		scollSource = new JScrollPane(tableSource, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panelSource.setLayout(new BorderLayout());
		panelSource.add(labelSource, BorderLayout.NORTH);
		panelSource.add(scollSource, BorderLayout.CENTER);

		scollSource.addMouseListener( this);
		tableSource.addMouseListener( this);

		
		
		
		
		
		
		
		
		
		// remote的界面
		vectorDstName.add("Index");// 序号
		vectorDstName.add("state");// 状态
		vectorDstName.add("Data");// 数据

		tabModelDst = new DefaultTableModel(vectorDataDst, vectorDstName);

		tableDst = new JTable(tabModelDst) {
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
		// int a[]=new int[100];
		tableDst.setPreferredScrollableViewportSize(new Dimension(600, 100));// 设置表格的大小
		tableDst.setRowHeight(30);// 设置每行的高度为20
		// remoteTableEstablished.
		// setColumnModel(getColumn(remoteTableEstablished, a));
		// 设置每行的高度为20
		tableDst.setRowHeight(0, 20);// 设置第1行的高度为15
		tableDst.setRowMargin(5);// 设置相邻两行单元格的距离
		// friends.setRowSelectionAllowed (true);//设置可否被选择.默认为false
		tableDst.setSelectionBackground(Color.white);// 设置所选择行的背景色
		tableDst.setSelectionForeground(Color.red);// 设置所选择行的前景色
		tableDst.setGridColor(Color.black);// 设置网格线的颜色
		// friends.selectAll ();//选择所有行
		// friends.setRowSelectionInterval (0,2);//设置初始的选择行,这里是1到3行都处于选择状态
		// friends.clearSelection ();//取消选择
		// friends.setDragEnabled (false);//不懂这个
		// friends.setShowGrid (false);//是否显示网格线
		tableDst.setShowHorizontalLines(false);// 是否显示水平的网格线
		tableDst.setShowVerticalLines(true);// 是否显示垂直的网格线
		// friends.setValueAt ("tt", 0, 0);//设置某个单元格的值,这个值是一个对象
		tableDst.doLayout();
		tableDst.setBackground(Color.lightGray);
		tableDst.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		rowSorterDst = new TableRowSorter<TableModel>(tabModelDst); // 排序
		tableDst.setRowSorter(rowSorterDst);

		// 隐藏lacalIndex
		// TableColumnModel remoteColumnModelEstablished =
		// tableEstablished.getColumnModel();
		// TableColumn remoteColumnEstablished =
		// remoteColumnModelEstablished.getColumn(0);
		// remoteColumnEstablished.setMinWidth(0);
		// remoteColumnEstablished.setMaxWidth(0);

//		 Vector<Comparable> q11 = new Vector();
//		 q11.add("this is a test");
//		 q11.add("1");
//		 q11.add("1");
//		 vectorDataDst.add(q11);
		//
		// Vector q22 = new Vector();
		// q22.add("this is a test aaaaaaaaaaa");
		// q22.add("2");
		// q22.add("2");
		// vectorDstData.add(q22);
		// Vector q33 = new Vector();
		// q33.add("this is a test aaaa");
		// q33.add("3");
		// q33.add("3");
		// vectorDstData.add(q33);
		tableDst.updateUI();

		panelDst = new JPanel();
		labelDst = new JLabel("From Destination IP", JLabel.LEFT);
		labelDst.setFont(new Font("",1,14));//设置字体及字号
		labelDst.setForeground(Color.BLACK);//设置字体颜色
		scollDst = new JScrollPane(tableDst, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panelDst.setLayout(new BorderLayout());
		panelDst.add(labelDst, BorderLayout.NORTH);
		panelDst.add(scollDst, BorderLayout.CENTER);

		//scollDst.addMouseListener(this);
		tableDst.addMouseListener(this);


		
		
		
		
		editorGUINow = new EditorGUI();
		editorGUIHistory = new EditorGUI();
		panelLeft = new JPanel();
		panelRight = new JPanel();
		panelLeft.setLayout(new GridLayout(1, 1));// 设置左半部分的布局为1行1列的GridLayout
		
		
		splitSourceDst = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, panelSource, panelDst);

		panelRight.setLayout(new GridLayout(1, 1));
		
		
		panelLeft.setPreferredSize(new Dimension(250,0));
		panelLeft.setMinimumSize((new Dimension(200,0)));
		panelRight.setMinimumSize((new Dimension(300,0)));
		
		panelLeft.add(splitSourceDst);
		panelLeft.setMinimumSize(new Dimension(200,0));
		splitSourceDst.setDividerSize(5);// 设置分隔条的宽度
		
		
		
		tabNowAndHistory=new JTabbedPane();
		
		panelNow=new JPanel(new GridLayout(1,1));
		panelHistory=new JPanel(new GridLayout(1,1));
		panelNow.add(editorGUINow);
		panelHistory.add(editorGUIHistory);
		
		tabNowAndHistory.add(panelNow,"Now");
		tabNowAndHistory.add(panelHistory,"History");
		
		panelRight.add(tabNowAndHistory);

		splitDetail.setLeftComponent(panelLeft);
		splitDetail.setRightComponent(panelRight);
		splitDetail.setDividerSize(5);// 设置分隔条的宽度
		
//		toolBar = new JToolBar(JToolBar.VERTICAL);// 建立工具栏
		
//		panelContentEditor = new JPanel(new BorderLayout());// 同时要设置布局为BorderLayout，否则会是默认布局FlowLayout
		this.add(splitDetail, BorderLayout.CENTER);
//		this.add(toolBar, BorderLayout.EAST);

		

	}
	
//	public BasicEditor() {
//
//		super(new BorderLayout());
//		
//
//		splitDetail = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
//
//		vectorSourceName.add("Index");// 序号
//		vectorSourceName.add("state");// 状态
//		vectorSourceName.add("Data");// 数据
//
//		tabModelSource = new DefaultTableModel(vectorDataSource, vectorSourceName);
//
//		tableSource = new JTable(tabModelSource) {
//			public boolean isCellEditable(int row, int column) {
//				return false;
//			}
//
//			public Class getColumnClass(int column) {
//				Class returnValue;
//				if ((column >= 0) && (column < getColumnCount())) {
//					returnValue = getValueAt(0, column).getClass();
//				} else {
//					returnValue = Object.class;
//				}
//				return returnValue;
//			}
//		};
//		// int a[]=new int[100];
//		tableSource.setPreferredScrollableViewportSize(new Dimension(600, 100));// 设置表格的大小
//		tableSource.setRowHeight(30);// 设置每行的高度为20
//		// localTableEstablished.
//		// setColumnModel(getColumn(localTableEstablished, a));
//		// 设置每行的高度为20
//		tableSource.setRowHeight(0, 20);// 设置第1行的高度为15
//		tableSource.setRowMargin(5);// 设置相邻两行单元格的距离
//		// friends.setRowSelectionAllowed (true);//设置可否被选择.默认为false
//		tableSource.setSelectionBackground(Color.white);// 设置所选择行的背景色
//		tableSource.setSelectionForeground(Color.red);// 设置所选择行的前景色
//		tableSource.setGridColor(Color.black);// 设置网格线的颜色
//		// friends.selectAll ();//选择所有行
//		// friends.setRowSelectionInterval (0,2);//设置初始的选择行,这里是1到3行都处于选择状态
//		// friends.clearSelection ();//取消选择
//		// friends.setDragEnabled (false);//不懂这个
//		// friends.setShowGrid (false);//是否显示网格线
//		tableSource.setShowHorizontalLines(false);// 是否显示水平的网格线
//		tableSource.setShowVerticalLines(true);// 是否显示垂直的网格线
//		// friends.setValueAt ("tt", 0, 0);//设置某个单元格的值,这个值是一个对象
//		tableSource.doLayout();
//		tableSource.setBackground(Color.lightGray);
//		tableSource.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//		rowSorterSource = new TableRowSorter<TableModel>(tabModelSource); // 排序
//		tableSource.setRowSorter(rowSorterSource);
//
//		tableSource.updateUI();
//
//		// local的界面
//		panelSource = new JPanel();
//		labelSource = new JLabel("From Source IP", JLabel.LEFT);
//		labelSource.setFont(new Font("",1,14));//设置字体及字号
//		labelSource.setForeground(Color.BLACK);//设置字体颜色
//		scollSource = new JScrollPane(tableSource, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
//				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		panelSource.setLayout(new BorderLayout());
//		panelSource.add(labelSource, BorderLayout.NORTH);
//		panelSource.add(scollSource, BorderLayout.CENTER);
//
//		tableSource.addMouseListener( this);
//
//		// remote的界面
//		vectorDstName.add("Index");// 序号
//		vectorDstName.add("state");// 状态
//		vectorDstName.add("Data");// 数据
//
//		tabModelDst = new DefaultTableModel(vectorDataDst, vectorDstName);
//
//		tableDst = new JTable(tabModelDst) {
//			public boolean isCellEditable(int row, int column) {
//				return false;
//			}
//
//			public Class getColumnClass(int column) {
//				Class returnValue;
//				if ((column >= 0) && (column < getColumnCount())) {
//					returnValue = getValueAt(0, column).getClass();
//				} else {
//					returnValue = Object.class;
//				}
//				return returnValue;
//			}
//		};
//		// int a[]=new int[100];
//		tableDst.setPreferredScrollableViewportSize(new Dimension(600, 100));// 设置表格的大小
//		tableDst.setRowHeight(30);// 设置每行的高度为20
//		// remoteTableEstablished.
//		// setColumnModel(getColumn(remoteTableEstablished, a));
//		// 设置每行的高度为20
//		tableDst.setRowHeight(0, 20);// 设置第1行的高度为15
//		tableDst.setRowMargin(5);// 设置相邻两行单元格的距离
//		// friends.setRowSelectionAllowed (true);//设置可否被选择.默认为false
//		tableDst.setSelectionBackground(Color.white);// 设置所选择行的背景色
//		tableDst.setSelectionForeground(Color.red);// 设置所选择行的前景色
//		tableDst.setGridColor(Color.black);// 设置网格线的颜色
//		// friends.selectAll ();//选择所有行
//		// friends.setRowSelectionInterval (0,2);//设置初始的选择行,这里是1到3行都处于选择状态
//		// friends.clearSelection ();//取消选择
//		// friends.setDragEnabled (false);//不懂这个
//		// friends.setShowGrid (false);//是否显示网格线
//		tableDst.setShowHorizontalLines(false);// 是否显示水平的网格线
//		tableDst.setShowVerticalLines(true);// 是否显示垂直的网格线
//		// friends.setValueAt ("tt", 0, 0);//设置某个单元格的值,这个值是一个对象
//		tableDst.doLayout();
//		tableDst.setBackground(Color.lightGray);
//		tableDst.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//		rowSorterDst = new TableRowSorter<TableModel>(tabModelDst); // 排序
//		tableDst.setRowSorter(rowSorterDst);
//
//		// 隐藏lacalIndex
//		// TableColumnModel remoteColumnModelEstablished =
//		// tableEstablished.getColumnModel();
//		// TableColumn remoteColumnEstablished =
//		// remoteColumnModelEstablished.getColumn(0);
//		// remoteColumnEstablished.setMinWidth(0);
//		// remoteColumnEstablished.setMaxWidth(0);
//
//		// Vector<Comparable> q11 = new Vector();
//		// q11.add("this is a test");
//		// q11.add("1");
//		// q11.add("1");
//		// vectorDstData.add(q11);
//		//
//		// Vector q22 = new Vector();
//		// q22.add("this is a test aaaaaaaaaaa");
//		// q22.add("2");
//		// q22.add("2");
//		// vectorDstData.add(q22);
//		// Vector q33 = new Vector();
//		// q33.add("this is a test aaaa");
//		// q33.add("3");
//		// q33.add("3");
//		// vectorDstData.add(q33);
//		tableDst.updateUI();
//
//		panelDst = new JPanel();
//		labelDst = new JLabel("From Destination", JLabel.LEFT);
//		labelDst.setFont(new Font("",1,14));//设置字体及字号
//		labelDst.setForeground(Color.BLACK);//设置字体颜色
//		scollDst = new JScrollPane(tableDst, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
//				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		panelDst.setLayout(new BorderLayout());
//		panelDst.add(labelDst, BorderLayout.NORTH);
//		panelDst.add(scollDst, BorderLayout.CENTER);
//
//		tableDst.addMouseListener(this);
//
//		editorGUINow = new EditorGUI();
//		editorGUIHistory = new EditorGUI();
//		panelLeft = new JPanel();
//		panelRight = new JPanel();
//		panelLeft.setLayout(new GridLayout(1, 1));// 设置左半部分的布局为1行1列的GridLayout
//		splitSourceDst = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, panelDst, panelSource);
//
//		panelRight.setLayout(new GridLayout(1, 1));
//		panelLeft.add(splitSourceDst);
//		panelLeft.setPreferredSize(new Dimension(250,0));
//		panelLeft.setMinimumSize((new Dimension(200,0)));
//		panelRight.setMinimumSize((new Dimension(300,0)));
//		
//		splitSourceDst.setDividerSize(5);// 设置分隔条的宽度
//		
//		
//		tabNowAndHistory=new JTabbedPane();
//		
//		panelNow=new JPanel(new GridLayout(1,1));
//		panelHistory=new JPanel(new GridLayout(1,1));
//		panelNow.add(editorGUINow);
//		panelHistory.add(editorGUIHistory);
//		
//		tabNowAndHistory.add(panelNow,"Now");
//		tabNowAndHistory.add(panelHistory,"History");
//		tabNowAndHistory.remove(panelHistory);
//		panelRight.add(tabNowAndHistory);
//
//		splitDetail.setLeftComponent(panelLeft);
//		splitDetail.setRightComponent(panelRight);
//		splitDetail.setDividerSize(5);// 设置分隔条的宽度
//		
////		toolBar = new JToolBar(JToolBar.VERTICAL);// 建立工具栏
//		
////		panelContentEditor = new JPanel(new BorderLayout());// 同时要设置布局为BorderLayout，否则会是默认布局FlowLayout
//		this.add(splitDetail, BorderLayout.CENTER);
//
//		
//
//	}
	public void RefreshFromEstablish(int tIndex){
		tableDst.clearSelection();
		tableSource.clearSelection();
		vectorDataSource.clear();
		vectorDataDst.clear();
		NowSocketInfo = manageSocketData.GetSocketInfoEstablish(tIndex);
		NowSocketIndex = tIndex;
		if (NowSocketInfo != null) {
			
			RefreshDstSource( tIndex);
		}
		tableSource.updateUI();
		tableDst.updateUI();
	}
	public void RefreshFromClose(int tIndex){
		tableDst.clearSelection();
		tableSource.clearSelection();
		vectorDataSource.clear();
		vectorDataDst.clear();
		NowSocketInfo = manageSocketData.GetSocketInfoClose(tIndex);
		NowSocketIndex = tIndex;
		if (NowSocketInfo != null) {
			
			RefreshDstSource( tIndex);
		}
		tableSource.updateUI();
		tableDst.updateUI();
	}
	
private  void RefreshDstSource(int tIndex) {
	// TODO Auto-generated method stub
		Iterator iter;
		Vector vTmp;
		iter = NowSocketInfo.byteDataSource.entrySet().iterator();
		
		while (iter.hasNext()) {
			
			Map.Entry entry = (Entry) iter.next();
			if ((int)entry.getKey()<0) {
				continue;
			}
			vTmp = new Vector();
			vTmp.addElement(entry.getKey());
			vTmp.addElement(((SocketByteData) entry.getValue()).proxyDataStatus);
			String sTmp = new String(((SocketByteData) entry.getValue()).data);
			vTmp.addElement(sTmp);
			vectorDataSource.addElement(vTmp);
		}
		iter = NowSocketInfo.byteDataDst.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Entry) iter.next();
			if ((int)entry.getKey()<0) {
				continue;
			}
			vTmp = new Vector();
			vTmp.addElement(entry.getKey());
			vTmp.addElement(((SocketByteData) entry.getValue()).proxyDataStatus);
			String sTmp = new String(((SocketByteData) entry.getValue()).data);
			vTmp.addElement(sTmp);
			vectorDataDst.addElement(vTmp);
		}
	return;
}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

		if (e.getSource() == tableDst) {
			if(tableDst.getSelectedRowCount()==1){
				int tIndex=(int) tableDst.getValueAt(tableDst.getSelectedRow(), 0);
				if (NowSocketInfo.byteDataDst.containsKey(-tIndex)) {
//					System.out.println(new String(NowSocketInfo.byteDataDst.get(2).data)+tIndex);
					String sTmp=new String(NowSocketInfo.byteDataDst.get(-tIndex).data);
					if(tabNowAndHistory.getComponentCount()==1){
						tabNowAndHistory.add(panelHistory,"History");
						editorGUIHistory.SetData(sTmp);
					}

				}
				else {
					tabNowAndHistory.remove(panelHistory);
				}
				originStr=new String(NowSocketInfo.byteDataDst.get(tIndex).data);
				editorGUINow.SetData(originStr);
			}
			tableSource.clearSelection();  //取消选择
			return;
		}
		if (e.getSource() == tableSource) {

			if (tableSource.getSelectedRowCount() == 1) {
				int tIndex = (int) tableSource.getValueAt(tableSource.getSelectedRow(), 0);
				
				if (NowSocketInfo.byteDataSource.containsKey(-tIndex)) {
					String sTmp=new String(NowSocketInfo.byteDataSource.get(-tIndex).data);
					if(tabNowAndHistory.getComponentCount()==1){
						tabNowAndHistory.add(panelHistory,"History");
						editorGUIHistory.SetData(sTmp);
					}

				}
				else {
					tabNowAndHistory.remove(panelHistory);
				}
				originStr = new String(NowSocketInfo.byteDataSource.get(tIndex).data);
				editorGUINow.SetData(originStr);
			}
			tableDst.clearSelection();  //取消选择
			return;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
