package jp.sugoi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Main implements Runnable {
	@Override
	public void run() {
		ServerSocket ss = null;
		try {
			ss = new ServerSocket(0xfeed);
			for (;;) {
				Socket so = null;
				//65261
				so = ss.accept();
				System.out.println("connected from:" + so.getInetAddress().getHostAddress());
				User u = new User(so, Main.BANGO);
				Main.BANGO++;
				Main.u.add(u);
				u.start();
			}
		} catch (IOException e) {
			console.put("SERVICE-BIND", "Already binded port 0xfeed?");
		}
	}
}
