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
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;


//
/**
 * マイニングしているかどうかをメインに表示。(nonceも表示してみる)
 * ステータスの表示に時間を加える。
 * TODO:１万ブロックを超えるブロック数に対応していません。ブロック記述・削除システムを作り直す。
 * TODO : error segment-1ブロックまでしか読み込まれてない！
 * @author yutadd
 */
class TreeMap2<K, V> extends ConcurrentHashMap<K, V> {
	@SuppressWarnings("unchecked")
	public V put(K key, V value) {
		if (key instanceof String && value instanceof String) {
			if (((String) key).contains("E-"))
				key = (K) ((String) key + " " + new SimpleDateFormat("HH時mm分ss秒").format(new Date()));
		}
		if (((String) key).matches(".*E-.*")) {
			System.out.println("[\033[31m" + ((String) key) + "\033[37m]" + ((String) value) + "\033[37m");
			return super.put(key, value);
		} else if (((String) key).matches(".*I-.*")) {
			System.out.println("[\033[34m" + ((String) key) + "\033[37m]" + ((String) value) + "\033[37m");
		} else if (((String) key).matches(".*")) {
			System.out.println("[\033[32m" + ((String) key) + "\033[37m]" + ((String) value) + "\033[37m");
		}
		return value;
		//return super.put(key,value);
	}
}

public class Main {

	static ArrayList<Transaction> savedTransaction = new ArrayList<Transaction>();
	static int ERROR = 0;
	static Map<String, BigDecimal> utxo = new HashMap<String, BigDecimal>();
	static boolean mati = false;
	static String latestHash = null;
	static String name = "XGW";
	static ArrayList<Transaction> pool = new ArrayList<Transaction>();
	static Wallet w;

	static int segmentation=10;
	//manual
	static String man = "";

	/**
	 * this may contains only error message
	 */
	static TreeMap2<String, String> console = new TreeMap2<String, String>();
	static int BANGO = 0;

	static ArrayList<User> u = new ArrayList<User>();
	static boolean mining = false;
	//ネットワークに参加しているIPのリスト
	static ArrayList<String> addressList=new ArrayList<String>();
	static Mining m;
	static int size = 0;
	static BigInteger diff = new BigInteger("26611349253966442813730644663330183884399686815584447189708332380985641",
			10);
	static BigInteger shoki = new BigInteger("26611349253966442813730644663330183884399686815584447189708332380985641",
			10);

	public static void main(String[] args) {

		new DNS();

		//コンソールに出力する際に、色を付けても文字化けしないようにする処理(必要なし？)
		if (System.getProperty("os.name").startsWith("Windows")) {
			/* 
			Function GetStdHandleFunc = Function.getFunction("kernel32", "GetStdHandle");
			DWORD STD_OUTPUT_HANDLE = new DWORD(-11);
			HANDLE hOut = (HANDLE) GetStdHandleFunc.invoke(HANDLE.class, new Object[] { STD_OUTPUT_HANDLE });
			DWORDByReference p_dwMode = new DWORDByReference(new DWORD(0));
			Function GetConsoleModeFunc = Function.getFunction("kernel32", "GetConsoleMode");
			GetConsoleModeFunc.invoke(BOOL.class, new Object[] { hOut, p_dwMode });
			int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;
			DWORD dwMode = p_dwMode.getValue();
			dwMode.setValue(dwMode.intValue() | ENABLE_VIRTUAL_TERMINAL_PROCESSING);
			Function SetConsoleModeFunc = Function.getFunction("kernel32", "SetConsoleMode");
			SetConsoleModeFunc.invoke(BOOL.class, new Object[] { hOut, dwMode });*/
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
		w = new Wallet();
		addManuals();
		readHash();
		mining = true;
		new Mining();
		//65261
		Thread th = new Thread(new Server());
		th.start();
		showStats();
		new ReceiveCommand();


	}

	public static void showStats() {
		System.out.println("\033[32m==========↓Stats↓==========");
		System.out.println("[\033[34mYOUR WALLET BALANCE\033[37m]: " + utxo.get(w.pub[0].toString(16)));
		System.out.println("[\033[34mIS WAITING PROCESS IS DONE\033[37m]: " + mati);
		System.out.println("[\033[34mBLOCK SIZE\033[37m]: " + getBlockSize());
		char hugo = (diff.compareTo(shoki) >= 0 ? '+' : '-');
		System.out.println("[\033[34mdifficulty\033[37m]:" + hugo + diff.subtract(shoki).abs().toString(16));
		//gui_check();
		for (Entry<String, BigDecimal> set : utxo.entrySet()) {
			System.out.printf("[\033[34mADDR\033[37m:\033[34m%s\033[37m]: \033[42m%s\033[49m \r\n", new String(Base64.getEncoder().encode(new BigInteger(set.getKey(),16).toByteArray())),
					set.getValue().toString());
		}
		System.out.println("\033[34mYOUR ADDRESS \033[37m: " + w.encodedSection);
		for (Entry<String, String> ent : console.entrySet()) {
			System.out.println("[\033[31m" + ent.getKey() + "\033[37m]" + ent.getValue() + "\033[37m");
		}
		System.out.println("\033[32m==========↑Stats↑==========\033[37m");
	}

	public static void console_clear() {
		try {
			if (System.getProperty("os.name").contains("Windows")) {
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			}else {
				new ProcessBuilder("/bin/clear").inheritIO().start().waitFor();
			}
		} catch (IOException | InterruptedException ex) {
		}
	}

	/**2ブロック以降はチェックを行う<br>一番最初に呼んで*/
	static void readHash() {
		size=0;
		boolean syuryo = false;
		pool = new ArrayList<Transaction>();
		savedTransaction = new ArrayList<Transaction>();
		utxo.clear();
		diff = shoki;
		for (int a = 1; !syuryo; a++) {
			File file = new File("Blocks" + File.separator + "Block-" + a);
			try {
				BufferedReader bs = new BufferedReader(new FileReader(file));
				if (file.exists()) {
					for (int i = 1; i <= segmentation; i++) {
						String line = bs.readLine();
						if (line != null) {
							
							if((i+((a-1)*segmentation))!=1) {
								System.out.println("[getblock]number going to decode is "+(i+((a-1)*segmentation)));
								System.out.println("[getblock]string going to decode is "+line);
								line=decode64(line);
							}
							size = i + (a - 1) * segmentation;
							Block b = new Block(line, diff, utxo, size< 2);
							latestHash = Mining.hash(b.fullText);
							if (size !=1) {
								if (b.ok) {
									b.give_utxo();
									for (Transaction t : b.ts) {
										t.doTrade();
										savedTransaction.add(t);
									}
									try {
										diff = getMin(diff, b.time,getBlock(b.number-1).time);
									} catch (Exception e) {
										console.put("MAINE-01", "[ブロック]minの計算中にエラーが発生しました");
										System.exit(1);
									}
								} else {
									mining = false;
									console.put("MAINE-02", "Block " + i + " invalid");
									System.exit(1);
								}
							}
						}else {
							console.put("MAIN05", "ブロックを読み切りました");
							syuryo=true;
							bs.close();
							break;
						}
					}
				} else {
					bs.close();
					syuryo=true;
					return;
				}
			} catch (Exception e) {
				syuryo=true;
				return;
			}
		}
	}

	
	/**ジェネシスブロックがあるため、チェックを行わない。*/
	static Entry<BigInteger, HashMap<String, BigDecimal>> readhash(int leng) {
		boolean syuryo = false;
		BigInteger diff = shoki;
		HashMap<String, BigDecimal> result = new HashMap<String, BigDecimal>();
		for (int a = 1; !syuryo; a++) {
			File file = new File("Blocks" + File.separator + "Block-" + a);
			try {
				BufferedReader bs = new BufferedReader(new FileReader(file));
				if (file.exists()) {
					for (int i = 1; i <= segmentation; i++) {
						if (leng >= i + (a - 1) * segmentation) {
							try {
								String line = bs.readLine();
								if(i+((a-1)*segmentation)!=1)line=decode64(line);
								Block b = new Block(line, diff, result, i + ((a - 1) * segmentation) <= 1);
								for (Transaction t : b.ts) {
									BigDecimal bal = checkNullAndGetValue(result, t.input);
									result.put(t.input, bal.subtract(t.sum_minus));
									for (Output o : t.out) {
										result.put(o.address[0].toString(16),
												checkNullAndGetValue(result, o.address[0].toString(16)).add(o.amount));
									}
								}
								BigDecimal m_balance = checkNullAndGetValue(result, b.miner);
								result.put(b.miner, m_balance.add(new BigDecimal(50.0)));
								if (i + (a - 1) * segmentation > 4) {
									diff = getMin(diff,b.time,getBlock(b.number-1).time);
								}
							} catch (Exception e) {
								e.printStackTrace();
								syuryo=true;
								break;
							}
						}else {
							syuryo = true;
							break;
						}
					}
				}
				bs.close();
			} catch (Exception e) {
				syuryo=true;
				break;
			}
		}
		return new SimpleEntry<BigInteger, HashMap<String, BigDecimal>>(diff, result);
	}

	
	static int getBockSizeFrom(int i) {
		return getBlockSize() - i;
	}

	static int getBlockSize() {
		return size;
	}

	static String getHash(int number) {
		File file = new File("Blocks" + File.separator + "Block-" +(((number -1) / segmentation) + 1));
		if (!file.exists()) {
			return null;
		} else {
			String s = null;
			BufferedReader br = null;
			try {
				int count = 1;
				br = new BufferedReader(new FileReader(file));
				while ((s = br.readLine()) != null) {
					if (number % segmentation == count++) {//目的の行まで読む
						br.close();
						if(number>1)s=decode64(s);
						return Mining.hash(s);
					}
				}
				return null;
			} catch (FileNotFoundException e) {
				return null;
			} catch (IOException e) {
				console.put("MAINE-03", "br.readLine()でエラーが起きた.");
				return null;
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//番号とハッシュを更新し、書き込みの関数を呼び出します。
	static void addBlock(String block) {
		Block blo = new Block(block, diff, utxo, true);
		blo.give_utxo();
		for(Transaction t:blo.ts) {
			t.doTrade();
		}
		size= blo.number;
		latestHash=Mining.hash(blo.fullText);
/*		console.put("MAIN04", "このブロックのナンバー: " + size);
		console.put("MAIN05", "セーブされたブロックの数: " + getBlockSize());*/
		saveBlock(block);
	}

	/**GETBLOCKは保存された後のブロックを利用するため、チェックを行わない。
	 * 10以降が取得できない
	 * */
	static Block getBlock(int numb) {
		if (numb > 0) {
			int fileNum= (((numb -1) / segmentation) + 1);
			File file = new File("Blocks" + File.separator + "Block-" + fileNum);
			String s;
			int count =1;
			Block b = null;
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				while ((s = br.readLine()) != null) {
					if (numb%segmentation== count%segmentation) {
						br.close();
						System.out.println("[getblock]number going to decode is "+numb);
						System.out.println("[getblock]string going to decode is "+s);
						if(numb>1)s=decode64(s);
						b = new Block(s, BigInteger.ZERO, utxo, true);
						return b;
					}
					count++;
				}
				Main.console.put("見つからない", "です" + numb);
				br.close();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;

	}

	/**ブロックをファイルに記述することのみを行う*/
	private static void saveBlock(String fullText) {
		Block b = new Block(fullText, BigInteger.ZERO, new HashMap<String, BigDecimal>(), true);
		File file = new File("Blocks" + File.separator + "Block-" + (((b.number-1) / segmentation) + 1));
		try {
			file.createNewFile();
			FileWriter fw = new FileWriter(file, true);
			fullText=encode64(fullText);
			fw.write(fullText + System.getProperty("line.separator"));
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static String getlatesthash() {
		return latestHash;
	}

	/**
	 * このメソッドの呼出後、残額のズレを治すため初期化用readhash()を呼び出してください。
	 * */
	static void delfrom(int from) {
		try {
			int begin=(int)(Math.ceil((from-1)/segmentation))+1;
			int end=(int)(Math.ceil((size-1)/segmentation))+1;
			for(int a=begin;a<end;a++) {
				File file = new File("Blocks" + File.separator + "Block-" + a);
				if(a==begin) {
					BufferedReader bs = new BufferedReader(new FileReader(file));
					ArrayList<String> line = new ArrayList<String>();
					for (int i = 1; i < from; i++) {
						line.add(bs.readLine());
					}
					bs.close();
					FileWriter fw = new FileWriter(file);
					for (String s : line) {
						fw.append(s + System.getProperty("line.separator"));
					}
					fw.close();
				}else {
					file.delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	static BigInteger getMin(BigInteger base,long l1,long l2) {
		BigInteger sa =BigInteger.valueOf(l1-l2);
		if (sa.compareTo(BigInteger.valueOf(25000)) > 0) {//テストのために短めに
			//時間が60秒オーバー：もっとかんたんに：数値にプラス
			BigInteger i = new BigInteger("2910A562CF81F8A20CB31817F4350CA75ECF1CB59BED6E75AB6AEB1F4", 16);
			return base.add(i);
		} else {
			//６０秒以下：もっと難しく:数値にマイナス
			BigInteger i = new BigInteger("2910A562CF81F8A20CB31817F4350CA75ECF1CB59BED6E75AB6AEB1F4", 16);
			return base.subtract(i);
		}
	}

	private static void addManuals() {
		File file = new File("Commands.txt");
		if (!file.exists()) {
			console.put("MAINE-08", "コマンドリストファイルがありません。");
		} else {
			String s = "";
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file, Charset.forName("utf-8")));
				char c = 0;
				while ((c = (char) br.read()) != (char) -1)
					s += c;
				br.close();
				man = s;
			} catch (FileNotFoundException e) {
				console.put("MAINE-09", "コマンドリストファイルがありません。");
			} catch (IOException e) {
				console.put("MAINE-10", "br.readLine()でエラーが起きた.");
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static BigDecimal checkNullAndGetValue(Map<String, BigDecimal> map, String key) {
		BigDecimal bal2 = map.get(key);
		if (bal2 == null)
			bal2 = new BigDecimal(0);
		return bal2;
	}
	public static String encode64(String line) {
		String[] ls=line.split(",");
		ls[1]=new String(Base64.getEncoder().encode(new BigInteger(ls[1],16).toByteArray()));
		line="";
		int f=0;
		for(String s:ls) {
			if(f==0) {
				line+=s;
			}else {
				line+=","+s;
			}
			f++;
		}
		return line;
	}
	public static String decode64(String line) {
		System.out.println("I'll decode "+line);
		String[] ls=line.split(",");
		ls[1]=new BigInteger(Base64.getDecoder().decode(ls[1])).toString(16);
		line="";
		int f=0;
		for(String s:ls) {
			if(f==0) {
				line+=s;
			}else {
				line+=","+s;
			}
			f++;
		}
		return line;
	}
}
