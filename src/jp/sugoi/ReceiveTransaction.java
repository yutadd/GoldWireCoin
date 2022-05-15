package jp.sugoi;

import java.io.IOException;
import java.net.Socket;

public class ReceiveTransaction {
	public static void exec(String line,Socket so) {
		String[] tr=line.split("~");
		String from=tr[1].split("@")[0];
		String from_shou=from.split("0x0a")[0];
		for(String s:Main.pool) {
			Transaction t=new Transaction(s,Main.utxo);
			if(t.input.equals(from_shou)) {
			}
		}
		Transaction t=new Transaction((tr[0].equals("disc_transaction"))?tr[2]:tr[1],Main.utxo);
		if(t.ok) {
			try {
				so.getOutputStream().write((t.hash+"~ok\r\n").getBytes());
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			Main.pool.add(t.transaction_sum);
			Network.share("trans~", t.transaction_sum,so);
		}else {
			try {
				so.getOutputStream().write((t.hash+"~denny\r\n").getBytes());
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			//notice=false;
		}
	}
}
