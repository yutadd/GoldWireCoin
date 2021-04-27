package jp.sugoi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

import priv.key.Bouncycastle_Secp256k1;
import priv.key.Secp256k1;

public class Wallet {
	public   byte[]		priv;
	public BigInteger[]	pub 	= new BigInteger[2];
	public String address_0x0a;
	public Wallet() {
		File wallet=new File("wallet.enc");
		if(wallet.exists()) {
			try {
				BufferedReader fr=new BufferedReader(new FileReader(wallet));
				try {
					BigInteger b=new BigInteger(fr.readLine(),16);
					priv=b.toByteArray();
					Secp256k1 g = new Secp256k1();
					pub = g.multiply_G(b);
				} catch (Exception e) {System.out.println("walletファイルに異常があります。\n確認してください。");
				}finally {try {fr.close();System.out.println("Close_File");} catch (IOException e) {System.out.println("Already Closed?");}
				}
			} catch (FileNotFoundException e) {
				System.out.println("ウォレットの存在を確認できません。");
			}
			//読み込み処理
		}else {
			Secp256k1 g = new Secp256k1();
			BigInteger[] pubK1 = new BigInteger[2];
			Random rn = new Random();
			BigInteger   priK = new BigInteger(255,rn);
			pubK1 = g.multiply_G(priK);
			priv=priK.toByteArray();
			pub=pubK1;
			try {
				if(wallet.createNewFile()) {
					FileWriter fw=new FileWriter(wallet);
					fw.write(new BigInteger(priv).toString(16));
					fw.write("\r\n"+"このファイルの内容は秘密鍵です。他人に見せたりしないでください※お金が盗まれてしまいます。");
					fw.flush();
					fw.close();
				}
			} catch (IOException e) {
				System.out.println("ウォレットの作成に失敗しました。");
			}
		}
		address_0x0a=pub[0]+"0x0a"+pub[1];
	}


	BigInteger[] sign(byte[] hash){
		//署名用の乱数
		Random rn = new Random();
		byte[]  ran = new BigInteger(255,rn).toByteArray();
		BigInteger[] sig = Bouncycastle_Secp256k1.sig(hash, priv, ran);
		return sig;
	}







	public static void main(String[] args) {
		Wallet w=new Wallet();
		Main.readHash();
		long time=System.currentTimeMillis();
		System.out.println(time);
		System.out.println("秘密鍵："+new BigInteger(w.priv).toString(16));
		System.out.println("公開鍵："+w.pub[0].toString(16));
		BigInteger[] sign_=w.sign(Transaction.hash("aa").getBytes());
		byte[] b=(Transaction.hash(w.pub[0].toString(16)+"0x0a"+w.pub[1].toString(16)+w.pub[0].toString(16)+"0x0b"+w.pub[1].toString(16)+"0x0c"+49+""+1.0+""+time).getBytes());
		//                                                                              from                                              input                            output
		System.out.println("BYTE : "+w.pub[0].toString(16)+"0x0a"+w.pub[1].toString(16)+w.pub[0].toString(16)+w.pub[0].toString(16)+"0x0b"+w.pub[1].toString(16)+"0x0c"+49+""+1.0+""+time);
		BigInteger[] bi=w.sign(b);
		Transaction t=new Transaction(w.pub[0].toString(16)+"0x0a"+w.pub[1].toString(16)   +"@"+     w.pub[0].toString(16)+"0x0b"+w.pub[1].toString(16)+"0x0c"+49   +"@"+   1.0   +"@"+  time +"@"+bi[0].toString(16)+"0x0a"+bi[1].toString(16),new BigDecimal(0));
		System.out.println(t.hash);
		System.out.println("sig："+bi[0].toString(16)+"+"+bi[1].toString(16));
		System.out.println("pu："+w.pub[0].toString(16)+"0x0a"+w.pub[1].toString(16));
		System.out.println("outputs："+w.pub[0].toString(16)+"0x0b"+1);
		System.out.println("偽鍵で認証："+Bouncycastle_Secp256k1.verify(b,bi, w.pub));
		/*偽造・テスト*/
		System.out.println("認証結果："+Bouncycastle_Secp256k1.verify(Transaction.hash("aa").getBytes(), sign_, w.pub));
	}
}
