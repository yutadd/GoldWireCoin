package jp.sugoi;

import java.io.IOException;
import java.net.Socket;

public class ReceiveBlock {
	public static void exec(String line,Socket s) {
		if(!Main.mati) {
			Main.console.put("RECEIVEBLOCK00", "一つのブロックを受信");
			String blocks=line.split("~")[1];
			Block b=new Block(blocks,Main.min,Main.utxo,false);
			if(b.previous_hash.equals(Main.getlatesthash())) {
				if(b.ok) {
					Main.addBlock(b.sum);
					Network.share("block~", b.sum, s);
					//System.out.println("[ユーザー]書き込みました。");
				}else {
					Main.console.put("RECEIVEBLOCKE-01", "受信したブロックが不正");
				}
			}else {
				if(b.number>Main.getBlockSize()) {
					try {
						s.getOutputStream().write(("getfrom~"+Main.getlatesthash()+"\r\n").getBytes());
					} catch (IOException e) {
						Main.console.put("RECEIVEBLOCKE-02", "getfromを送信できない");
						e.printStackTrace();
					}
					
				}else {
					Main.console.put("RECEIVEBLOCK03", "記述し終えたブロックが送られてきた。");
				}
			}
		}
	}
}
