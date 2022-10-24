package jp.sugoi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import com.google.common.net.InetAddresses;

public class DNS extends Main {
	/* 
	 * DNSシードより、アドレスのリストを取得する。
	 * seed.bitcoinstats.comと同じ仕組み。
	 */
	DNS(){
		try {
			InetAddress[] ina=InetAddress.getAllByName("yutadd.com");
			for(InetAddress s:ina) {
				addressList.add(s.getHostAddress());
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		Thread th=new Thread() {
			@Override
			public void run() {
				Random random=new Random();
				boolean bigNetwork=addressList.size()>50;
				int offset=0;
				int tried=0;
				do {
					tried++;
				if(bigNetwork) {
					offset=random.nextInt(addressList.size()-5);
				}
				for(int i=offset;i<addressList.size();i++) {
					Socket s=new Socket();
					InetSocketAddress target=new InetSocketAddress(addressList.get(i),321);
					try {
						s.connect(target,1024);
						User u=new User(s, BANGO);
						u.start();
						BANGO++;

					} catch (IOException e) {
						//There are some unconnectable nodes.It's not needed to show.
					}
				}
				console.put("DNS", BANGO+"/"+addressList.size()+" nodes has connected successfully.");
				if(BANGO==0)console.put("DNS2", BANGO+"/"+addressList.size()+" I'll retry after 5 sec");
				try{Thread.sleep(5000);}catch(Exception e) {}
				}while(BANGO==0&&tried>2);
			}
		};
		th.start();
		}
}
