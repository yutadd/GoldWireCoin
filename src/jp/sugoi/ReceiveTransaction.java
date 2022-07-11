package jp.sugoi;

import java.io.IOException;
import java.net.Socket;

public class ReceiveTransaction {
	public static void exec(String line,Socket so) {
		String[] tr=line.split("~");
		String from=tr[1].split("@")[0];
		Transaction t=new Transaction(tr[1],Main.checkNullAndGetValue(Main.utxo,from.split("0x0a")[0]),false);
		if(t.ok) {
			Main.console.put("RECEIVETRANSACTION-SUCCESS", "Received transaction is good one!");
			try {
				so.getOutputStream().write(("ok~"+t.sig[0].toString(16)+"\r\n").getBytes());
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			if(!Main.pool.contains(t)) {
				Main.pool.add(t);
				Network.shareToNodes("transaction~"+ t.transaction_sum);
			}
			
		}else {
			Main.console.put("RECEIVETRANSACTIONE-FAILED", "Received transaction is invalid!");
			try {
				so.getOutputStream().write(("reject~"+t.sig[0].toString(16)+"\r\n").getBytes());
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			//notice=false;
		}
	}
}
