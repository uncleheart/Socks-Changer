package pers.ui.proxy.options;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.MathContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.python.antlr.ast.boolopType;

import pers.data.InterceptStatus;
import pers.data.ProxyDataStatus;
import pers.data.SocketInfo;
import pers.dll.infc.NewConData;
import pers.ui.proxy.options.enumsum.BooleanOperator;
import pers.ui.proxy.options.enumsum.MatchRelationship;
import pers.ui.proxy.options.enumsum.MatchType;



public class PanelOptions extends JPanel implements MouseListener,ItemListener{

	//private JPanel panelOptions;
	
	
	protected JPanel panelProxyListeners;
	private JPanel panelInterceptClientRequests;
	private JPanel panelChange;//添加ip等规则的JPanel
	private JPanel panelLoadScript;
	
	private JButton button;//测试时所用
	
	public String RemoteIPAddress;
	public int RemotePort=20;

	
	JPanel panelProxysetNameAndExplain;
	//JPanel panelsetProxyExplain;
	JLabel labelProxyListenersName;//名为ProxyListeners的标签
	JLabel labelProxyListenersExplain;//ProxyListeners的说明性文字

	JTextArea textAreaProxyListenersExplain;
	JPanel panelProxyListenersCombination;//ProxyListeners下面部分组合的JPanel

	JLabel labelProxyListenersPort;
	public	JTextField textFieldProxyListenersPort;
	JLabel labelProxyListenersIP;
	public	JTextField textFieldProxyListenersIP;
	
	//InterceptClientRequests界面
	
	
	JLabel labelInterceptClientName;//名为InterceptClientName的标签
	JLabel labelInterceptClientExplain;//的说明性文字
	JPanel panelClientTopExplain;//放上面说明性的文字
	JPanel panelClientCombination;//组合的JPanel
	JPanel panelClientUnderExplain;//放下面俩复选框
	JButton buttonClientAdd;
	JButton buttonClientRemove;
	JPanel panelClientSetButton;//放button
	JScrollPane scrollClient;
	String [] tableNamesClient = {"Enabled","Operator","Match type","Relationship","Condition"};	
	JCheckBox checkBoxClient1;
	JCheckBox checkBoxClient2;
	JCheckBox checkBoxClient3;
	JPanel paneltableClient;
	protected Vector vectorClientData = new Vector();
	
	
	protected Vector vectorClientName = new Vector();
	protected TableModel1 tabModelClient;
	Vector<InterceptRuler> interceptRulerArray=new Vector<InterceptRuler>();
	protected JTable tableClient;
	protected RowSorter<TableModel1> rowSorterClient;
	

	
	
	//change部分
	JLabel labelChangeName;//change标签
	JLabel labelChangeExplain;//change的说明性文字
	JPanel panelChangesetNameAndExplain;
	JPanel panelChangeCombination;//Change下面部分组合的JPanel
	JPanel panelChangeSetButton;//放button
	JButton buttonChangeAdd;
	JButton buttonChangeRemove;
	JScrollPane scrollChange;
	JPanel panelTableChange;
	protected Vector vectorChangeData = new Vector();
	protected Vector vectorChangeName = new Vector();
	protected TableModel1 tabModelChange;
	protected JTable tableChange;
	protected RowSorter<TableModel1> rowSorterChange;
	String [] tableChangeNames = {"Status","Source Domain","Source IP","Source Port","Destination Domain","Destination IP","Destination Port","Domain to modify","IP to modify","Port to modify"};
	
	
	//LoadScript部分
	JLabel labelLoadScriptName;
	JLabel labelLoadScriptExplain;
	JPanel panelLoadScriptsetNameAndExplain;
	JPanel panelLoadScriptCombination;
	JPanel panelLoadScriptSetButton;
	JButton buttonLoadScriptAdd;
	JButton buttonLoadScriptRemove;
	JScrollPane scrollLoadScript;
	JPanel panelTableLoadScript;
	protected Vector vectorLoadScriptData = new Vector();
	protected Vector vectorLoadScriptName = new Vector();
	protected TableModel1 tabModelLoadScript;
	protected JTable tableLoadScript;
	protected RowSorter<TableModel1> rowSorterLoadScript;
	String [] tableLoadScriptNames = {"                             Status","                              File path"};
	
	JFileChooser fileChooser;//=new JFileChooser();//文件对话框
	
	public PanelOptions(){
		
		RemoteIPAddress=new String("127.0.0.1");
		
		button=new JButton();//测试、、、、、、、、、、、、、、、、、、、、、、、、、、、
		panelProxyListeners=new JPanel(new BorderLayout(5,10));
		
	
		
		
		
		labelProxyListenersName=new JLabel("Proxy Listeners");
		labelProxyListenersName.setFont(new Font("",1,17));//设置字体及字号
		labelProxyListenersName.setForeground(Color.BLACK);//设置字体颜色
		
		panelProxysetNameAndExplain=new JPanel(new GridLayout(2,1,5,5));
		
		
		labelProxyListenersExplain=new JLabel(">>>>用于修改目的ip与端口设置，复选框未选中时不生效");
		labelProxyListenersExplain.setFont(new Font("",1,14));//设置字体及字号
		labelProxyListenersExplain.setForeground(Color.BLACK);//设置字体颜色
		
		panelProxysetNameAndExplain.add(labelProxyListenersName);
		panelProxysetNameAndExplain.add(labelProxyListenersExplain);

		labelProxyListenersPort=new JLabel("Port:");
		textFieldProxyListenersPort=new JTextField();
		textFieldProxyListenersPort.setText("12345");
		textFieldProxyListenersPort.setPreferredSize(new Dimension(200,30));

		labelProxyListenersIP=new JLabel("IP:");
		textFieldProxyListenersIP=new JTextField();
		textFieldProxyListenersIP.setText("0.0.0.0");
		textFieldProxyListenersIP.setPreferredSize(new Dimension(200,30));
		
		panelProxyListenersCombination=new JPanel();
		GridBagLayout panelProxyListenersCombinationlayoutOptions = new GridBagLayout();
		panelProxyListenersCombination.setLayout(panelProxyListenersCombinationlayoutOptions);
		
		GridBagConstraints panelProxyListenersCombinatiogridBagConstraints=new GridBagConstraints();
		panelProxyListenersCombinatiogridBagConstraints.fill = GridBagConstraints.BOTH;
		panelProxyListenersCombinatiogridBagConstraints.insets = new Insets(0, 0, 5, 5);// 设置控件的空白（上，左，下，右）
		panelProxyListenersCombinatiogridBagConstraints.gridwidth=2;//该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
		panelProxyListenersCombinatiogridBagConstraints.gridheight = 3;// 占用2行
		panelProxyListenersCombinatiogridBagConstraints.gridx = 0;// 起始点为第1列
		panelProxyListenersCombinatiogridBagConstraints.gridy = 0;// 起始点为第1行
		
		panelProxyListenersCombinatiogridBagConstraints.weightx = 0;//该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		panelProxyListenersCombinatiogridBagConstraints.weighty=0;//该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		panelProxyListenersCombinationlayoutOptions.setConstraints(labelProxyListenersIP, panelProxyListenersCombinatiogridBagConstraints);//设置组件
		
		//GridBagConstraints gridBagInterceptClientRequests=new GridBagConstraints();
		panelProxyListenersCombinatiogridBagConstraints.insets = new Insets(5, 5, 5, 800);// 设置控件的空白
		panelProxyListenersCombinatiogridBagConstraints.gridwidth=2;//该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
		panelProxyListenersCombinatiogridBagConstraints.gridheight = 3;// 占用2行
		panelProxyListenersCombinatiogridBagConstraints.gridx = 2;// 起始点为第1列
		panelProxyListenersCombinatiogridBagConstraints.gridy = 0;// 起始点为第2行
		panelProxyListenersCombinatiogridBagConstraints.weightx = 0;//该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		panelProxyListenersCombinatiogridBagConstraints.weighty=0;//该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		panelProxyListenersCombinationlayoutOptions.setConstraints(textFieldProxyListenersIP, panelProxyListenersCombinatiogridBagConstraints);//设置组件
		
		panelProxyListenersCombinatiogridBagConstraints.insets = new Insets(5, 0, 5, 5);// 设置控件的空白
		panelProxyListenersCombinatiogridBagConstraints.gridwidth=2;//该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
		panelProxyListenersCombinatiogridBagConstraints.gridheight = 1;// 占用2行
		panelProxyListenersCombinatiogridBagConstraints.gridx = 0;// 起始点为第1列
		panelProxyListenersCombinatiogridBagConstraints.gridy = 3;// 起始点为第2行
		panelProxyListenersCombinatiogridBagConstraints.weightx = 0;//该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		panelProxyListenersCombinatiogridBagConstraints.weighty=0;//该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		panelProxyListenersCombinationlayoutOptions.setConstraints(labelProxyListenersPort, panelProxyListenersCombinatiogridBagConstraints);//设置组件
		
		panelProxyListenersCombinatiogridBagConstraints.insets = new Insets(5, 5, 5, 800);// 设置控件的空白
		panelProxyListenersCombinatiogridBagConstraints.gridwidth=2;//该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
		panelProxyListenersCombinatiogridBagConstraints.gridheight = 1;// 占用2行
		panelProxyListenersCombinatiogridBagConstraints.gridx = 2;// 起始点为第1列
		panelProxyListenersCombinatiogridBagConstraints.gridy = 3;// 起始点为第2行
		panelProxyListenersCombinatiogridBagConstraints.weightx = 0;//该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		panelProxyListenersCombinatiogridBagConstraints.weighty=0;//该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		panelProxyListenersCombinationlayoutOptions.setConstraints(textFieldProxyListenersPort, panelProxyListenersCombinatiogridBagConstraints);//设置组件
		
		
		
		panelProxyListenersCombination.add(labelProxyListenersIP);
		panelProxyListenersCombination.add(textFieldProxyListenersIP);
		panelProxyListenersCombination.add(labelProxyListenersPort);
		panelProxyListenersCombination.add(textFieldProxyListenersPort);
		
			
		panelProxyListeners.add(panelProxysetNameAndExplain,BorderLayout.NORTH);
		panelProxyListeners.add(panelProxyListenersCombination,BorderLayout.CENTER); 

		panelInterceptClientRequests=new JPanel(new BorderLayout(5,10));
		
		//InterceptClientRequests界面
		
		panelClientTopExplain=new JPanel(new GridLayout(3,1,5,5));
				
		labelInterceptClientName=new JLabel("InterceptClientRequests");
		labelInterceptClientName.setFont(new Font("",1,17));
		labelInterceptClientName.setForeground(Color.BLACK);
		
		labelInterceptClientExplain=new JLabel(">>>>设置拦截规则");
		labelInterceptClientExplain.setFont(new Font("",1,14));
		labelInterceptClientExplain.setForeground(Color.BLACK);
		
		checkBoxClient1=new JCheckBox("Intercept requests based on the following rlues");
		panelClientTopExplain.add(labelInterceptClientName);
		panelClientTopExplain.add(labelInterceptClientExplain);
		panelClientTopExplain.add(checkBoxClient1);		
		panelClientCombination=new JPanel(new BorderLayout());
		panelClientSetButton=new JPanel(new GridLayout(5,1));
		buttonClientAdd=new JButton("Add");
		buttonClientRemove=new JButton("Remove");
		panelClientSetButton.add(buttonClientAdd);
		panelClientSetButton.add(buttonClientRemove);
		
		buttonClientAdd.addMouseListener(this);
		buttonClientRemove.addMouseListener(this);

		scrollClient=new JScrollPane();//滚动窗格

		
		Object[] tableDataClient={true,"And","Destination IP","Match","192.168.233.128/32"};
		interceptRulerArray.addElement(new InterceptRuler(BooleanOperator.AND,MatchType.DstIP,MatchRelationship.Match,"192.168.233.128/32"));
		tabModelClient=new TableModel1(tableNamesClient, tableDataClient);
		tableClient = new JTable(tabModelClient);
		SetTableName(tableClient, tableNamesClient);	

		tableClient.setRowHeight(30);// 设置每行的高度为20
		tableClient.setShowHorizontalLines(false);// 是否显示水平的网格线
		tableClient.setShowVerticalLines(true);// 是否显示垂直的网格线
		// friends.setValueAt ("tt", 0, 0);//设置某个单元格的值,这个值是一个对象
		tableClient.doLayout();
		tableClient.setBackground(Color.lightGray);
		// JTable最好加在JScrollPane上
		scrollClient = new JScrollPane(tableClient, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		paneltableClient = new JPanel();
		paneltableClient.setLayout(new BorderLayout());
		paneltableClient.add(scrollClient, BorderLayout.CENTER);
		
		panelClientCombination.add(panelClientSetButton,BorderLayout.WEST);
		panelClientCombination.add(scrollClient,BorderLayout.CENTER);
		
		panelClientUnderExplain=new JPanel(new GridLayout(2,1,5,5));
		checkBoxClient2=new JCheckBox("Automatically fix missing or superfluous new lines at end of request");
		checkBoxClient3=new JCheckBox("Automatically update Content-Length header when the request is edited");
		panelClientUnderExplain.add(checkBoxClient2);
		panelClientUnderExplain.add(checkBoxClient3);
		panelInterceptClientRequests.setPreferredSize(new Dimension(0,400));
		panelInterceptClientRequests.add(panelClientTopExplain,BorderLayout.NORTH);
		panelInterceptClientRequests.add(panelClientCombination,BorderLayout.CENTER);
		panelInterceptClientRequests.add(panelClientUnderExplain,BorderLayout.SOUTH);


		panelChange=new JPanel(new BorderLayout(5,10));

		labelChangeName=new JLabel("Change IP or Port");
		labelChangeName.setFont(new Font("",1,17));//设置字体及字号
		labelChangeName.setForeground(Color.BLACK);//设置字体颜色
		
		panelChangesetNameAndExplain=new JPanel(new GridLayout(2,1,5,5));
		
		labelChangeExplain=new JLabel(">>>>将满足条件的连接的目的IP地址与端口号修改为指定值，为空代表任意或者不修改。复选框未选中时不修改。");
		labelChangeExplain.setFont(new Font("",1,14));//设置字体及字号
		labelChangeExplain.setForeground(Color.BLACK);//设置字体颜色
		
	
		panelChangesetNameAndExplain.add(labelChangeName);
		panelChangesetNameAndExplain.add(labelChangeExplain);
		panelChangeCombination=new JPanel(new BorderLayout());
		panelChangeSetButton=new JPanel(new GridLayout(6,1));
		
		buttonChangeAdd=new JButton("Add");
		buttonChangeRemove=new JButton("Remove");
		
		panelChangeSetButton.add(buttonChangeAdd);
		panelChangeSetButton.add(buttonChangeRemove);

		buttonChangeAdd.addMouseListener(this);
		buttonChangeRemove.addMouseListener(this);
		
		Object[] tableDataChange={true,"","","","","","",""};
		
		
		tabModelChange = new TableModel1(tableChangeNames,null);
		tableChange = new JTable(tabModelChange);
		SetTableName(tableChange, tableChangeNames);
		
		tableChange.setPreferredScrollableViewportSize(new Dimension(600, 100));// 设置表格的大小
		tableChange.setRowHeight(30);// 设置每行的高度为20
		tableChange.setRowHeight(0, 20);// 设置第1行的高度为15
		tableChange.setRowMargin(5);// 设置相邻两行单元格的距离
		tableChange.setGridColor(Color.black);// 设置网格线的颜色
		tableChange.setShowHorizontalLines(false);// 是否显示水平的网格线
		tableChange.setShowVerticalLines(true);// 是否显示垂直的网格线
		// friends.setValueAt ("tt", 0, 0);//设置某个单元格的值,这个值是一个对象
		tableChange.doLayout();
		tableChange.setBackground(Color.lightGray);

		
		// JTable最好加在JScrollPane上
		scrollChange = new JScrollPane(tableChange, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panelTableChange = new JPanel();
		panelTableChange.setLayout(new BorderLayout());
		panelTableChange.add(scrollChange, BorderLayout.CENTER);

		tableChange.updateUI();
		
		panelChangeCombination.add(panelChangeSetButton,BorderLayout.WEST);
		panelChangeCombination.add(panelTableChange,BorderLayout.CENTER);
		
		
		//panelProxysetNameAndExplain.setPreferredSize(new Dimension(0,(int)(panelProxyListeners.getHeight()*0.4)));
		panelChange.setPreferredSize(new Dimension(0,290));
		
		panelChange.add(panelChangesetNameAndExplain,BorderLayout.NORTH);
		panelChange.add(panelChangeCombination,BorderLayout.CENTER);
		
		
		
		//LoadScript部分
		panelLoadScript=new JPanel(new BorderLayout(5,10));

		labelLoadScriptName=new JLabel("LoadScriptFile");
		labelLoadScriptName.setFont(new Font("",1,17));//设置字体及字号
		labelLoadScriptName.setForeground(Color.BLACK);//设置字体颜色
		
		panelLoadScriptsetNameAndExplain=new JPanel(new GridLayout(2,1,5,5));
		
		labelLoadScriptExplain=new JLabel(">>>>加载脚本文件。复选框未选中时不加载");
		labelLoadScriptExplain.setFont(new Font("",1,14));//设置字体及字号
		labelLoadScriptExplain.setForeground(Color.BLACK);//设置字体颜色
		
	
		panelLoadScriptsetNameAndExplain.add(labelLoadScriptName);
		panelLoadScriptsetNameAndExplain.add(labelLoadScriptExplain);
		panelLoadScriptCombination=new JPanel(new BorderLayout());
		panelLoadScriptSetButton=new JPanel(new GridLayout(6,1));
		
		buttonLoadScriptAdd=new JButton("Add");
		buttonLoadScriptRemove=new JButton("Remove");
		
		panelLoadScriptSetButton.add(buttonLoadScriptAdd);
		panelLoadScriptSetButton.add(buttonLoadScriptRemove);

		buttonLoadScriptAdd.addMouseListener(this);
		buttonLoadScriptRemove.addMouseListener(this);
		
		Object[] tableDataLoadScript={true,""};
		
		
		tabModelLoadScript = new TableModel1(tableLoadScriptNames,null);
		tableLoadScript = new JTable(tabModelLoadScript);
		SetTableName(tableLoadScript, tableLoadScriptNames);
		
		tableLoadScript.setPreferredScrollableViewportSize(new Dimension(600, 100));// 设置表格的大小
		tableLoadScript.setRowHeight(30);// 设置每行的高度为20
		tableLoadScript.setRowHeight(0, 20);// 设置第1行的高度为15
		tableLoadScript.setRowMargin(5);// 设置相邻两行单元格的距离
		tableLoadScript.setGridColor(Color.black);// 设置网格线的颜色
		tableLoadScript.setShowHorizontalLines(false);// 是否显示水平的网格线
		tableLoadScript.setShowVerticalLines(true);// 是否显示垂直的网格线
		// friends.setValueAt ("tt", 0, 0);//设置某个单元格的值,这个值是一个对象
		tableLoadScript.doLayout();
		tableLoadScript.setBackground(Color.lightGray);

		
		// JTable最好加在JScrollPane上
		scrollLoadScript = new JScrollPane(tableLoadScript, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panelTableLoadScript = new JPanel();
		panelTableLoadScript.setLayout(new BorderLayout());
		panelTableLoadScript.add(scrollLoadScript, BorderLayout.CENTER);

		tableLoadScript.updateUI();
		
		panelLoadScriptCombination.add(panelLoadScriptSetButton,BorderLayout.WEST);
		panelLoadScriptCombination.add(panelTableLoadScript,BorderLayout.CENTER);
		
		
		//panelProxysetNameAndExplain.setPreferredSize(new Dimension(0,(int)(panelProxyListeners.getHeight()*0.4)));
		panelLoadScript.setPreferredSize(new Dimension(0,290));
		
		panelLoadScript.add(panelLoadScriptsetNameAndExplain,BorderLayout.NORTH);
		panelLoadScript.add(panelLoadScriptCombination,BorderLayout.CENTER);
		
		
		
		
		//this.setPreferredSize(new Dimension(800, 1000));
		//JPanel ss=new JPanel();
		GridBagLayout layoutOptions = new GridBagLayout();
		this.setLayout(layoutOptions);
		
		GridBagConstraints gridBagConstraints=new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(0, 0, 10, 0);// 设置控件的空白（上，左，下，右）
		gridBagConstraints.gridwidth=0;//该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
		gridBagConstraints.gridheight = 2;// 占用2行
		gridBagConstraints.gridx = 0;// 起始点为第1列
		gridBagConstraints.gridy = 0;// 起始点为第1行
		
		gridBagConstraints.weightx = 1;//该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		gridBagConstraints.weighty=0;//该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		layoutOptions.setConstraints(panelProxyListeners, gridBagConstraints);//设置组件
		
		//GridBagConstraints gridBagInterceptClientRequests=new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 0, 10, 0);// 设置控件的空白
		gridBagConstraints.gridwidth=0;//该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
		gridBagConstraints.gridheight = 2;// 占用2行
		gridBagConstraints.gridx = 0;// 起始点为第1列
		gridBagConstraints.gridy = 2;// 起始点为第2行
		gridBagConstraints.weightx = 1;//该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		gridBagConstraints.weighty=0;//该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		layoutOptions.setConstraints(panelInterceptClientRequests, gridBagConstraints);//设置组件
		
		gridBagConstraints.insets = new Insets(5, 0, 5, 0);// 设置控件的空白
		gridBagConstraints.gridwidth=0;//该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
		gridBagConstraints.gridheight = 2;// 占用2行
		gridBagConstraints.gridx = 0;// 起始点为第1列
		gridBagConstraints.gridy = 4;// 起始点为第2行
		gridBagConstraints.weightx = 1;//该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		gridBagConstraints.weighty=0;//该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		layoutOptions.setConstraints(panelChange, gridBagConstraints);//设置组件
		
		gridBagConstraints.insets = new Insets(5, 0, 5, 0);// 设置控件的空白
		gridBagConstraints.gridwidth=0;//该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
		gridBagConstraints.gridheight = 2;// 占用3行
		gridBagConstraints.gridx = 0;// 起始点为第1列
		gridBagConstraints.gridy = 6;// 起始点为第2行
		gridBagConstraints.weightx = 1;//该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		gridBagConstraints.weighty=0;//该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		layoutOptions.setConstraints(panelLoadScript, gridBagConstraints);//设置组件
		
		this.add(panelProxyListeners);
		this.add(panelInterceptClientRequests);
		this.add(panelChange);
		this.add(panelLoadScript);
	}

	//设置table表头
	private void SetTableName(JTable table,String[] tableName){
		int n=tableName.length;
	//	System.out.println(n);
		for(int i=0;i<n;i++){
			table.getColumnModel().getColumn(i).setHeaderValue(tableName[i]);
		}
		
	}
 
	class InterceptRuler{
//		public boolean abled;
		public BooleanOperator booleanOperator;
		public MatchType matchType;
		public MatchRelationship matchRelationship;
		public String strMatchCondition;
		public InterceptRuler(BooleanOperator tBooleanOperator,MatchType tMatchType,MatchRelationship tMatchRelationship,String tStrMatchCondition ){
//			abled=tAbled;
			booleanOperator=tBooleanOperator;
			matchType=tMatchType;
			matchRelationship=tMatchRelationship;
			strMatchCondition=tStrMatchCondition;
		}
	}
	
	//给新建的窗口调用，添加客户端拦截的规则
	public void AddInteceptClientRuler(BooleanOperator booleanOperator,MatchType matchType,MatchRelationship matchRelationship,String strMatchCondition)
	{
		Object[]data={true,booleanOperator,matchType,matchRelationship,strMatchCondition};
		interceptRulerArray.addElement(new InterceptRuler(booleanOperator,matchType,matchRelationship,strMatchCondition));
		tabModelClient.addRow(data);
		tableClient.updateUI();
	}
	
	//给主界面使用，给定参数（源IP，端口，目的IP端口等等信息，，然后判断是否修改ip地址或者端口号
	public int HandleChange(NewConData newConData){
		int res=0;
		int size=tabModelChange.vector.size();
		if (size<1) {
			return 0;
		}
		Vector vTmp;
		for(int i=0;i<size;i++){
			vTmp=(Vector) tabModelChange.vector.get(i);
			String string=(String)vTmp.get(2);
			System.out.println(string);
			if(!(boolean)vTmp.get(0)){  //这个条件没有激活
				continue;
			}
			if (!((String)vTmp.get(1)).isEmpty() && !newConData.SourceHostname.equals((String)vTmp.get(1))) {
				continue;
			}
			if (!((String)vTmp.get(2)).isEmpty() && !newConData.SourceIPAddress.equals((String)vTmp.get(2))) {
				continue;
			}
			if (!((String)vTmp.get(3)).isEmpty() && newConData.SourcePort!=Integer.parseInt((String)vTmp.get(3))) {
				continue;
			}
			if (!((String)vTmp.get(4)).isEmpty() && !newConData.DstHostname.equals((String)vTmp.get(4))) {
				continue;
			}
			if (!((String)vTmp.get(5)).isEmpty() && !newConData.DstIPAddress.equals((String)vTmp.get(5))) {
				continue;
			}
			if (!((String)vTmp.get(6)).isEmpty() && newConData.DstPort!=Integer.parseInt((String)vTmp.get(6))) {
				continue;
			}
			if (!((String)vTmp.get(7)).isEmpty() ) {
				newConData.DstHostname=(String)vTmp.get(7);
				res|=0b100;
			}
			if (!((String)vTmp.get(8)).isEmpty() ) {
				newConData.DstIPAddress=(String)vTmp.get(8);
				res|=0b1000;
				break;
			}
			if (!((String)vTmp.get(9)).isEmpty() ) {
				newConData.DstPort=Integer.parseInt((String)vTmp.get(9));
				res|=0b10000;
				break;
			}
		}
		return res;
	}
	
	boolean  MatchRule(NewConData newConData,MatchType matchType,String strCondition)
	{
		switch (matchType) {
		case SourceIP:
			return IPMatch.isInRange(newConData.SourceIPAddress, strCondition);
		case SouceHostname:
			return newConData.SourceHostname.equalsIgnoreCase(strCondition);
		case SourcePort:
			return newConData.SourcePort==Integer.parseInt(strCondition);
		case DstIP:
			return IPMatch.isInRange(newConData.DstIPAddress, strCondition);
		case DstHostname:
			return newConData.DstHostname.equalsIgnoreCase(strCondition);
		case DstPort:
			return newConData.DstPort==Integer.parseInt(strCondition);
		default:
			return false;
		}
		
	}

	//给主界面使用，给定参数（源ip 端口，目的ip端口  等等）
	public InterceptStatus HandleNewCon(NewConData newConData)
	{
		int size=interceptRulerArray.size();
		if(size<=0){
			return InterceptStatus.INTERCEPT_ALL; 
		}
		boolean boolRes=false,boolTmp;
		InterceptRuler vTmp=(InterceptRuler)interceptRulerArray.get(0);
		boolTmp=MatchRule(newConData, vTmp.matchType,vTmp.strMatchCondition);
		if((vTmp.matchRelationship)==MatchRelationship.Match)
		{
			boolRes=boolTmp;
		}
		else {
			boolRes=!boolTmp;
		}
		for (int i = 1; i <size ; i++) {
		//	boolTmp=MatchRule(newConData, matchType, strCondition)
			vTmp=(InterceptRuler)interceptRulerArray.get(i);
			boolTmp=MatchRule(newConData, vTmp.matchType,vTmp.strMatchCondition);
			if((vTmp.matchRelationship)==MatchRelationship.Match)
			{
				boolTmp= boolTmp ;
			}
			else {
				boolTmp=!boolTmp ;
			}
			if((vTmp.booleanOperator)==BooleanOperator.OR)
			{
				if (boolRes==true) {
//					return true;
					
				}
				else{
					boolRes=boolTmp;
				}
			}
			else {
				boolRes= boolTmp&boolRes ;
			}
			
		}
		
		if (boolRes) {
			return InterceptStatus.INTERCEPT_ALL;
		}
		else{
			return InterceptStatus.INTERCEPT_NONE;
		}
	
	}
	public byte[] Handle(int tIndex, byte[] srcData, SocketInfo tSocketInfo) {
	// TODO Auto-generated method stub
	if (tSocketInfo.port==50001) {
		return "successed\n".getBytes();
	}
	
	return srcData;
}
	byte[] LoadJarScript(int tIndex,byte[] srcData,String jarPath,SocketInfo tSocketInfo){
        byte[] res;
        res=Handle(tIndex,srcData,tSocketInfo);  
        return res;
//        URL url1;
//		try {
//			url1 = new URL("file:"+jarPath);
//
//        URLClassLoader myClassLoader1 = new URLClassLoader(new URL[] { url1 }, Thread.currentThread()  
//                .getContextClassLoader());  
//        Class<?> myClass1 = myClassLoader1.loadClass("pers.ui.proxy.options.HandleData");  
//        DataInface action1 = (DataInface) myClass1.newInstance();  
//
//        byte[] res = action1.Handle(tIndex,srcData,tSocketInfo);  
//
//        return res;
//		} catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}  
	}
	

	//给主界面使用，通过脚本修改数据
	public byte[] HandleNewData(int tIndex, ProxyDataStatus tProxyDataStatus, byte[] tData,SocketInfo tSocketInfo)	{
		byte[] resData=tData;
		int size=tabModelLoadScript.vector.size();
		if(size<=0){
			return resData; 
		}
		Vector vTmp;

		for (int i = 0; i <size ; i++) {
		//	boolTmp=MatchRule(newConData, matchType, strCondition)
			vTmp=(Vector) tabModelLoadScript.vector.get(i);
			resData=LoadJarScript(tIndex,resData, (String)vTmp.get(1), tSocketInfo);
			
		}
		return resData;
	}
	 public static void main(String[] args) {
		PanelOptions panelOptions=new PanelOptions();
		panelOptions.LoadJarScript(1, "test".getBytes(), "g:/test.jar", null);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
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
	if(e.getSource()==buttonClientAdd)	{//Client的Add按钮事件
		new InteceptClientRulerAdd(this);
	}
	
	if(e.getSource()==buttonClientRemove){//Client的Remove按钮事件
		if(tableClient.getSelectedRow()==-1){
			System.out.println("没有选中要删除项");
		}
		else{
			tabModelClient.removeRow(tableClient.getSelectedRow());
			tableClient.updateUI();
		}
		return;	
	}
	
	
	
	
	
	if(e.getSource()==buttonChangeAdd){//Change的Add按钮事件
		
		Object[] dataTemp = {true,"","","","","","","","",""};
		
		tabModelChange.addRow(dataTemp);
		tableChange.updateUI();
		return;
	}
	
	if(e.getSource()==buttonChangeRemove){//Change的Remove按钮事件
		if(tableChange.getSelectedRow()==-1){
			System.out.println("没有选中要删除项");
		}

		else{
			tabModelChange.removeRow(tableChange.getSelectedRow());
			tableChange.updateUI();
		}
		return;	
	}
	
	if(e.getSource()==buttonLoadScriptAdd){//LoadScript的Add按钮事件
		
		//Object[] dataTemp = {true,"","","","","","","","",""};
		fileChooser=new JFileChooser();
		int select=fileChooser.showOpenDialog(null);
		if(select==fileChooser.APPROVE_OPTION){
			
			File fileTemp=fileChooser.getSelectedFile();
			if(!fileTemp.exists()){
				System.out.println("该文件不存在");
			}
			else{
				String StrFileName=fileTemp.getAbsolutePath();
				Object[] dataTemp = {true,StrFileName};
				tabModelLoadScript.addRow(dataTemp);
			}
			
		}
		
		
		
		tableLoadScript.updateUI();
		return;
	}
	
	if(e.getSource()==buttonLoadScriptRemove){//LoadScript的Remove按钮事件
		if(tableLoadScript.getSelectedRow()==-1){
			System.out.println("没有选中要删除项");
		}

		else{
			tabModelLoadScript.removeRow(tableLoadScript.getSelectedRow());
			tableLoadScript.updateUI();
		}
		return;	
	}
	
	
}


	@Override
	public void itemStateChanged(ItemEvent arg0) {
	}
	
}


/**
 * 
 * AbstractTableModel:中的抽象方法：
 * getColumnCount(),getRowCount(),getValueAt()：
 * 当JTable调用方法updateUI()的时候，就会执行这些方法。
 * 
 * 
 * updateUI()会把父类中的方法全部执行一遍 
 * @author Administrator
 *
 */
class TableModel1 extends AbstractTableModel{
	public Vector vector = new Vector();
	private String[] columnNames;// = {"用户名","密码","是否可用"};
	//private PanelOptions panelOptionstemp;
/**
 * ------------------------------------------------------
 * 重写父类中的抽象方法
 * 
 * 获得表格中的列数
 */
	@Override
	public int getColumnCount() {
			// TODO Auto-generated method stub
			//System.out.println("getColumnCount()");
		//return panelOptionstemp.tableName;
			return columnNames.length;
		}
 
	@Override
	public int getRowCount() {
			// TODO Auto-generated method stub
				return vector.size();
		}
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
			// TODO Auto-generated method stub
			//vector.get(rowIndex);得到的是Object的对象
				return ((Vector)vector.get(rowIndex)).get(columnIndex);
		}
/**
 * 抽象方法
 * -----------------------------------------------------------
 */
/**
 * 重写父类中非抽象的方法------覆盖父类中的方法
 */

 
 /**
  * 重写父类中的方法=======获得输入数据的类型,实现复选框的显示
  */
	public Class getColumnClass(int columnIndex){
			return getValueAt(0,columnIndex).getClass();
		}
 
 /**
  * 让表格中某些值可以进行修改
  * return false,说明不能进行修改
  */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
			//	 if(columnIndex==3)
			//		 return true;
			//	 else
			return true;
		}
 
 /**
  * 重写父类中的方法=====实现表格的数据可操作
  */
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		//先删除，在添加
			((Vector) vector.get(rowIndex)).remove(columnIndex);
			((Vector) vector.get(rowIndex)).add(columnIndex,value);
			//System.out.println("在这停顿");
			this.fireTableCellUpdated(rowIndex, columnIndex);
		}
 
 
 
/**
 * 无参构造方法------初始化数据
 */
	public TableModel1(String[] tableName,Object[] data){
		//panelOptionstemp=PanelOptions;
		this.columnNames=tableName;
		
		if(data!=null && data.length!=0){
			this.addRow(data);
		}
	}
 
/**
 * 往行中添加数据----这个方法名可以随意，由用户进行自行调用，
 * 否则table.updateUI()是不会自动调用的
 */
	public void addRow(Object[] data){
			int size = data.length;
			Vector v = new Vector();
			for(int i=0; i<size; i++){
				v.add(data[i]);
			}
			vector.add(v);
	}
 
	public void removeRow(int n){
		
		vector.remove(n);
	}
}



