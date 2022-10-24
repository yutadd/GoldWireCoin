package jp.sugoi;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;


public class DNS {
	/* 
	 * DNSシードより、アドレスのリストを取得する。
	 * seed.bitcoinstats.comと同じ仕組み。
	 */
	DNS(){
		try {
			InetAddress[] ina=InetAddress.getAllByName("yutadd.com");
			for(InetAddress s:ina) {
				Main.addressList.add(s.getHostAddress());
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		Thread th=new Thread() {
			@Override
			public void run() {
				Random random=new Random();
				boolean bigNetwork=Main.addressList.size()>50;
				int offset=0;
				int tried=0;
				do {
					tried++;
				if(bigNetwork) {
					offset=random.nextInt(Main.addressList.size()-5);
				}
				for(int i=offset;i<Main.addressList.size();i++) {
					Socket s=new Socket();
					InetSocketAddress target=new InetSocketAddress(Main.addressList.get(i),321);
					try {
						s.connect(target,1024);
						User u=new User(s, Main.BANGO);
						u.start();
						Main.BANGO++;

					} catch (IOException e) {
						//There are some unconnectable nodes.It's not needed to show.
					}
				}
				Main.console.put("DNS", Main.BANGO+"/"+Main.addressList.size()+" nodes has connected successfully.");
				if(Main.BANGO==0)Main.console.put("DNS2", Main.BANGO+"/"+Main.addressList.size()+" I'll retry after 5 sec");
				try{Thread.sleep(5000);}catch(Exception e) {}
				}while(Main.BANGO==0&&tried<3);
			}
		};
		th.start();
		}
}
