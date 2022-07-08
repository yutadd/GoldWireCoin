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
						Main.console.put("NETWORK00", "Last Output String:"+(st.length()<14?st:st.substring(0, 14)));
						os.write("\r\n".getBytes());
						os.flush();
					}
				} catch (Exception e) {
					Main.console.put("NETWORKE-1", e.getMessage());
					u.remove();
				}
			}
		}
	}
}
