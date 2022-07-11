package jp.sugoi;

import java.io.OutputStream;

public class Network {
	public static void shareToNodes(String str) {
		for(User u:Main.u) {
			if(u==null) {continue;}
			try {
				OutputStream os=u.s.getOutputStream();
				if(!u.light) {
					os.write((str+"\r\n").getBytes());
					Main.console.put("NETWORK00", "Last Output String:"+(str.length()<14?str:str.substring(0, 14)));
					os.flush();
				}
			} catch (Exception e) {
				Main.console.put("NETWORKE-1", e.getMessage());
				u.remove();
			}
		}
	}
}
