package jp.sugoi;

import java.io.IOException;
import java.net.Socket;

public class ReceiveTransaction {
	public static void exec(String line,Socket so) {
		String[] tr=line.split("~");
		String from=tr[1].split("@")[0];
		Transaction t=new Transaction((tr[0].equals("disc_transaction"))?tr[2]:tr[1],Main.utxo.get(from));
		if(t.ok) {
			try {
				so.getOutputStream().write((t.hash+"~ok\r\n").getBytes());
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			if(!Main.pool.contains(t)) {
				Main.pool.add(t);
			}
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
