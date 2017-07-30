package pers.ui.proxy.options;

import java.awt.*;
import java.awt.event.*;


import javax.swing.*;

import pers.ui.proxy.options.enumsum.*;


public class InteceptClientRulerAdd extends JFrame implements MouseListener {

	
	
	PanelOptions pPanelOption;
	
	Container contentPane ;
	
	JLabel jLabelBooleanOperator,jLabelMatchType,jLabelMatchRelationship,jLabelMatchCondition;
	
	JComboBox jComboBoxBooleanOperator,jComboBoxMatchType,jComboBoxMatchRelationship;
	
	JTextArea jTextAreaMatchCondition;
	
	JButton jButtonOK,jButtonCancel;
	
	JPanel panelSelect;//上面选组部分的JPanel
	JPanel panelSetButton;//
	public InteceptClientRulerAdd(PanelOptions tPanelOptions)
	{
		pPanelOption=tPanelOptions;
		contentPane = this.getContentPane();// 获取JFrame默认的内容窗格
		contentPane.setLayout(new BorderLayout());
		
		panelSelect=new JPanel();
		panelSelect.setLayout(new GridLayout(9,2));  

		Box boxBinding1=Box.createHorizontalBox();
		Box boxBinding2=Box.createHorizontalBox();
		Box boxBinding3=Box.createHorizontalBox();
		Box boxBinding4=Box.createHorizontalBox();
		
		
		jLabelBooleanOperator=new JLabel("Boolean operator    ");
		jLabelMatchType=new JLabel("Match type          ");
		jLabelMatchRelationship=new JLabel("Match relationship  ");
		jLabelMatchCondition=new JLabel("Match condition     ");
		
		jComboBoxBooleanOperator=new JComboBox(BooleanOperator.values());
		jComboBoxMatchType=new JComboBox(MatchType.values());
		jComboBoxMatchRelationship=new JComboBox(MatchRelationship.values());

		jTextAreaMatchCondition=new JTextArea();
		
		boxBinding1.add(Box.createHorizontalStrut(50));
		boxBinding1.add(jLabelBooleanOperator);
		boxBinding1.add(Box.createHorizontalStrut(20));
		boxBinding1.add(jComboBoxBooleanOperator);
		boxBinding1.add(Box.createHorizontalStrut(50));
		
		boxBinding2.add(Box.createHorizontalStrut(50));
		boxBinding2.add(jLabelMatchType);
		boxBinding2.add(Box.createHorizontalStrut(20));
		boxBinding2.add(jComboBoxMatchType);
		boxBinding2.add(Box.createHorizontalStrut(50));
		
		boxBinding3.add(Box.createHorizontalStrut(50));
		boxBinding3.add(jLabelMatchRelationship);
		boxBinding3.add(Box.createHorizontalStrut(20));
		boxBinding3.add(jComboBoxMatchRelationship);
		boxBinding3.add(Box.createHorizontalStrut(50));
		
		boxBinding4.add(Box.createHorizontalStrut(50));
		boxBinding4.add(jLabelMatchCondition);
		boxBinding4.add(Box.createHorizontalStrut(20));
		boxBinding4.add(jTextAreaMatchCondition);
		boxBinding4.add(Box.createHorizontalStrut(50));
		
		JPanel panel1=new JPanel();
		JPanel panel2=new JPanel();
		JPanel panel3=new JPanel();
		JPanel panel4=new JPanel();
		JPanel panel5=new JPanel();
		panelSelect.add(panel1);
		panelSelect.add(boxBinding1);
		panelSelect.add(panel2);
		panelSelect.add(boxBinding2);
		panelSelect.add(panel3);
		panelSelect.add(boxBinding3);
		panelSelect.add(panel4);
		panelSelect.add(boxBinding4);
		panelSelect.add(panel5);
		
			jButtonOK=new JButton("  OK  ");
		jButtonCancel=new JButton("Cancel");
		jButtonOK.addMouseListener(this);
		jButtonCancel.addMouseListener(this);
		
		panelSetButton=new JPanel();
		panelSetButton.add(jButtonOK);
		panelSetButton.add(jButtonCancel);
		
		this.add(panelSelect,BorderLayout.CENTER);
		this.add(panelSetButton,BorderLayout.SOUTH);
		

		
		this.setSize(600, 400);
		this.setLocationRelativeTo(null); // 设置窗口位置在屏幕中间
		this.setVisible(true);
		this.setResizable(false);//设置不可改变大小
        
        
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
		// TODO Auto-generated method stub
		if (e.getSource()==jButtonOK) {
			pPanelOption.AddInteceptClientRuler((BooleanOperator)jComboBoxBooleanOperator.getSelectedItem(),(MatchType)jComboBoxMatchType.getSelectedItem(),(MatchRelationship)jComboBoxMatchRelationship.getSelectedItem(),jTextAreaMatchCondition.getText());
			this.dispose();
			return ;
		}
		else if(e.getSource()==jButtonCancel){
			this.dispose();
		}
	}
	
	
}
