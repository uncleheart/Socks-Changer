/*代码显示部分界面*/
package pers.ui.basic;

import java.io.*;
import java.util.Vector;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.InlineView;
import javax.swing.text.html.ParagraphView;
import javax.swing.text.html.HTMLEditorKit.HTMLFactory;

import org.exbin.deltahex.DataChangedListener;
import org.exbin.deltahex.swing.CodeArea;
import org.exbin.utils.binary_data.BinaryData;
import org.exbin.utils.binary_data.ByteArrayEditableData;

public class EditorGUI extends JTabbedPane implements ChangeListener, ComponentListener, DocumentListener, MouseListener {

	private String data = "\n";
	public ImprovedTextPane StrEditor;// 为代码以字符串显示的部分创建文本区
	private JScrollPane scrollStrEditor;
	private JPanel panelStr;// 为代码以字符串显示的部分创建内容窗格
	private JPanel panelSearch;//下面搜索部分的panel
	private DeltaHexPanel hexPanel;
	private CodeArea codeArea;
	private ByteArrayEditableData byteArrayData; // hex编辑器的数据
	private JButton buttonLeftMove;
	private JButton buttonAdd;
	private JButton buttonRightMove;
	private JTextField textFieldSearch;
	private int WhoBeingEdited; // 0代表字符串编辑器正在修改中 1代表hex编辑器处于修改中 用于得到最终的数据

	private String strSearch=new String();
	Document document;//实现动态监听textFieldSearch中变化时用
	Vector vectorSearchPosition=new Vector();
	int SearchPositionIndex=-1;//记录现在指针所处的位置，用来实现左移和右移功能
	
	
	private boolean isEdited;//内容是否被修改
	public EditorGUI() {
		// TODO Auto-generated constructor stub

		
		codeArea = new CodeArea();
		WhoBeingEdited = 0;
		hexPanel = new DeltaHexPanel();
		hexPanel.setCodeArea(codeArea);
		
		

		StrEditor = new ImprovedTextPane();

		scrollStrEditor=new JScrollPane(StrEditor);
		scrollStrEditor.setHorizontalScrollBarPolicy( 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
		
		panelStr = new JPanel();
		panelStr.setLayout(new BorderLayout());
		panelSearch = new JPanel();
		
		buttonLeftMove=new JButton(" < ");
		buttonAdd=new JButton(" + ");
		buttonRightMove=new JButton(" > ");
		JPanel paneltemp1=new JPanel();//填充，不显示
		textFieldSearch=new JTextField();
		
		buttonLeftMove.addMouseListener(this);
		buttonRightMove.addMouseListener(this);
		buttonAdd.addMouseListener(this);
		document = textFieldSearch.getDocument();  
        document.addDocumentListener(this); //实现动态监听textFieldSearch中数据的变化
		
		
		GridBagLayout layoutOptions = new GridBagLayout();
		panelSearch.setLayout(layoutOptions);
		
		GridBagConstraints gridBagConstraints=new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(0, 5, 0, 5);// 设置控件的空白（上，左，下，右）
		gridBagConstraints.gridwidth=2;//该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
		gridBagConstraints.gridheight = 1;// 占用1行
		gridBagConstraints.gridx = 0;// 起始点为第1列
		gridBagConstraints.gridy = 0;// 起始点为第1行
		gridBagConstraints.weightx = 0;//该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		gridBagConstraints.weighty=0;//该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		layoutOptions.setConstraints(buttonLeftMove, gridBagConstraints);//设置组件
		
		gridBagConstraints.insets = new Insets(0, 5, 0, 5);// 设置控件的空白
		gridBagConstraints.gridwidth=2;//该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
		gridBagConstraints.gridheight = 1;// 占用1行
		gridBagConstraints.gridx = 2;// 起始点为第3列
		gridBagConstraints.gridy = 0;// 起始点为第2行
		gridBagConstraints.weightx = 0;//该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		gridBagConstraints.weighty=0;//该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		layoutOptions.setConstraints(buttonAdd, gridBagConstraints);//设置组件
		
		gridBagConstraints.insets = new Insets(0, 5, 0, 5);// 设置控件的空白
		gridBagConstraints.gridwidth=2;//该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
		gridBagConstraints.gridheight = 1;// 占用1行
		gridBagConstraints.gridx = 4;// 起始点为第5列
		gridBagConstraints.gridy = 0;// 起始点为第0行
		gridBagConstraints.weightx = 0;//该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		gridBagConstraints.weighty=0;//该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		layoutOptions.setConstraints(buttonRightMove, gridBagConstraints);//设置组件
		
		gridBagConstraints.insets = new Insets(0, 5, 0, 5);// 设置控件的空白
		gridBagConstraints.gridwidth=8;//该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
		gridBagConstraints.gridheight = 1;// 占用1行
		gridBagConstraints.gridx = 6;// 起始点为第5列
		gridBagConstraints.gridy = 0;// 起始点为第0行
		gridBagConstraints.weightx = 1;//该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		gridBagConstraints.weighty=0;//该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		layoutOptions.setConstraints(textFieldSearch, gridBagConstraints);//设置组件
		
		gridBagConstraints.insets = new Insets(0, 5, 0, 5);// 设置控件的空白
		gridBagConstraints.gridwidth=2;//该方法是设置组件水平所占用的格子数，如果为0，就说明该组件是该行的最后一个
		gridBagConstraints.gridheight = 1;// 占用1行
		gridBagConstraints.gridx = 14;// 起始点为第5列
		gridBagConstraints.gridy = 0;// 起始点为第0行
		gridBagConstraints.weightx = 0;//该方法设置组件水平的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		gridBagConstraints.weighty=0;//该方法设置组件垂直的拉伸幅度，如果为0就说明不拉伸，不为0就随着窗口增大进行拉伸，0到1之间
		layoutOptions.setConstraints(paneltemp1, gridBagConstraints);//设置组件
		
		panelSearch.add(buttonLeftMove);
		panelSearch.add(buttonAdd);
		panelSearch.add(buttonRightMove);
		panelSearch.add(textFieldSearch);
		panelSearch.add(paneltemp1);
		
		panelStr.add(scrollStrEditor, BorderLayout.CENTER);
		panelStr.add(panelSearch, BorderLayout.SOUTH);
		// HexJPanel.add(HexJTextArea);

		this.add(panelStr, "字符串显示");
		this.add(hexPanel, "16进制显示");
		this.addChangeListener(this);
		byteArrayData = new ByteArrayEditableData();

		SetHexEditorData("");
		SetStrEditorData("");
		this.addComponentListener(this);
		isEdited=false;
		codeArea.addDataChangedListener(new DataChangedListener() {
			
			@Override
			public void dataChanged() {
				// TODO Auto-generated method stub
				isEdited=true;
			}
		});
		StrEditor.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				isEdited=true;
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				isEdited=true;
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				isEdited=true;
			}
		});
	}

	private void SetStrEditorData(String input) {
		StrEditor.setText(input);
	}

	private void SetHexEditorData(String input) {
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(input.getBytes());
			InputStream inputStream = is;
			byteArrayData.loadFromStream(inputStream);
			codeArea.setData(byteArrayData);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void SetData(String input) {
		
		if (WhoBeingEdited == 0) {
			SetStrEditorData(input);
		} else if (WhoBeingEdited == 1) {
			SetHexEditorData(input);
		}
		isEdited=false;
	}
	public boolean ChangeData()
	{
		return isEdited;
	}
	public String GetDataFromEditor() {
		if (WhoBeingEdited == 0) {
			return StrEditor.getText();
		} else if (WhoBeingEdited == 1) {
			BinaryData binaryData = codeArea.getData();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			OutputStream outputStream = byteArrayOutputStream;
			try {
				binaryData.saveToStream(outputStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return outputStream.toString();
		}
		return "";
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		if (((JTabbedPane) e.getSource()).getSelectedIndex() == 0) {
			data = GetDataFromEditor();
			WhoBeingEdited = 0;
			SetStrEditorData(data);

		} else if (((JTabbedPane) e.getSource()).getSelectedIndex() == 1) {
			data = GetDataFromEditor();
			WhoBeingEdited = 1;
			SetHexEditorData(data);
		}
	}

	public void Search(String str){
		
		DefaultStyledDocument doc=(DefaultStyledDocument)StrEditor.getDocument();//在开始时先将所有样式设置为默认样式（黑色字体）
		SimpleAttributeSet attribute = new SimpleAttributeSet();
		StyleConstants.setForeground(attribute,Color.BLACK);
		doc.setCharacterAttributes(0,StrEditor.getText().length(),attribute,true);//设置指定范围的文字样式
		
		vectorSearchPosition.removeAllElements();//清空所有位置信息
		StrEditor.getHighlighter().removeAllHighlights();
		if(str.isEmpty())
			return;

		int start = 0;
	    int end = 0;
		int pointer=StrEditor.getText().indexOf(str);
		while (pointer!=-1) {//将每一个找到的位置设置为红色字体
			start = pointer;
			vectorSearchPosition.add(start);
			pointer=StrEditor.getText().indexOf(str,start+1);
			StyleConstants.setForeground(attribute,Color.RED);
			doc.setCharacterAttributes(start,str.length(),attribute,true);//设置指定范围的文字样式

			
		}
	}
	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		SearchPositionIndex=-1;
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		strSearch=textFieldSearch.getText();//赋值到strSearch
		SearchPositionIndex=-1;
		Search(strSearch);
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		// TODO Auto-generated method stub
		strSearch=textFieldSearch.getText();//赋值到strSearch
		SearchPositionIndex=-1;
		Search(strSearch);
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
		if(arg0.getSource()==buttonLeftMove){
			StrEditor.getHighlighter().removeAllHighlights();
			if(SearchPositionIndex>=0){
				SearchPositionIndex--;
			}
			if(SearchPositionIndex==-1)
				SearchPositionIndex=vectorSearchPosition.size()-1;
			try {
				StrEditor.getHighlighter().addHighlight((int)vectorSearchPosition.elementAt(SearchPositionIndex), (int)vectorSearchPosition.elementAt(SearchPositionIndex)+strSearch.length(), DefaultHighlighter.DefaultPainter);
				} catch (BadLocationException ble) {
					
				}
		}
		
		else if(arg0.getSource()==buttonRightMove){
			StrEditor.getHighlighter().removeAllHighlights();
			int vectorsize=vectorSearchPosition.size();
			if(vectorsize==0)return;
			
			SearchPositionIndex++;
			
			if(SearchPositionIndex==vectorSearchPosition.size())
				SearchPositionIndex=0;
			
			try {
				StrEditor.getHighlighter().addHighlight((int)vectorSearchPosition.elementAt(SearchPositionIndex), (int)vectorSearchPosition.elementAt(SearchPositionIndex)+strSearch.length(), DefaultHighlighter.DefaultPainter);
				} catch (BadLocationException ble) {
					
				}
			}
		else if(arg0.getSource()==buttonAdd){
			System.out.println(vectorSearchPosition.size());
		}
	}
}