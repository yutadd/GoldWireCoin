package jp.sugoi;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.Map.Entry;

public class SendBalances {
	public static void exec(Socket s) {
		for(Entry<String, BigDecimal> e:Main.utxo.entrySet()) {
			try {
				s.getOutputStream().write(("balance~"+e.getKey()+","+e.getValue().toString()+"\r\n").getBytes());
				s.getOutputStream().flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
}
