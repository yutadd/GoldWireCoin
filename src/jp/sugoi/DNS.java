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
			System.out.println("yutadd.com - timeout1024ms");
			s=new Socket();
			InetSocketAddress endpoint= new InetSocketAddress("yutadd.com",25565);
			s.connect(endpoint,1024);
			System.out.println("connect complete");
		} catch (Exception e) {
			try {
				System.out.println("164.70.118.44- timeout1024ms");
				s=new Socket();
				InetSocketAddress endpoint= new InetSocketAddress("164.70.118.44",  25565);
				s.connect(endpoint,1024);
				System.out.println("connect complete");
			}catch (IOException e1) {
				try {
					System.out.println("localhost - timeout1024ms");
					s=new Socket();
					InetSocketAddress endpoint= new InetSocketAddress("localhost",  25565);
					s.connect(endpoint,1024);
					System.out.println("connect complete");
				}catch(Exception ep) {
					System.out.println("faild to connect");
				}
			}
			if(s!=null){
				try {
					s.getOutputStream().write("get\r\n".getBytes());
				} catch (IOException e1) {
					System.out.print(e1.getMessage());
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
										System.out.println("[DNS]返答："+"no users");
									}else {
										boolean already=false;
										for(User u:Main.u) {
											if(u.s.getInetAddress().getHostAddress().equals(cmd)) {
												already=true;
											}
										}
										if(!already) {
											System.out.println("[DNS]返答："+cmd);
											try {//65261
												Socket s=new Socket(cmd,0xfeed);
												System.out.println("connect"+s.getInetAddress().getHostName());
												User user = new User(s,Main.BANGO);
												Main.BANGO++;
												Main.u.add(user);
												user.start();
											}catch(Exception e) {e.printStackTrace();}
										}else {
											System.out.println("[DNS]すでに登録済みであるため、スキップ");
										}
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
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
