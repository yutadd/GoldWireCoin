package jp.sugoi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;


//
/**
 *TODO:VPSで使うために、早急にCLIですべての操作ができるようにする。<br>
 * @author yutadd
 */

public class Main {
	static int ERROR=0;
	static int reflesh=1;
	static boolean haikei_nashi=false;
	static Map<String,BigDecimal> utxo=new HashMap<String,BigDecimal>();
	static int BANGO=0;
	static int getfrom=0;
	static boolean mati=false;
	static String buffer=new Random().nextInt()+"";
	static String latestHash=null;
	static long[] time= {6000,6000};
	static String name="XGW";
	static ArrayList<Transaction> pool=new ArrayList<Transaction>();
	static Wallet w;
	static String man="";
	static TreeMap<String, String> console=new TreeMap<String,String>();
	static long[] 間隔=new long[1];
	static long[] 難易度 = new long[1];
	static ArrayList<User> u=new ArrayList<User>();

	/**]
	 * (ServerSocket)サーバーソケットからIPフィールドを取得するためのもの
	 */
	//static HashMap<User,JTextField> ssock=new HashMap<>();
	/*
	 * (ClientSocket)ClientソケットからIPフィールドを取得するためのもの
	 */
	//static HashMap<User,JTextField> csock=new HashMap<>();

	/**
	 * ソケットとそれに対応するIPTextFieldを記憶する。
	 */
	//static HashMap<User,Integer> debug_lab=new HashMap<>();
	static Block rsv_Block;
	static boolean mining=false;
	static Mining m;
	static int size=0;
	static BigInteger min=new BigInteger("26611349253966442813730644663330183884399686815584447189708332380985641",10);
	static BigInteger shoki=new BigInteger("26611349253966442813730644663330183884399686815584447189708332380985641",10);
	static String console_mode="live";
	public static void main(String[] args) {
		//BigInteger x=new BigInteger("f7723c38398cef511bc83c70dd8733efb401e60563245ca9969997e2e93c5db9",16);
		//System.out.println(new BigInteger("37443777233090894268066866047209366710731662759769367399990945955501994572681872242754721414801382673755834059072708",10).toString(16));
		//System.out.println(x.toString(10));
		//BigInteger y=(x.pow(3).add(BigInteger.valueOf(7))).sqrt();
		//System.out.println(y.toString(16));
		BigInteger x=new BigInteger("f7723c38398cef511bc83c70dd8733efb401e60563245ca9969997e2e93c5db9",16);
		BigInteger y=new BigInteger("b90c7a7fcabdd98291b2df5d3488a930413f7d0399ab449dafd95e556f1ea0a3",16);
		String pub_x;
		if(Integer.parseInt(x.remainder(new BigInteger("2")).toString(10))==0) {
			pub_x='3'+x.toString(16);
		}else {
			pub_x='2'+x.toString(16);
		}
		BigInteger p=new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F",16);
		System.out.println(x.pow(3).add(new BigInteger("7")).sqrt().toString(16));
		//System.out.println(alpha.toString(16));
		//BigInteger beta=(alpha.sqrt().mod(p));
		System.out.println(y.pow(2).mod(p).toString(16));
		System.out.println(x.pow(3).add(new BigInteger("7")).mod(p).toString(16));

		//if(x.remainder(new BigInteger("2")).intValue()==0) {
		//	System.out.println(p.subtract(alpha).toString(16));
		//}else {
		//	System.out.println(alpha.toString(16));
		//}
		System.out.println(System.getProperty("file.encoding"));
		w=new Wallet();
		addManuals();
		Thread th1=new Thread() {
			@Override
			public void run() {
				while(true) {
					try{if(console_mode.equals("live")) {
						console_clear();
						for(Entry<String,String> e:console.entrySet()) {
							if(e.getKey().matches(".*E-.*")) {
								System.out.println("[\033[31m"+e.getKey()+"\033[37m]"+e.getValue()+"\033[37m");
							}else if(e.getKey().matches(".*I-.*")) {
								System.out.println("[\033[34m"+e.getKey()+"\033[37m]"+e.getValue()+"\033[37m");
							}else  if(e.getKey().matches(".*")) {
								System.out.println("[\033[32m"+e.getKey()+"\033[37m]"+e.getValue()+"\033[37m");
							}
						}
					}

					console.put("MAINI-01","My wallet balance : "+utxo.get(w.pub[0].toString(16)));
					console.put("MAINI-02","blockSize : "+getBlockSize());
					console.put("MAINI-03","YOUR ADDRESS : "+w.address_0x0a);
					console.put("MAINI-04","NODES : "+"ME");
					for(User us:u) {
						console.put("MAINI-04",console.get("MAINII-04")+" "+us.s.getInetAddress().getAddress());
					}
					if(console_mode.equals("live"))System.out.println("モード：\033[42mライブモード\033[49m\r\n\t(ENTERで切り替え)");
					for(int i=0;i<reflesh;i++) {
						if(console_mode.equals("live")) {
							System.out.print("\r"+((reflesh-i)/10.0)+"  ");
						}
						Thread.sleep(100);
					}
					}catch(Exception e) {};
				}
			}
		};
		th1.start();
		readHash();
		try {
			//Runtime.getRuntime().exec("cmd /c cls");
		}catch(Exception e) {e.printStackTrace();}
		Thread Dr_AI=new Thread(){
			@Override
			public void run() {
				for(;;) {
					//gui_check();
					try {Thread.sleep(700);} catch (InterruptedException e) {e.printStackTrace();}
				}
			}
		};
		Dr_AI.start();
		Mining m=new Mining();
		m.mining();
		//65261
		Thread th=new Thread(new Server());
		th.start();
		Thread th_=new Thread() {
			@Override
			public void run() {
				Scanner sc=new Scanner(System.in);
				for(;;) {
					System.out.print("\033[34m┌──(\033[31mGWC\033[37m x💀x \033[31mCMD\033[34m)-[\033[37mBlock Size :"+size+"\033[34m]\r\n└─#\033[37m");
					String s=sc.nextLine();
					String cmd=s.split(" ")[0];
					if(console_mode.equals("cmd")) {
						if(cmd.equals("pay")) {
							new Pay(s);
						}else if(cmd.equals("mining")) {

						}else if(cmd.equals("stats")){
							System.out.println("==========↓Stats↓==========");
							System.out.println("My wallet balance : "+utxo.get(w.pub[0].toString(16)));
							System.out.println("mati"+mati);
							System.out.println("blockSize : "+getBlockSize());
							String now_10=min.toString(10);
							String def_10=shoki.toString(10);
							System.out.println("NOW : "+now_10+"\r\n"+" 初期 - 今: "+shoki.subtract(min).toString(10)+"\r\n"+"DEF : "+def_10);
							//gui_check();
							int i=0;
							for(;i<間隔.length;i++) {
								if(i<=3&&i>=間隔.length-3) {
									System.out.print(i+" : ");
									try {
										System.out.println("間隔 : "+間隔[i]+" 難易度 : "+難易度[i]);
									}catch(Exception e) {
										//TODO Do Nothing
									}
								}
							}
							for(Entry<String,BigDecimal> set:utxo.entrySet()) {
								double bd=set.getValue().doubleValue();
								System.out.printf("%s : %f \r\n",set.getKey(),bd);
							}
							System.out.println("YOUR ADDRESS : "+w.address_0x0a);
							System.out.println("==========↑Stats↑==========");
						}else if(cmd.equals("")) {
							console_mode="live";
						}else if(cmd.equals("live")) {
							console_mode="live";
						}else if(cmd.equals("help")) {
							System.out.println();
							System.out.println(man);
							System.out.println();
						}else if(cmd.equals("clear")||cmd.equals("cls")){
							console_clear();
						}else {
							String[] args=s.split(" ");
							System.out.println("execute→"+s);
							try {
								if (System.getProperty("os.name").contains("Windows")) {
									new ProcessBuilder("cmd","/c",s).inheritIO().start().waitFor();
								}else {
									Runtime.getRuntime().exec(s);}
							} catch (IOException | InterruptedException ex) {}
						}

					}else {
						console_mode="cmd";
						console_clear();
						for(Entry<String,String> e:console.entrySet()) {
							if(e.getKey().matches(".*E-.*")) {
								System.out.println("\033[31m["+e.getKey()+"]"+e.getValue()+"\033[37m");
							}else if(e.getKey().matches(".*I-.*")) {
								System.out.println("\033[34m["+e.getKey()+"\033[37m]"+e.getValue()+"\033[37m");
							}else  if(e.getKey().matches(".*")) {
								System.out.println("\033[32m["+e.getKey()+"\033[37m]"+e.getValue());
							}
						}
						System.out.println("モード：\033[44mコマンド入力モード\033[49m\r\n\t(ENTERで切り替え)");

					}
				}
			}
		};
		th_.start();

		new DNS();
	}
	public static void console_clear(){
		//System.out.print("\033[2J");
		//Clears Screen in java
		try {
			if (System.getProperty("os.name").contains("Windows")) {
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			}else {
				Runtime.getRuntime().exec("clear");
			}
		} catch (IOException | InterruptedException ex) {ex.printStackTrace();}
	}
	//データベースに格納するものだから一番最初に
	static void readHash() {
		mati=true;
		int i=0;
		間隔=new long[1];
		難易度 = new long[1];
		pool=new ArrayList<Transaction>();
		utxo.clear();
		min=shoki;
		for(i=1;;i++) {
			File file=new File("Blocks"+File.separator+"Block-"+i);
			if(file.exists()) {
				FileReader is=null;
				BufferedReader bs=null;
				try {
					is=new FileReader(file);
					bs=new BufferedReader(is);
				} catch (FileNotFoundException e) {console.put("MAINE-00","File Not Found");}
				while(true) {
					try {
						String line = bs.readLine();
						if(line==null) {bs.close();break;}
						Block b=new Block(line,false,min,utxo,false);
						b.give_utxo(false);
						for(Transaction t:b.ts) {
							t.doTrade();
						}
						latestHash=Mining.hash(b.sum);
					} catch (IOException e) {e.printStackTrace();break;}
					try{Thread.sleep(1);}catch(Exception e) {e.printStackTrace();}
				}
				size=i;
				if(i>=6) {
					try {
						Block b=getBlock(i);
						time[0]=b.time;
						time[1]=getBlock(b.number-1).time;
						//time_sum+=time[0]-time[1];
						//System.out.println("[ブロック]平均掘削時間:"+ ((time_sum/i-5))/1000);
					}catch(Exception e) {console.put("MAINE-01","[ブロック]minの計算中にエラーが発生しました");time[0]=6000;time[1]=6000;}
					size=i;
					min=getMin(true);
				}
			}else {
				break;
			}
		}
		reflesh=2;

		mati=false;
	}
	static int getBockSizeFrom(int i){
		return getBlockSize()-i;
	}
	static int getBlockSize() {
		return size;
	}
	static String getHash(int number) {
		File file=new File("Blocks"+File.separator+"Block-"+number);
		if(!file.exists()) {
			return "notexists";
		}else {
			String s = null;
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file));
				s = br.readLine().trim();
				br.close();
				return Mining.hash(s);
			} catch (FileNotFoundException e) {
				return "notexists";
			} catch (IOException e) {
				console.put("MAINE-02","br.readLine()でエラーが起きた.");
				return "exception";
			}finally {
				try {br.close();} catch (IOException e) {e.printStackTrace();}
			}
		}
	}
	static void addBlock(String block) {
		Block blo=new Block(block,true,min,utxo,false);
		int numb=blo.number;
		console.put("MAIN03","このブロックのナンバー: "+numb);
		console.put("MAIN04","セーブされたブロックの数: "+getBlockSize());
		if(numb>getBlockSize()) {
			delfrom(numb);
			saveBlock(block);
		}
	}
	static Block getBlock(int numb) {
		File file=new File("Blocks"+File.separator+"Block-"+numb);
		String s;
		try {
			BufferedReader br=new BufferedReader(new FileReader(file));
			s=br.readLine().trim();
			br.close();
			Block b=null;
			b=new Block(s,true,null,utxo,false);
			return b;
		}catch(Exception e) {e.printStackTrace();}
		return null;
	}
	static int getNumber(String hash) {
		for(int i=1;i<=getBlockSize();i++) {
			File file=new File("Blocks"+File.separator+"Block-"+i);
			String s;
			try {
				BufferedReader br=new BufferedReader(new FileReader(file));
				s=br.readLine().trim();
				br.close();
				if(Mining.hash(s).equals(hash)) {
					return i;
				}
			}catch(Exception e) {e.printStackTrace();return -1;}
		}
		return -1;
	}
	private static void saveBlock(String arg) {
		Block b=new Block(arg,false,min,utxo,false);
		if(b.ok) {
			File file=new File("Blocks"+File.separator+"Block-"+b.number);
			try {
				file.createNewFile();
				FileWriter fw=new FileWriter(file);
				fw.write(arg);
				fw.flush();
				fw.close();
				for(Transaction t :b.ts) {
					t.doTrade();//取引完了させる
				}
				for(Transaction t:b.ts) {
					pool.remove(t);
				}
				b.give_utxo(false);
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			latestHash=Mining.hash(b.sum);
			size=b.number;
			try {
				time[0]=b.time;
				time[1]=getBlock(b.number-1).time;
			}catch(Exception e) {console.put("MAINE-05","minの計算中にエラーが発生しました");time[0]=6000;time[1]=6000;}
			min=getMin(true);
		}

	}
	static String getlatesthash() {
		return latestHash;
	}
	static void delfrom(int from) {
		for(int i=size;from<=i;i--) {
			File file=new File("Blocks"+File.separator+"Block-"+(i));
			if(file.exists()) {
				try {
					FileReader is=null;
					BufferedReader bs=null;
					try {
						is=new FileReader(file);
						bs=new BufferedReader(is);
					} catch (FileNotFoundException e) {console.put("DELFROM","File Not Found");}
					while(true) {
						try {
							String line = bs.readLine();
							if(line==null) {bs.close();break;}
							Block b=new Block(line,true,min,utxo,false);
							for(Transaction t:b.ts) {
								pool.add(t);
								utxo.put(t.from.split("0x0a")[0],utxo.get(t.from.split("0x0a")[0]).add(t.amount));
								for(String s: t.Address_Amount.keySet()) {
									utxo.put(s,utxo.get(s).subtract(t.Address_Amount.get(s)));
								}
							}
						} catch (IOException e) {e.printStackTrace();break;}
					}
					file.delete();
				}catch(Exception e) {System.out.println("maaiiya");}
			}else {break;}
		}
		latestHash=getHash(from-1);
		size=from-1;
		try {
			time[0]=getBlock(from-1).time;
			time[1]=getBlock(from-2).time;
		}catch(Exception e) {console.put("MAINE-06","minの計算中にエラーが発生しました");time[0]=6000;time[1]=6000;}
		min=getMin(false);
	}
	static BigInteger getMin(boolean show){
		Long sa=Main.time[0]-Main.time[1];
		long result=sa-60000;

		if(!(size<=4)) {
			if(sa>60000) {
				//時間が60秒オーバー：もっとかんたんに：数値にプラス
				if(show) {
					long[] temp=new long[間隔.length+1];
					間隔[間隔.length-1]=sa;
					System.arraycopy(間隔, 0, temp, 0, 間隔.length);
					間隔=temp;
					long[] temp_d=new long[難易度.length+1];
					難易度[難易度.length-1]=result;
					System.arraycopy(難易度, 0, temp_d, 0, 難易度.length);
					難易度=temp_d;
				}
				BigInteger i=new BigInteger("2910A562CF81F8A20CB31817F4350CA75ECF1CB59BED6E75AB6AEB1F4",16);
				return min.add(i);
			}else {
				//６０秒以下：もっと難しく:数値にマイナス
				if(show) {
					long[] temp=new long[間隔.length+1];
					間隔[間隔.length-1]=sa;
					System.arraycopy(間隔, 0, temp, 0, 間隔.length);
					間隔=temp;
					long[] temp_d=new long[難易度.length+1];
					難易度[難易度.length-1]=result;
					System.arraycopy(難易度, 0, temp_d, 0, 難易度.length);
					難易度=temp_d;
				}
				BigInteger i=new BigInteger("2910A562CF81F8A20CB31817F4350CA75ECF1CB59BED6E75AB6AEB1F4",16);
				return min.subtract(i);
			}
		}else {
			return shoki;
		}
	}
	private static void addManuals() {
		// TODO 自動生成されたメソッド・スタブ
		File file=new File("Commands.txt");
		if(!file.exists()) {
			console.put("MAINE-07", "コマンドリストファイルがありません。");
		}else {
			String s = "";
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file,Charset.forName("utf-8")));
				char c = 0;
				while((c=(char)br.read())!=(char)-1)s+=c;
				br.close();
				man=s;
			} catch (FileNotFoundException e) {
				console.put("MAINE-08", "コマンドリストファイルがありません。");
			} catch (IOException e) {
				console.put("MAINE-09","br.readLine()でエラーが起きた.");

			}finally {
				try {br.close();} catch (IOException e) {e.printStackTrace();}
			}
		}

	}
}
