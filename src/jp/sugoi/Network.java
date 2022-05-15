package jp.sugoi;

import java.io.OutputStream;
import java.net.Socket;

public class Network {
	public static void share(String type,String st,Socket s) {
		for(User u:Main.u) {
			if(u==null) {continue;}
			if(!(u.s.getInetAddress().getHostAddress().equals(s.getInetAddress().getHostAddress())&&u.s.getPort()==s.getPort())) {
				try {
					OutputStream os=u.s.getOutputStream();
					if(!u.light) {
						os.write((type+st).getBytes());
						System.out.println("send\r\n"+st);
						os.write("\r\n".getBytes());
						os.flush();
					}
				} catch (Exception e) {
					System.out.println("[ユーザー]"+e.getMessage());
					u.remove();

				}
			}
		}
	}
}
