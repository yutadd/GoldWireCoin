package jp.sugoi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class User extends Thread{
	Socket s;
	boolean light=false;
	int b;

	@Override
	public void run() {
		Thread th=new Thread() {
			@Override
			public void run() {
				InputStream sock_in;
				try {
					sock_in = s.getInputStream();
					InputStreamReader sock_is = new InputStreamReader(sock_in);
					BufferedReader sock_br = new BufferedReader(sock_is);
					for(;;) {
						String line=sock_br.readLine();
						if(line==null) {System.out.println("nullを送られてきました。");break;}
						Main.console.put("NETWORK(USER)01", "Last input String:"+(line.length()<14?"NONE":line.substring(0, 14)));
						if(line.startsWith("block~")) {
							ReceiveBlock.exec(line,s);
						}else if(line.startsWith("trans~")||line.startsWith("disc_transaction~")) {
							ReceiveTransaction.exec(line,s);
						}else if(line.startsWith("notfound")){
							System.out.println("["+Main.getfrom+"][ユーザー]notfoundが送られてきた。");
							s.getOutputStream().write(("getfrom~"+Main.getHash(3)+"\r\n").getBytes());
						}else if(line.startsWith("blocks~")) {
							ReceiveBlocks.exec(line);
						}else if(line.startsWith("getfrom~")) {
							SendBlocks.exec(line,s);
						}else if(line.startsWith("balances~")){
							SendBalances.exec(s);
						}
					
					
					}
				} catch (Exception e) {
					remove();
				}
				
			}
		};
		th.start();
	}

	synchronized public void remove() {

		Main.console.put("USER01", "LAST DELETED USER:"+s.getInetAddress().getHostAddress());
		int i=0;
		try {
			if(!s.isClosed())s.close();
			Main.u.remove(this);
		}catch(Exception e) {Main.console.put("USERE-02", "正常に削除されませんでした："+s.getInetAddress().getHostAddress());}
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
