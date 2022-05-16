package jp.sugoi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

public class DNS extends Main {
	static Socket s;
	DNS(){
		try {
			console.put("DNS00","yutadd.com - timeout1024ms");
			s=new Socket();
			InetSocketAddress endpoint= new InetSocketAddress("yutadd.com",25565);
			s.connect(endpoint,1024);
			console.put("DNS01","connect complete");
		} catch (Exception e) {
			try {
				console.put("DNS00","164.70.118.44- timeout1024ms");
				s=new Socket();
				InetSocketAddress endpoint= new InetSocketAddress("164.70.118.44",  25565);
				s.connect(endpoint,1024);
				console.put("DNS01","connect complete");
			}catch (IOException e1) {
				try {
					System.out.println("localhost - timeout1024ms");
					s=new Socket();
					InetSocketAddress endpoint= new InetSocketAddress("localhost",  25565);
					s.connect(endpoint,1024);
					console.put("DNS00","connect complete");
				}catch(Exception ep) {
					console.put("DNS01","faild to connect");
				}
			}
			if(s!=null){
				try {
					s.getOutputStream().write("get\r\n".getBytes());
				} catch (IOException e1) {
					console.put("DNSE-3",e1.getMessage());
				}
				Thread th_in=new Thread(){
					@Override
					public void run() {
						for(;;) {
							try {
								BufferedReader br=new BufferedReader(new InputStreamReader(s.getInputStream()));
								for(;!s.isClosed();) {
									String cmd=br.readLine();
									System.out.println(cmd);
									if(cmd.equals("no_users")) {
										console.put("DNS02","[DNS]返答：no users");
									}else {
										boolean already=false;
										for(User u:Main.u) {
											if(u.s.getInetAddress().getHostAddress().equals(cmd)) {
												already=true;
											}
										}
										if(!already) {
											console.put("DNS02","[DNS]返答："+cmd);
											try {//65261
												Socket s=new Socket(cmd,0xfeed);
												System.out.println("connect"+s.getInetAddress().getHostName());
												User user = new User(s,Main.BANGO);
												Main.BANGO++;
												Main.u.add(user);
												user.start();
											}catch(Exception e) {e.printStackTrace();}
										}else {
											console.put("DNS02","ALREADY CONNECTED");
										}
									}
								}
							} catch (Exception e) {
								//e.printStackTrace();
								try {
									s.close();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
								break;
							}
						}
					}
				};
				th_in.start();
			}
		}
	}
}
