package jp.sugoi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Map;
import java.util.Map.Entry;

public class User extends Thread{
	Socket s;
	int ip_num=0;
	int debug_num=0;
	boolean light=false;
	String transactions="";
	int b;
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
						if(line.startsWith("block~")) {
							if(!Main.mati) {
								System.out.println("ブロックを受信");
								String blocks=line.split("~")[1];
								Block b=new Block(blocks,false,Main.min,Main.utxo);
								if(b.previous_hash.equals(Main.getlatesthash())) {
									if(b.ok) {
										Main.addBlock(b.sum);
										User.share("block~", b.sum, s);
										System.out.println("[ユーザー]書き込みました。");
									}else {
										System.out.println("[ブロック]受け取ったブロックが不正");
									}
								}else {
									if(b.number>Main.getBlockSize()) {
										s.getOutputStream().write(("getfrom~"+Main.getlatesthash()+"\r\n").getBytes());
										System.out.println("["+(++Main.getfrom)+"][ユーザー]getfromを送信しました : \r\n  getfrom~"+Main.getlatesthash());
									}else {
										System.out.println("記述し終えたブロックが送られてきた。");
									}
								}
							}
						}else if(line.startsWith("trans~")||line.startsWith("disc_transaction~")) {
							String[] tr=line.split("~");
							String from=tr[1].split("@")[0];
							String from_shou=from.split("0x0a")[0];
							BigDecimal amount=new BigDecimal(0.0);
							for(String s:Main.pool) {
								Transaction t=new Transaction(s,new BigDecimal(0),Main.utxo);
								if(t.input.equals(from_shou)) {
									amount=amount.add(t.amount);
								}
							}
							Transaction t=new Transaction((tr[0].equals("disc_transaction"))?tr[2]:tr[1],amount,Main.utxo);
							if(t.ok) {
								s.getOutputStream().write((t.hash+"~ok\r\n").getBytes());
								transactions=transactions+((transactions.equals(""))?"":"0x0a")+t.hash;
								Main.pool.add(t.transaction_sum);
								User.share("trans~", t.transaction_sum,s);
							}else {
								s.getOutputStream().write((t.hash+"~denny\r\n").getBytes());
								//notice=false;
							}
						}else if(line.startsWith("notfound")){
							System.out.println("["+Main.getfrom+"][ユーザー]notfoundが送られてきた。");
							s.getOutputStream().write(("getfrom~"+Main.getHash(3)+"\r\n").getBytes());
						}else if(line.startsWith("blocks~")) {
							System.out.println(line);
							if(!Main.mati) {
								String st=line.split("~")[1];
								String[] args=st.split("0x0f");
								Block[] blocks=new Block[args.length];
								int i=0;
								boolean ok=true;
								if(args[0]==null) {continue;}
								Map<String,BigDecimal> temp_utxo=Main.utxo;
								
								Block bl=new Block(args[0],true,null,Main.utxo);
								if(bl.ok) {
									BigInteger temp_diff=Main.getBlock(bl.number-1).diff;
									for(Transaction t:bl.ts) {
										if(t.ok) {
											temp_utxo.put(t.from, temp_utxo.get(t.from).subtract(t.amount.add(t.fee)));
											for(Entry<String,BigDecimal> add:t.Address_Amount.entrySet()) {
												temp_utxo.put(add.getKey(),temp_utxo.get(add.getKey()).add(add.getValue()));
											}
										}
									}
									for(String s:args) {
										Block b=new Block(s,false,temp_diff,temp_utxo);
										if(b.ok) {
											for(Transaction t:b.ts) {
												if(t.ok) {
													temp_utxo.put(t.from, temp_utxo.get(t.from).subtract(t.amount.add(t.fee)));
													for(Entry<String,BigDecimal> add:t.Address_Amount.entrySet()) {
														temp_utxo.put(add.getKey(),temp_utxo.get(add.getKey()).add(add.getValue()));
													}
												}
											}
											blocks[i]=b;
										}else {
											ok=false;
											break;
										}
										i++;
									}
									if(ok) {
										if(blocks[0].previous_hash.equals(Mining.hash( Main.getBlock(blocks[0].number-1).sum))) {
										if(blocks[blocks.length-1].number>Main.getBlockSize()) {
											Main.mati=true;
											Main.delfrom(blocks[0].number);
											for(Block b:blocks) {
												Main.addBlock(b.sum);
											}
											Main.mati=false;
											Main.readHash();
										}
										}else {
											System.out.println("[ユーザー]処理中断：受け取ったブロックが自分の持っているブロックとつながらない。");
										}
									}else {
										System.out.println("[ユーザー]処理中断：not ok");
									}
								}
							}
						}else if(line.startsWith("getfrom~")) {
							System.out.println("["+(++Main.getfrom)+"][ユーザー]getfromが送られてきました\r\n  "+line);
							try {
								String hash=line.split("~")[1];
								int block_number=Main.getNumber(hash);
								if(!(block_number<0)) {//                                                 3
									Block[] list=new Block[(Main.getBlockSize()+1)-block_number];
									int shoki=block_number;
									boolean ok=true;
									for(;block_number<=Main.getBlockSize();block_number++) {
										Block b=Main.getBlock(block_number);
										if(b==null) {System.out.println("[ユーザー]ブロックを読み込めず。");ok=false;break;}
										if(b.ok) {
											//4?                     4
											list[block_number-shoki]=b;
										}else {
											ok=false;
											break;
										}
									}
									System.out.println("["+Main.getfrom+"][block]ok? : " + ok);
									if(ok) {
										int i=0;
										StringBuilder sb=new StringBuilder();
										for(Block b:list) {
											if(i==0) {
												sb.append(b.sum);
											}else {
												sb.append("0x0f"+b.sum);
											}
											i++;
										}
										s.getOutputStream().write(("blocks~"+sb.toString()+"\r\n").getBytes());
										System.out.println("["+Main.getfrom+"][ユーザー]お繰り返した");
									}
								}else {
									s.getOutputStream().write("notfound\r\n".getBytes());
									System.out.println("["+Main.getfrom+"][ユーザー]hashが見つかりませんでした.");
								}
							}catch(Exception e) {e.printStackTrace();}
						}else if(line.startsWith("balance~")){
							String[] str=line.split("~");
							String add=str[2];
							BigDecimal d=(Main.utxo.get(add.split("0x0a")[0])==null)?new BigDecimal(0.0):Main.utxo.get(add.split("0x0a")[0]);
							s.getOutputStream().write(("balance~"+line.split("~")[1]+"~"+add+"~"+d+"\r\n").getBytes());
							System.out.println("balance~"+line.split("~")[1]+"~"+add+"~"+d+"\r\n");
						}else if(line.equals("light")) {
							light=true;
						}else if(line.equals("notice")) {
							//notice=true;
							System.out.println("noticeをテュルーにした！");
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("[ユーザー]IOストリームを開けませんでした");
				remove();
			}
		};
		th.start();
	}

	synchronized public void remove() {

		System.out.println("ID"+b);
		int i=0;
		try {
			if(!s.isClosed())s.close();
			System.out.println("[ユーザー]自身を削除した : "+i+","+b);
			Main.u.remove(this);
			}catch(Exception e) {System.out.println("[User]削除ミス");}
	}

	public static void share(String type,String st,Socket s) {
		for(User u:Main.u) {
			if(u==null) {continue;}
			if(!(u.s.getInetAddress().getHostAddress().equals(s.getInetAddress().getHostAddress())&&u.s.getPort()==s.getPort())) {
				try {
					OutputStream os=u.s.getOutputStream();
					if(!u.light) {
						os.write((type+st).getBytes());
						System.out.println("send\r\n"+st);
						os.write("\r\n".getBytes());
						os.flush();
					}
				} catch (Exception e) {
					System.out.println("[ユーザー]"+e.getMessage());
					u.remove();

				}
			}
		}
	}

}
