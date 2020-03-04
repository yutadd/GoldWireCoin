package priv.key;
import java.math.BigInteger;
import java.util.Random;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;



/****************************************************************************************
 *											*
 * 	Hier werden die Funktionen und die Laufzeit von Secp256k1 getestet.  		*
 *											*
 *****************************************************************************************/



public class Test
{


	final static int 		testRuns = 10;	// Hier die Anzahl der Testläufe änerden!
	public static byte[] 		rand;
	public static byte[] 		hash;
	public static  byte[]		priv;
	private static BigInteger[]	pub 	= new BigInteger[2];
	private static BigInteger[]	sig 	= new BigInteger[2];




	public static void main(String[] args)
	{
		createOneSig();
		allRandomTest();
	}




	// 署名を作成する
	private static void createOneSig()
	{
		rand 	= hexStringToByteArray	("000aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");				 // Zufall (K)
		hash 	= hexStringToByteArray	("fff88df40bcedbe641ddb16ff0a1842d9c67ea1c3bf63f3e0471baa664531d1a");	 // Hash (M)
		priv 	= hexStringToByteArray	("fff112222233333444445555566666777778888899999aaaaabbbbbcccccdddd");	 // Priv.Key
		pub[0] 	= new BigInteger	("FF409479FF9667B49F4BEDFE3191E6078C15873012E6D05A534EA9DB6FA9736B",16); // Pub.Key x
		pub[1] 	= new BigInteger	("4307E003C4D2E575435414970F8C971758AD64C697B1BB6EA25F9C624CF54422",16); // Pub.Key y

		//署名を検証
		bouncastleSigTest();

		//上に同じ
		maxwell_sigTest();
	}




	/**
	 * 署名作成
	 * @return 出来上がった署名
	 */
	private static boolean bouncastleSigTest()
	{
		//署名を作り、sigに格納する
		sig = Bouncycastle_Secp256k1.sig(hash, priv, rand);
		System.out.println	("\nSignaure erstellt mit Bouncastle");

		//署名のための一時的な乱数
		System.out.println	("Zufall (K) =   "+byteArrayToHexString(rand));

		//取引のハッシュ
		System.out.println	("Hash   (M) =   "+byteArrayToHexString(hash));

		//プライベート鍵
		System.out.println	("Priv.Key   =   "+byteArrayToHexString(priv));

		//公開鍵
		System.out.print	("Pub.Key    =  "); print(pub);

		//出来上がった署名
		System.out.print	("Sig. r , s =  "); print(sig);

		//出樹上がった署名と Bouncycastle を使った検証
		if(Bouncycastle_Secp256k1.verify(hash, sig, pub)) { System.out.println("verify_bouncycastle: true"); }
		else{ System.out.println(">>>>>>>>>>>>>>>>>>>>>    verify_bouncycastle,  Error, signature fails!\n"); return false;}

		//上に同じ
		Secp256k1 o = new Secp256k1();
		if(o.verify(hash, sig, pub)) { System.out.println("verify_Maxwell:      true\n"); return true;}
		else{ System.out.println(">>>>>>>>>>>>>>>>>>>>>    verify_Maxwell,      Error, signature fails!\n"); return false;}
	}




	// Erstellt eine Signature mit Maxwell und prüft sie.
	private static boolean maxwell_sigTest()
	{
		Secp256k1 g = new Secp256k1();
		sig = g.sig(hash, priv, rand);
		System.out.println	("\nSignaure erstellt mit Maxwell");
		System.out.println	("Zufall (K) =   "+byteArrayToHexString(rand));
		System.out.println	("Hash   (M) =   "+byteArrayToHexString(hash));
		System.out.println	("Priv.Key   =   "+byteArrayToHexString(priv));
		System.out.print	("Pub.Key    =  "); print(pub);
		System.out.print	("Sig. r , s =  "); print(sig);
		if(Bouncycastle_Secp256k1.verify(hash, sig, pub)) { System.out.println("verify_bouncycastle: true"); }
		else{ System.out.println(">>>>>>>>>>>>>>>>   verify_bouncycastle,  Error, signature fails!\n"); return false;}

		Secp256k1 o = new Secp256k1();
		if(o.verify(hash, sig, pub)) { System.out.println("verify_Maxwell:      true\n"); return true;}
		else{ System.out.println(">>>>>>>>>>>>>>>>>>>>>    verify_Maxwell,      Error, signature fails!\n"); return false;}
	}




	/**
	 * ランダムテスト
	 */
	private static void allRandomTest()
	{
		priv 	= hexStringToByteArray	("111112222233333444445555566666777778888899999aaaaabbbbbcccccdddd");	 // Priv.Key
		pub[0] 	= new BigInteger	("CAD5E868FC3437CB26423166631D6DA9185991D37252AA6A5C898956BF288AA2",16); // Pub.Key x
		pub[1] 	= new BigInteger	("E5866FE1B7A9E0DB979F48A118B987148E97C50F76F585FAFD2D2A791164C74F",16); // Pub.Key y
		for(int i = 0; i<testRuns; i++)
		{
			Random k = new Random();
			rand = (new BigInteger(256, k)).toByteArray();
			Random h = new Random();
			hash = (new BigInteger(256, h)).toByteArray();
			hash = Secp256k1.to_fixLength(hash,32);
			if(bouncastleSigTest() == false) break;
			if(maxwell_sigTest() == false) break;
		}
		VergleichsTest();
		secp256k1_speed_Test(); // 3,0ms
	}

	/**
	 * どちらがより早く
	 * 秘密鍵と公開鍵
	 * を作成できるかのテスト<br>
	 * ランダムループの結果を公式のSecp256k1関数と比較して正確さを確認します
	 */
	private static void VergleichsTest()
	{
		Secp256k1 g = new Secp256k1();
		BigInteger[] NULL= new BigInteger[2];
		NULL[0] = BigInteger.ZERO;
		NULL[1] = BigInteger.ZERO;
		BigInteger[] pubK1 = new BigInteger[2];
		BigInteger[] pubK2 = new BigInteger[2];
		long msec = 0;
		long t0 = System.currentTimeMillis();
		for(int i=0; i<testRuns; i++){
			t0 = System.currentTimeMillis();
			Random rn = new Random();
			BigInteger   priK = new BigInteger(255,rn);
			System.out.println(priK);
			pubK1 = g.multiply_G(priK);
			String b = pubK1[0].toString(16);
			System.out.printf("\npublic key0: %S\n",b);
			pubK2 =  getPublicKeyOriginal(priK);
			b = pubK2[0].toString(16);
			System.out.printf("Public  Key0: %S\n",b);
			if(pubK1[0].equals(pubK2[0])) System.out.println("correct!");
			else {System.out.println(" >>>>>>>>>>>>>>>>>>正しくない。キーが同じではありません<<<<<<<<<<<<"); break;}
			System.out.printf("Zeit: %d ms\n", msec);
			msec = System.currentTimeMillis()-t0;
		}
	}




	// aktuell 3,22 ms
	private static void secp256k1_speed_Test()
	{
		System.out.println("\nRun Benchmarks . . .");
		Secp256k1 g = new Secp256k1();
		BigInteger[] pubK1 = new BigInteger[2];
		Random rn = new Random();
		BigInteger   priK = new BigInteger(255,rn);
		long t = System.currentTimeMillis();
		for(int i=0; i<testRuns; i++) pubK1 = g.multiply_G(priK);
		t = System.currentTimeMillis()-t;
		print(pubK1);
		System.out.println("\n\nZeit:  "+t*1000/testRuns +"µs");
	}



	/**
	 * 秘密鍵から公開鍵を作る
	 * @param in 秘密鍵
	 * @return 公開鍵(0番目)
	 */
	private static BigInteger[] getPublicKeyOriginal(BigInteger in)
	{
		ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("secp256k1");
		byte[] pubKey = spec.getG().multiply(in).getEncoded(false);
		byte[] a = new byte[32];
		byte[] b = new byte[32];
		System.arraycopy(pubKey, 1, a, 0, 32);
		System.arraycopy(pubKey, 33, b, 0, 32);
		BigInteger[] erg = new BigInteger[2];
		erg[0] = new BigInteger(1,a);
		erg[1] = new BigInteger(1,b);
		return erg;
	}




	// Gibt den Punkt x,y in HEX auf der Konsole aus
	static void print(BigInteger[] P)
	{
		String a = P[0].toString(16);
		String b = P[1].toString(16);
		System.out.printf(" |%S|",a);
		System.out.printf(" |%S| \n",b);
	}




	/**
	 * １６進数の文字列をbyte[]に変換
	 * @param １６進数
	 * @return
	 */
	public static byte[] hexStringToByteArray(String hex)
	{
		if((hex.length()%2)==1)                          // Falls die Länge des Strings ungerade ist, wird eine 1 angehängt
		{
			char c = '1';
			hex += c;
			System.out.println("Fehler in Convert.hexStringToByteArray: Ungerade String-Zeichenfolge!");
		}
		int l = hex.length();
		byte[] data = new byte[l/2];
		for (int i = 0; i < l; i += 2)
		{
			data[i/2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i+1), 16));
		}
		return data;
	}




	// Byte Array wird in einen Hexa String konvertiert
	public static String byteArrayToHexString(byte[] a)
	{
		StringBuilder sb = new StringBuilder(a.length * 2);
		for(byte b: a)
			sb.append(String.format("%02x", b));
		return sb.toString();
	}
}
