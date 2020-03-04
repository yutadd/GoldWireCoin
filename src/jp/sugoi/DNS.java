package jp.sugoi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;

public class DNS extends Main {
	DNS(){
		try {
			System.out.println("すっごい.jp - timeout1024ms");
			s=new Socket();
			InetSocketAddress endpoint= new InetSocketAddress("すっごい.jp",  53);
			s.connect(endpoint,1024);
			System.out.println("connect complete");
			String[][] message={
					{"name","DNS SEED"},
					{"IP",s.getInetAddress().getHostName()}
			};
			gui.stat(0,"DNS",true,message);
		} catch (Exception e) {
			try {
				System.out.println("192.168.1.12- timeout1024ms");
				s=new Socket();
				InetSocketAddress endpoint= new InetSocketAddress("192.168.1.12",  53);
				s.connect(endpoint,1024);
				System.out.println("connect complete");
				String[][] message={
						{"name","DNS SEED"},
						{"IP",s.getInetAddress().getHostName()}
				};
				gui.stat(0,"DNS",true,message);
			}catch (IOException e1) {
				try {
					System.out.println("localhost - timeout1024ms");
					s=new Socket();
					InetSocketAddress endpoint= new InetSocketAddress("localhost",  53);
					s.connect(endpoint,1024);
					System.out.println("connect complete");
					String[][] message={
							{"name","DNS SEED"},
							{"IP",s.getInetAddress().getHostName()}
					};
					gui.stat(0,"DNS",true,message);
				}catch(Exception ep) {
					System.out.println("faild to connect");
					String[][] message={
							{"name","DNS SEED"},
							{"Error",ep.getMessage()},
							{"IP","Could not connect to all servers"}
					};
					gui.stat(0,"DNS",false,message);
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
										System.out.println("no users");
									}else {
										boolean aru=false;
										for(int i=4;i<8;i++) {
											if(Main.u[i]==null) {
												aru=true;
											}
										}
										if(!aru) {
											System.out.println("キューがいっぱい");
											s.close();
											continue;
										}else {
											try {//65261
												Socket s=new Socket(cmd,0xfeed);
												System.out.println("connect"+s.getInetAddress().getHostName());
												User user = new User(s,Main.BANGO);
												Main.BANGO++;
												for(int i=4;i<8;i++) {
													if(Main.u[i]==null) {
														Main.u[i]=user;
														break;
													}
												}
												GUI gui=Main.gui;
												for(int i=0;i<=3;i++) {
													if(gui.ips[i].getText().equals("")) {
														gui.ips[i].setText(s.getInetAddress().getHostAddress());
														user.ip_num=i;
														String[][] message={
																{"name","Node"},
																{"IP", s.getInetAddress().getHostAddress()},
														};
														Stats st=new Stats(message);
														gui.stat(i+3,"node", true, st.stats);
														user.debug_num=i+3;
														break;
													}
												}
												user.start();

											}catch(Exception e) {e.printStackTrace();}
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
