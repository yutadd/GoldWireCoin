package jp.sugoi;

import java.io.IOException;
import java.net.Socket;

public class ReceiveBlock {
	public static void exec(String line,Socket s) {
		if(!Main.mati) {
			System.out.println("ブロックを受信");
			String blocks=line.split("~")[1];
			Block b=new Block(blocks,false,Main.min,Main.utxo,false);
			if(b.previous_hash.equals(Main.getlatesthash())) {
				if(b.ok) {
					Main.addBlock(b.sum);
					Network.share("block~", b.sum, s);
					System.out.println("[ユーザー]書き込みました。");
				}else {
					System.out.println("[ブロック]受け取ったブロックが不正");
				}
			}else {
				if(b.number>Main.getBlockSize()) {
					try {
						s.getOutputStream().write(("getfrom~"+Main.getlatesthash()+"\r\n").getBytes());
					} catch (IOException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
					}
					System.out.println("["+(++Main.getfrom)+"][ユーザー]getfromを送信しました : \r\n  getfrom~"+Main.getlatesthash());
				}else {
					System.out.println("記述し終えたブロックが送られてきた。");
				}
			}
		}
	}
}
