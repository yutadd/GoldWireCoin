package jp.sugoi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

public class DNS extends Main {
	static Socket s;
	DNS(){
		//デプロイ時に内容の優先度を入れ替える。
		try {
			console.put("DNS00","localhost - timeout1024ms");
			s=new Socket();
			InetSocketAddress endpoint= new InetSocketAddress("localhost",  25565);
			
			s.connect(endpoint,1024);
			console.put("DNS01","connect complete");
		} catch (Exception e) {
			try {
				console.put("DNS00","164.70.64.125 - timeout1024ms");
				s=new Socket();
				InetSocketAddress endpoint= new InetSocketAddress("164.70.64.125",  25565);
				s.connect(endpoint,1024);
				console.put("DNS01","connect complete");
			}catch (IOException e1) {
				try {
					System.out.println("localhost - timeout1024ms");
					s=new Socket();
					InetSocketAddress endpoint= new InetSocketAddress("yutadd.com",25565);
					s.connect(endpoint,1024);
					console.put("DNS00","connect complete");
				}catch(Exception ep) {
					console.put("DNS01","faild to connect");
				}
			}
		}
			if(s!=null){
				try {
					s.getOutputStream().write("get\r\n".getBytes());
					console.put("DNS-3","Writed GET");
				} catch (IOException e1) {
					console.put("DNSE-3",e1.getMessage());
				}
				Thread th_in=new Thread(){
					public void run() {
						System.out.println("th_in_started");
							try {
								BufferedReader br=new BufferedReader(new InputStreamReader(s.getInputStream()));
								while(!s.isClosed()) {
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
												console.put("DNS-CON","CONNECTED TO RECEIVED ADDRESS!");
												User user = new User(s,Main.BANGO);
												Main.BANGO++;
												Main.u.add(user);
												user.start();
												
											}catch(Exception e) {console.put("DNSE-KEN","CONNOT CONNECT TO RECEIVED ADDRESS;;");}
										}else {
											console.put("DNSE-ALR","ALREADY CONNECTED");
										}
									}
								}
								console.put("DNSE-ALR","Shutdowning input stream.");
							} catch (Exception e) {
								e.printStackTrace();
								try {
									s.close();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
						}
				};
				th_in.start();
				System.out.println("th_in_started");
			}
		}
}
