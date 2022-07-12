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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.jna.Function;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;


//
/**
 * マイニングしているかどうかをメインに表示。(nonceも表示してみる)
 * ステータスの表示に時間を加える。
 * @author yutadd
 */
class TreeMap2<K,V> extends ConcurrentHashMap<K,V>{
	@SuppressWarnings("unchecked")
	public V put(K key,V value){
		if(key instanceof String&& value instanceof String) {
			if(((String)key).contains("E-"))key=(K) ((String)key+" "+new SimpleDateFormat("HH時mm分ss秒").format(new Date()));
		}
		if(((String)key).matches(".*E-.*")) {
			System.out.println("[\033[31m"+((String)key)+"\033[37m]"+((String)value)+"\033[37m");
		}else if(((String)key).matches(".*I-.*")) {
			System.out.println("[\033[34m"+((String)key)+"\033[37m]"+((String)value)+"\033[37m");
		}else  if(((String)key).matches(".*")) {
			System.out.println("[\033[32m"+((String)key)+"\033[37m]"+((String)value)+"\033[37m");
		}
		return value;
		//return super.put(key,value);
	}
}

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
	static TreeMap2<String, String> console=new TreeMap2<String,String>();
	static long[] 間隔=new long[1];
	static long[] 難易度 = new long[1];
	static ArrayList<User> u=new ArrayList<User>();
	static String mining_block_sum="";

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
		if(System.getProperty("os.name").startsWith("Windows"))
		{
			// Set output mode to handle virtual terminal s	equences
			Function GetStdHandleFunc = Function.getFunction("kernel32", "GetStdHandle");
			DWORD STD_OUTPUT_HANDLE = new DWORD(-11);
			HANDLE hOut = (HANDLE)GetStdHandleFunc.invoke(HANDLE.class, new Object[]{STD_OUTPUT_HANDLE});
			DWORDByReference p_dwMode = new DWORDByReference(new DWORD(0));
			Function GetConsoleModeFunc = Function.getFunction("kernel32", "GetConsoleMode");
			GetConsoleModeFunc.invoke(BOOL.class, new Object[]{hOut, p_dwMode});
			int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;
			DWORD dwMode = p_dwMode.getValue();
			dwMode.setValue(dwMode.intValue() | ENABLE_VIRTUAL_TERMINAL_PROCESSING);
			Function SetConsoleModeFunc = Function.getFunction("kernel32", "SetConsoleMode");
			SetConsoleModeFunc.invoke(BOOL.class, new Object[]{hOut, dwMode});
		}
		/* 



		以前生成した公開鍵x,yがあり、このxから同じyを導き出したい。	
		BigInteger x=new BigInteger("f7723c38398cef511bc83c70dd8733efb401e60563245ca9969997e2e93c5db9",16);
		BigInteger y=new BigInteger("b90c7a7fcabdd98291b2df5d3488a930413f7d0399ab449dafd95e556f1ea0a3",16);


		//そもそも、https://qiita.com/ryo0301/items/0bc9ccfb3291cabd50d5 で紹介されている y^2=x^3+7 が以前生成した鍵のでは成り立たない。
		//というか公開鍵って曲線上じゃないもしや？
		System.out.println(x.pow(3).add(new BigInteger("7")).toString(16));
		System.out.println(y.pow(2));
		//x^3+7   e72fc307743260d1b4b184bb2968c3797bf9ccb026306004c9b83cc550afc27239274041a0ab935c0b27a356edd0b1ca0ca2b7fc4abaf3e93998f708a24eef9ca651b6ec9661e5d1ad9e16192bd6e2a795fcb41997c316667c29c8b2a94b83f0
		//y^2     7005677379887140445805822117143064195372430554416381098174919741378312732591761318729693609592313066632967724110040127402969407546304486671992055318718409
		//等式が成り立たない


		//両辺をmod(p)すると等式が成り立つ。
		BigInteger p=new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F",16);
		System.out.println(x.pow(3).add(new BigInteger("7")).remainder(p).toString(16));
		System.out.println(y.pow(2).remainder(p).toString(16));
		//f0283dece25386b67bdcfd53ea48e4130d8bda1667aa70f433f85046f64371f9
		//f0283dece25386b67bdcfd53ea48e4130d8bda1667aa70f433f85046f64371f9

		//ちなみに、y=sqrt(x^3+7)だと
		System.out.println(x.pow(3).add(new BigInteger("7")).sqrt().toString(16));
		//f346f1f8bf7282a7110365a58e3f355d7af24666b0c11aedc68700c334dd5dcd4b3258aef06b91365ae8f9c6224824c4

		 */


		System.out.println(System.getProperty("file.encoding"));
		w=new Wallet();
		addManuals();
		/*Thread th1=new Thread() {
			@Override
			public void run() {
				while(true) {
					if(console_mode.equals("live")) {
						console_reset();
						System.out.println("現在時刻："+new SimpleDateFormat("HH時mm分ss秒").format(new Date())+"\033[37m");
						TreeMap<String,String> ent=new TreeMap<String,String>();
						ent.putAll(console);
						for(Entry<String,String> e:ent.entrySet()) {
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
						console.put("MAINI-04",console.get("MAINI-04")+" "+us.s.getInetAddress().getHostAddress());
					}
					if(console_mode.equals("live"))System.out.println("モード：\033[42mライブモード\033[49m\r\n\t(ENTERで切り替え)");
					for(int i=0;i<reflesh;i++) {
						if(console_mode.equals("live")) {
							System.out.print("\r"+((reflesh-i)/10.0)+"  ");
						}
						try{
							Thread.sleep(100);
						}catch(Exception e) {};
					}

				}
			}
		};
		th1.start();
		Thread th_clear=new Thread() {
			public void run() {
				while(true) {
					if(console_mode.equals("live")) {
						console_clear();
					}
					try {
						sleep(4000);
					} catch (InterruptedException e) {}
				}
			}
		};
		th_clear.start();
		
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
		};Dr_AI.start();*/
		readHash();
		mining=true;
		new Mining();
		//65261
		Thread th=new Thread(new Server());
		th.start();
		Thread th_=new Thread() {
			@Override
			public void run() {
				Scanner sc=new Scanner(System.in);
				//InputStreamReader isr=new InputStreamReader(System.in);
				//char c;
				while(true) {
					System.out.print("\033[34m┌──(\033[31mGWC\033[37m x💀x \033[31mCMD\033[34m)-[\033[37mBlock Size :"+size+"\033[34m]\r\n└─#\033[37m");
					String s=sc.nextLine();
					/*String s="";
					try {
						while((c=(char)isr.read())!=(char)-1) {
							if(c!='\r'&&c!='\n') {
								System.out.print(c);
							s+=c;
							}else {
								break;
							}
						}
					} catch (IOException e) {
						// TODO 自動生成された catch ブロック
						break;
					}*/
					String cmd=s.split(" ")[0];
						if(cmd.equals("pay")) {
							new Pay(s);
						}else if(cmd.equals("mining")) {
							if(mining) {
								mining=false;
								System.out.println("\033[31mMINING STOPPED.");
							}else {
								mining=true;
								new Mining();
								System.out.println("\033[32mMINING STARTED.");
							}
						}else if(cmd.equals("stats")){
							System.out.println("==========↓Stats↓==========");
							System.out.println("My wallet balance : "+utxo.get(w.pub[0].toString(16)));
							System.out.println("mati"+mati);
							System.out.println("blockSize : "+getBlockSize());
							String now_10=min.toString(10);
							String def_10=shoki.toString(10);
							System.out.println("NOW : "+now_10+"\r\n"+" 初期 - 今: "+shoki.subtract(min).toString(10)+"\r\n"+"DEF : "+def_10);
							//gui_check();
							for(Entry<String,BigDecimal> set:utxo.entrySet()) {
								System.out.printf("%s : %f \r\n",set.getKey(),set.getValue().doubleValue());
							}
							System.out.println("YOUR ADDRESS : "+w.address_0x0a);
							System.out.println("==========↑Stats↑==========");
						}else if(cmd.equals("help")) {
							System.out.println();
							System.out.println(man);
							System.out.println();
						}else if(cmd.equals("clear")||cmd.equals("cls")){
							console_clear();
						}else {
							System.out.println("execute→"+s);
							try {
								if (System.getProperty("os.name").contains("Windows")) {
									new ProcessBuilder("cmd","/c",s).inheritIO().start().waitFor();
								}else {
									new ProcessBuilder(s.split(" ")).inheritIO().start().waitFor();
								}
							} catch (IOException | InterruptedException ex) {}
						}
				}
			}
		};
		th_.start();

		new DNS();
	}
	/*
	 * public static void console_reset() {
		System.out.print("\033[0;0H");
		System.out.print("\033[0;0f");
	}
	*/
	public static void console_clear(){

		/*for(int i=0;i<console.size();i++) {
			System.out.print("\033[1A");
		}*/

		//System.out.print("\033[2J");
		//Clears Screen in java


		try {
			if (System.getProperty("os.name").contains("Windows"))
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			else
				new ProcessBuilder("/bin/clear").inheritIO().start().waitFor();
		} catch (IOException | InterruptedException ex) {}
	}
	/**ジェネシスブロックがあるため、チェックを行わない。*/
	static HashMap<String,BigDecimal> readhash(int leng){
		boolean shuryo=false;
		HashMap<String,BigDecimal> result=new HashMap<String,BigDecimal>();
		for(int a=1;!shuryo;a++) {
			File file=new File("Blocks"+File.separator+"Block-"+a);
			FileReader is=null;
			BufferedReader bs=null;
			try {
				is=new FileReader(file);
				bs=new BufferedReader(is);
			} catch (FileNotFoundException e) {console.put("MAINE-00","File Not Found");}
			if(file.exists()) {
				for(int i=1;i<=10000;i++) {
					if(leng<i+(a-1)*10000) {
						shuryo=true;
						break;
					}
					try {
						String line = bs.readLine();
						if(line==null) {bs.close();shuryo=true;break;}
						Block b=new Block(line,BigInteger.ZERO,new HashMap<String,BigDecimal>(),true);
						for(Transaction t:b.ts) {
							BigDecimal bal=result.get(t.input);
							if(bal==null)bal=new BigDecimal(0);
							result.put(t.input,bal.subtract(t.sum_minus));
							for(Output o:t.out) {
								result.put(o.address[0].toString(16),
										checkNullAndGetValue(result,o.address[0].toString(16)).add(o.amount)
										);
							}
						}
						BigDecimal m_balance=result.get(b.miner);
						if(m_balance==null)m_balance=new BigDecimal(0);
						result.put(b.miner,m_balance.add(new BigDecimal(50.0)));
					}catch(Exception e) {return null;}
				}
			}
		}
		return result;
	}
	/**ジェネシスブロックがあるため、チェックを行わない。<br>一番最初に呼んで*/
	static void readHash() {
		mati=true;
		boolean shuryo=false;
		間隔=new long[1];
		難易度 = new long[1];
		pool=new ArrayList<Transaction>();
		utxo.clear();
		min=shoki;
		for(int a=1;!shuryo;a++) {
			File file=new File("Blocks"+File.separator+"Block-"+a);
			FileReader is=null;
			BufferedReader bs=null;
			try {
				is=new FileReader(file);
				bs=new BufferedReader(is);

			} catch (FileNotFoundException e) {console.put("MAINE-00","File Not Found");}
			if(file.exists()) {
				for(int i=1;i<=10000;i++) {
					try {
						String line = bs.readLine();
						if(line==null) {bs.close();shuryo=true;break;}
						Block b=new Block(line,min,utxo,i<=4);
						b.give_utxo(false);
						for(Transaction t:b.ts) {
							t.doTrade();
						}
						latestHash=Mining.hash(b.sum);
					} catch (IOException e) {int r=0;for(StackTraceElement ste:e.getStackTrace())Main.console.put("MAINE-2-"+r++,ste.toString());break;}
					size=i+(a-1)*10000;
					//if(i>=6) {
					try {
						Block b=getBlock(i);
						time[0]=b.time;
						time[1]=getBlock(b.number-1).time;
						//time_sum+=time[0]-time[1];
						//System.out.println("[ブロック]平均掘削時間:"+ ((time_sum/i-5))/1000);
					}catch(Exception e) {console.put("MAINE-02","[ブロック]minの計算中にエラーが発生しました");time[0]=6000;time[1]=6000;}
					size=i;
					min=getMin(true);
					//}

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
		File file=new File("Blocks"+File.separator+"Block-"+((number/10000)+1));
		if(!file.exists()) {
			return null;
		}else {
			String s = null;
			BufferedReader br = null;
			try {
				int count=1;
				br = new BufferedReader(new FileReader(file));
				while ((s = br.readLine()) != null) {
					if (number%10000 == count++) {
						br.close();
						return Mining.hash(s);
					}
				}
				return null;
			} catch (FileNotFoundException e) {
				return null;
			} catch (IOException e) {
				console.put("MAINE-03","br.readLine()でエラーが起きた.");
				return null;
			}finally {
				try {br.close();} catch (IOException e) {e.printStackTrace();}
			}
		}
	}
	static void addBlock(String block) {
		Block blo=new Block(block,min,utxo,true);
		int numb=blo.number;
		console.put("MAIN04","このブロックのナンバー: "+numb);
		console.put("MAIN05","セーブされたブロックの数: "+getBlockSize());
		if(numb>getBlockSize()) {
			delfrom(numb);
			saveBlock(block);
		}
	}
	/**GETBLOCKは保存された後のブロックを利用するため、チェックを行わない。*/
	static Block getBlock(int numb) {
		File file=new File("Blocks"+File.separator+"Block-"+((numb/10000)+1));
		String s;
		int count=1;
		Block b=null;
		try {
			BufferedReader br=new BufferedReader(new FileReader(file));
			while ((s = br.readLine()) != null) {
				if (numb%10000 == count++) {
					br.close();
					b=new Block(s,BigInteger.ZERO,utxo,true);
					return b;
				}
			}
			Main.console.put("見つからない", "です"+numb);
			br.close();
			return null;
		}catch(Exception e) {e.printStackTrace();}
		return null;
	}
	static int getNumber(String hash) {
		boolean syuryo=false;
		for(int a=1;!syuryo;a++) {
			File file=new File("Blocks"+File.separator+"Block-"+a);
			FileReader is=null;
			BufferedReader bs=null;
			try {
				is=new FileReader(file);
				bs=new BufferedReader(is);
			} catch (FileNotFoundException e) {console.put("MAINE-00","File Not Found");}
			if(file.exists()) {
				for(int i=1;i<=10000;i++) {
					try {
						String line = bs.readLine();
						if(line==null) {bs.close();syuryo=true;break;}
						if(Mining.hash(line).equals(hash))return i;
					}catch(Exception e) {e.printStackTrace();}
				}
			}else {
				return -1;
			}
		}
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
	/**セーブすることが目的なので、チェックを行いません*/
	private static void saveBlock(String arg) {
		Block b=new Block(arg,BigInteger.ZERO,new HashMap<String,BigDecimal>(),true);
		File file=new File("Blocks"+File.separator+"Block-"+((b.number/10000)+1));
		try {
			file.createNewFile();
			FileWriter fw=new FileWriter(file,true);
			fw.write(arg+System.getProperty("line.separator"));
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
		}catch(Exception e) {console.put("MAINE-06","minの計算中にエラーが発生しました");time[0]=6000;time[1]=6000;}
		min=getMin(true);

	}
	static String getlatesthash() {
		return latestHash;
	}
	static void delfrom(int from) {
		try {
			File file=new File("Blocks"+File.separator+"Block-"+((from/10000)+1));
			FileReader is=null;
			BufferedReader bs=null;
			is=new FileReader(file);
			bs=new BufferedReader(is);
			ArrayList<String> line=new ArrayList<String>();
			for(int i=1;i<from;i++) {
				line.add(bs.readLine());
			}
			bs.close();
			FileWriter fw=new FileWriter(file);
			for(String s:line) {
				fw.append(s+System.getProperty("line.separator"));
			}
			fw.close();
		}catch(Exception e) {int da=0;for(StackTraceElement ste:e.getStackTrace())Main.console.put("MainE-4-"+da++,ste.toString());}
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
			console.put("MAINE-08", "コマンドリストファイルがありません。");
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
				console.put("MAINE-09", "コマンドリストファイルがありません。");
			} catch (IOException e) {
				console.put("MAINE-10","br.readLine()でエラーが起きた.");
			}finally {
				try {br.close();} catch (IOException e) {e.printStackTrace();}
			}
		}

	}
	public static BigDecimal checkNullAndGetValue(Map<String,BigDecimal> map,String key) {
		BigDecimal bal2=map.get(key);
		if(bal2==null)bal2=new BigDecimal(0);
		return bal2;
	}
}
