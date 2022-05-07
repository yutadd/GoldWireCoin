package jp.sugoi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Main implements Runnable {
	@SuppressWarnings("resource")
	@Override
	public void run() {
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(0xfeed);
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		for(;;) {
			Socket so = null;
			/*
				String[][] message={
						{"name","ServerSocket"},
						{"Port","65261"},
						{"状態","接続待ち中"}
				};
				gui.stat(1,"SERVER",true,message);
				*/
				//65261
			try {
				so=ss.accept();
			}catch(IOException e){e.printStackTrace();}
			/*
			boolean aru=false;
			for(int i=0;i<=3;i++) {
				if(Main.u[i]==null) {
					aru=true;
				}
			}
			if(!aru) {
				try {
					so.close();
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
				String[][] message={
						{"name","ServerSocket"},
						{"Port","65261"},
						{"状態","最大接続数に達している。"}
				};
				gui.stat(1, "SERVER", false, message);
				continue;
			}*/
			System.out.println("connected from:"+so.getInetAddress().getHostAddress());
			User u=new User(so,Main.BANGO);
			Main.BANGO++;
			/*for(int i=0;i<=3;i++) {
				if(Main.u[i]==null) {
					Main.u[i]=u;
					break;
				}
			}*/
			Main.u.add(u);
			/*GUI gui=Main.gui;
			for(int i=0;i<=3;i++) {
				if(gui.ips[i].getText().equals("")) {
					//gui.ips[i].setText(so.getInetAddress().getHostAddress());
					u.ip_num=i;
					String[][] message={
							{"name","Node"},
							{"IP", so.getInetAddress().getHostAddress()},
					};
					Stats st=new Stats(message);
					gui.stat(i+3,"node", true, st.stats);
					u.debug_num=i+3;
					break;
				}
			}*/
			u.start();
		}
	}
}
