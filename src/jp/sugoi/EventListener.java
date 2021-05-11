package jp.sugoi;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;

import javax.swing.JOptionPane;

public class EventListener implements MouseListener, MouseMotionListener, ActionListener{
	boolean timeout = true;
	boolean waiting_agree=false;
	public EventListener() {}
	HashMap<String,Stats_GUI> c=new HashMap<>();
	@Override
	public void mouseClicked(MouseEvent e) {
		if(!waiting_agree) {
			if(timeout) {
				try {
					if(c.size()!=0) {
						if(c.containsKey(e.getComponent().getName())){
							c.get(e.getComponent().getName()).close();
						}
					}
					try {
						c.put(e.getComponent().getName(), new Stats_GUI(Main.gui.debugs.get(Main.gui.debug[Integer.parseInt(e.getComponent().getName())]),e.getComponent().getName()));
					}catch(Exception eer) {System.out.println(eer.getStackTrace()[0]);}
					timeout=false;
					Thread th_time=new Thread() {
						@Override
						public void run() {
							try {
								Thread.sleep(800);
								timeout=true;
							} catch (InterruptedException e) {e.printStackTrace();
							}
						}
					};
					th_time.start();
				}catch(Exception eee) {eee.printStackTrace();}
			}
		}
	}

	boolean start=false;
	boolean click;

	@Override
	public void mousePressed(MouseEvent e) {
		if(!waiting_agree) {
			if(e.getComponent().getName().equals("desktop")) {
				click=true;
				Thread th=new Thread() {
					@Override
					public void run() {
						int s_r_m=e.getLocationOnScreen().x-Main.gui.desktop_label.getLocation().x;
						int s_r_m_=e.getLocationOnScreen().y-Main.gui.desktop_label.getLocation().y;
						while(click) {
							int[] n_a_m=new int[2];
							n_a_m[0]=(Main.gui.desktop_label.getMousePosition().x+Main.gui.desktop_label.getLocation().x)-s_r_m;
							n_a_m[1]=(Main.gui.desktop_label.getMousePosition().y+Main.gui.desktop_label.getLocation().y)-s_r_m_;
							Main.gui.desktop_label.setLocation(n_a_m[0],n_a_m[1]);
							//	try {Thread.sleep(350);}catch(Exception e) {}
							//Main.gui.desktop_label.setLocation(start_soutai_loc.x-(start_loc.x-(x-start_loc.x)),start_soutai_loc.y-(start_loc.y-(y-start_loc.y)));
						}
						start=false;
					}
				};
				if(!start) {
					th.start();
					start=true;
				}

			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(!waiting_agree) {
			if(e.getComponent().getName().equals("desktop")){
				click=false;
				start=false;
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {


	}

	@Override
	public void mouseExited(MouseEvent e) {


	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("action-"+e.getActionCommand());
		if(e.getActionCommand().equals("send")){
			Thread kakunin=new Thread() {
				@Override
				public void run() {
					BigDecimal d=new BigDecimal(0);
					try {
						if(Main.utxo.containsKey(Main.w.pub[0].toString(16))) {
							d=Main.utxo.get(Main.w.pub[0].toString(16));
						}
					}catch(Exception en) {
					}
					try {
						if(d.compareTo(new BigDecimal(Main.gui.amount.getText()))>=0) {
							long time=System.currentTimeMillis();
							double fee=0.5;
							BigInteger[] sign=Main.w.sign((Transaction.hash(Main.w.pub[0].toString(16)+"0x0a"+Main.w.pub[1].toString(16)+Main.gui.to.getText().split("0x0a")[0]+"0x0b"+ Main.gui.to.getText().split("0x0a")[1]+"0x0c"+new BigDecimal(Main.gui.amount.getText())+   ""+fee    +""+time)).getBytes());
							System.out.printf("[イベントリスナー]サイン生成用原文 = %s\r\n",Main.w.pub[0].toString(16)+"0x0a"+Main.w.pub[1].toString(16)+Main.gui.to.getText().split("0x0a")[0]+"0x0b"+ Main.gui.to.getText().split("0x0a")[1]+"0x0c"+new BigDecimal(Main.gui.amount.getText())+   ""+fee    +""+time);
							Transaction t=new Transaction(Main.w.pub[0].toString(16)+"0x0a"+Main.w.pub[1].toString(16)+"@" +  Main.gui.to.getText().split("0x0a")[0]+"0x0b"+ Main.gui.to.getText().split("0x0a")[1]+"0x0c"+new BigDecimal(Main.gui.amount.getText())+"@" +   fee   +"@" +time   +"@" + sign[0].toString(16)+"0x0a"+sign[1].toString(16),new BigDecimal(0));
							System.out.println("[イベントリスナー]トランザクションハッシュ : "+t.hash);
							if(t.ok) {
								Main.pool.add(t.transaction_sum);
								User.share("trans~",t.transaction_sum, DNS.s);
								kakunin(Main.gui.to.getText(),new BigDecimal(Main.gui.amount.getText()),t.fee);
								Main.gui.to.setText("");
								Main.gui.amount.setText("");
							}else {
								System.out.println("[イベントリスナー]トランザクションが拒否されました。");
							}
						}else {
							System.out.println("[イベントリスナー]お金が足りねぇ: "+d);
						}
					}catch(Exception ee) {
						System.out.println("==========error==========");
						ee.printStackTrace();
						System.out.println("[イベントリスナー]宛先アドレス"+Main.gui.to.getText().split("0x0a")[0]+"0x0b"+ Main.gui.to.getText().split("0x0a")[1]);
						
						System.out.println("==========error==========");
					}
				}
			};
			kakunin.start();

		}
	}
	boolean kakunin(String address,BigDecimal amount,BigDecimal fee) {
		JOptionPane.showMessageDialog(Main.gui.l0,"送信しました。\r\n"+address+"\r\n"+amount+"  "+Main.name+"\r\n手数料"+fee+"  "+Main.name+"\r\n"+"残額  "+Main.utxo.get(Main.w.pub[0].toString(16)));
		return true;
	}

}
