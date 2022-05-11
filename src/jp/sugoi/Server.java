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
				//65261
			try {
				so=ss.accept();
			}catch(IOException e){e.printStackTrace();}
			System.out.println("connected from:"+so.getInetAddress().getHostAddress());
			User u=new User(so,Main.BANGO);
			Main.BANGO++;
			Main.u.add(u);
			u.start();
		}
	}
}
