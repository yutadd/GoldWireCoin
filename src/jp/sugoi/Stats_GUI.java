package jp.sugoi;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

public class Stats_GUI extends WindowAdapter{
	String name=null;
	JFrame frame=null;
	JLabel lab=null;
	JTable tab=null;
	 public void windowClosing(WindowEvent e) {
		 frame.setVisible(false);
			frame=null;
			Main.gui.ev.c.remove(this.name);
	 }
	public Stats_GUI(Stats arg,String name) {
		frame=new JFrame();
		frame.addWindowListener(this);
		JPanel p=new JPanel();
		this.name=name;
		frame.setBounds(100,80,0,0);
		String[] colm= {"key","value"};
		try {
			tab=new JTable(arg.stats,colm);
			tab.setBackground(GUI.default_color);
			tab.setForeground((Main.gui.mode.equals("blue")) ? new Color(0,162,232):Color.yellow);
			lab=new JLabel();
			lab.setIcon(Main.gui.image_mini);
			frame.add(tab);
			tab.add(lab);
			frame.setResizable(false);
		}catch(Exception e) {
			String[][] dat= {{"接続されていません。","not connected"}};
			tab=new JTable(dat,colm);
			tab.setBackground(GUI.default_color);
			tab.setForeground((Main.gui.mode.equals("blue")) ? new Color(0,162,232):Color.yellow);
			lab=new JLabel();
			lab.setIcon(Main.gui.image_mini);
			frame.add(tab);
			tab.add(lab);
			frame.setResizable(false);
		}
		frame.setVisible(true);
		Thread th=new Thread() {
			@Override
			public void run() {
				for(int i=0;(Main.gui.desktop_label.getWidth()/4)>i;i+=2) {
					try {
						frame.setBounds(100,80,i,(int)(i/4));
						Thread.sleep(1);
						frame.repaint();
					}catch(Exception en) {en.printStackTrace();break;}

				}
			}
		};
		th.start();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Stats_GUI) {
			if(((Stats_GUI) obj).name.equals(this.name)) {
				return true;
			}
		}
		return false;
	}

	public void close() {
		frame.setVisible(false);
		frame=null;
		Main.gui.ev.c.remove(this.name);
	}

}
