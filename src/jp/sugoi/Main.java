package jp.sugoi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;


//
/**
 *TODO:VPSで使うために、早急にCLIですべての操作ができるようにする。<br>
 * @author yutadd
 */

public class Main {
	static boolean haikei_nashi=false;
	static Map<String,BigDecimal> utxo=new HashMap<String,BigDecimal>();
	static int BANGO=0;
	static int getfrom=0;
	static boolean mati=false;
	static String buffer=new Random().nextInt()+"";
	static String latestHash=null;
	static long[] time= {6000,6000};
	static String name="XGW";
	static ArrayList<String> pool=new ArrayList<>();
	static Wallet w;
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
	static GUI gui;
	static Socket s = null;
	static Block rsv_Block;
	static boolean mining=false;
	static Mining m;
	static int size=0;
	static BigInteger min=new BigInteger("26611349253966442813730644663330183884399686815584447189708332380985641",10);
	static BigInteger shoki=new BigInteger("26611349253966442813730644663330183884399686815584447189708332380985641",10);
	
	public static void main(String[] args) {
		System.out.println(System.getProperty("file.encoding"));
		gui=new GUI();
		w=new Wallet();
		readHash();

		for(int i=0;i<30;i++) {
			System.out.println();
		}
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
					String s=sc.nextLine();
					String add=s;
					BigDecimal d=(Main.utxo.get(add.split("0x0a")[0])==null)?new BigDecimal(0.0):Main.utxo.get(add.split("0x0a")[0]);
					System.out.println("this wallet is : "+d);
					System.out.println("returned "+add+" , "+d);
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
					System.out.println("==========↑Stats↑==========");
				}
			}
		};
		th_.start();
		new DNS();
	}
	/*static void gui_check() {
		for(User us:u) {
			if(us!=null) {
				gui.ips[us.ip_num].setText(us.s.getInetAddress().getHostAddress());
				String[][] message={
						{"name","Node"},
						{"IP", us.s.getInetAddress().getHostAddress()},
				};
				Stats st=new Stats(message);
				//gui.stat(us.debug_num,"node", true, st.stats);
			}
		}
	}*/
	static void read_time() {
		return;
	}
	//データベースに格納するものだから一番最初に
	static void readHash() {
		int time_sum=0;
		mati=true;
		int i=0;
		間隔=new long[1];
		難易度 = new long[1];
		pool=new ArrayList<>();
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
				} catch (FileNotFoundException e) {System.out.println("File Not Found");}
				while(true) {
					try {
						String line = bs.readLine();
						if(line==null) {bs.close();System.out.println("[ブロック]EOF");break;}
						Block b=new Block(line,false,min,utxo);
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
						time_sum+=time[0]-time[1];
						System.out.println("[ブロック]平均掘削時間:"+ ((time_sum/i-5))/1000);
					}catch(Exception e) {System.out.println("[ブロック]minの計算中にエラーが発生しました");time[0]=6000;time[1]=6000;}
					size=i;
					min=getMin(true);
				}
			}else {
				System.out.println("[ブロック]ファイルがもう見当たりません.");
				break;
			}
		}


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
				System.out.println("[メイン]br.readLine()でエラーが起きた.");
				return "exception";
			}finally {
				try {br.close();} catch (IOException e) {e.printStackTrace();}
			}
		}
	}
	static void addBlock(String block) {
		Block blo=new Block(block,true,min,utxo);
		int numb=blo.number;
		System.out.println("[メイン]このブロックのナンバー: "+numb);
		System.out.println("[メイン]セーブされたブロックの数: "+getBlockSize());
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
			b=new Block(s,true,null,utxo);
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
		Block b=new Block(arg,false,min,utxo);
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
					pool.remove(t.transaction_sum);
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
			}catch(Exception e) {System.out.println("[メイン]minの計算中にエラーが発生しました");time[0]=6000;time[1]=6000;}
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
					} catch (FileNotFoundException e) {System.out.println("File Not Found");}
					while(true) {
						try {
							String line = bs.readLine();
							if(line==null) {bs.close();System.out.println("EOF");break;}
							Block b=new Block(line,true,min,utxo);
							for(Transaction t:b.ts) {
								pool.add(t.transaction_sum);
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
		}catch(Exception e) {System.out.println("[メイン]minの計算中にエラーが発生しました");time[0]=6000;time[1]=6000;}
		min=getMin(false);
	}
	static BigInteger getMin(boolean show){
		read_time();
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
				return min.add(i);
			}
		}else {
			return shoki;
		}
	}
}
