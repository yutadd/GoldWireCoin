package jp.sugoi;

import java.io.IOException;
import java.net.Socket;

public class ReceiveBlock {
	public static void exec(String line,Socket s) {
		if(!Main.mati) {
			Main.console.put("RECEIVEBLOCK00", "一つのブロックを受信");
			String blocks=line.split("~")[1];
			Block b=new Block(blocks,Main.diff,Main.utxo,true);//difficultエラーが出力されてしまうため、ここではpass-checkをtrueにする
			if(b.previousHash.equals(Main.getlatesthash())) {
				b=new Block(blocks,Main.diff,Main.utxo,false);
				if(b.ok) {
					Main.addBlock(b.fullText);
					Network.shareToNodes("block~"+ b.fullText);
					//System.out.println("[ユーザー]書き込みました。");
				}else {
					Main.console.put("RECEIVEBLOCKE-01", "受信したブロックが不正");
				}
			}else {
				if(b.number>Main.getBlockSize()) {
					try {
						s.getOutputStream().write(("getfrom~"+Main.getBlock(Main.getBlockSize()).fullText+"\r\n").getBytes());
					} catch (IOException e) {
						Main.console.put("RECEIVEBLOCKE-02", "getfromを送信できない");
						e.printStackTrace();
					}
					
				}else {
					Main.console.put("RECEIVEBLOCKI-03", "記述し終えたブロックが送られてきた。");
				}
			}
		}
	}
}
