package jp.sugoi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Socket;
import java.util.HashMap;

public class User extends Thread{
	Socket s;
	int b;

	@Override
	public void run() {
		Main.console.put("NETWORK(USER)00", "started user thread");
		InputStream sock_in;
		try {
			sock_in = s.getInputStream();
			InputStreamReader sock_is = new InputStreamReader(sock_in);
			BufferedReader sock_br = new BufferedReader(sock_is);
			for(;;) {
				String line=sock_br.readLine();
				if(line==null) {Main.console.put("USERE-NUL", "user sent null; ;");break;}
				Main.console.put("USER-LAST", "Last input String:"+(line.length()<14?line:line.substring(0, 14)));
				if(line.startsWith("block~")) {
					ReceiveBlock.exec(line,s);
				}else if(line.startsWith("transaction~")) {
					ReceiveTransaction.exec(line,s);
				}else if(line.startsWith("isSame~")){
					receiveIsSame(line);
				}else if(line.startsWith("blocks~")) {
					ReceiveBlocks.exec(line);
					Main.mati=false;
				}else if(line.startsWith("getfrom~")) {
					SendBlocks.exec(line,s);
				}else if(line.startsWith("balances~")){
					SendBalances.exec(s);
				}


			}
		} catch (Exception e) {
			e.printStackTrace();
			remove();
		}
	}
	void receiveIsSame(String line){
		try {
			String[] args=line.split("~");
			Block receivedBlock=new Block(args[1],BigInteger.ZERO,new HashMap<String,BigDecimal>(),true);
			String ReceivedHash=Mining.hash(receivedBlock.sum);
			int receivedNumber=receivedBlock.number;
			String localHash=Main.getHash(receivedNumber);
			if(localHash!=null&&localHash.equals(ReceivedHash)) {
				s.getOutputStream().write(("getfrom~"+Main.getBlock(receivedNumber).sum+"\r\n").getBytes());
				Main.console.put("[User]","getfrom "+receivedNumber+"is already writed.\r\n so getfrom "+receivedNumber+" has sent.");
			}else {
				s.getOutputStream().write(("getfrom~"+Main.getBlock(receivedNumber-1).sum+"\r\n").getBytes());
				Main.console.put("[User]","getfrom "+receivedNumber+" has sent.");
			}
		}catch(Exception e) {e.printStackTrace();}
	}
	synchronized public void remove() {

		Main.console.put("USER03", "LAST DELETED USER:"+s.getInetAddress().getHostAddress());
		int i=0;
		try {
			if(!s.isClosed())s.close();
			Main.u.remove(this);
		}catch(Exception e) {Main.console.put("USERE-04", "正常に削除されませんでした："+s.getInetAddress().getHostAddress());}
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof User) {
			if(((User)obj).b==this.b) {
				return true;
			}
		}
		return false;
	}
	User(Socket s,int b){
		this.s=s;
		this.b=b;

	}


}
