package jp.sugoi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import priv.key.Bouncycastle_Secp256k1;

/**
 * FROM<br>
 * TO_AMOUNT<br>
 * FEE<br>
 * TIMESTAMP<br>
 * SIGN<br>
 * Like this↓<br>
 * 8c34d885b883597c17790d7e20def48ee700884eee1f72c0c245557750ee5ca60x0ae9f572f6f96b461c4a229f32fc71a24ea57e4872b21819321bb03d6305a02903@<br>
 *1a17ac87a41ccf763f3c7b167e54580229be9f570776322c3b44ea6a652aacf80x0b526a25f24b9ff12f57989a765827e3f933a240d5498ee5e8a3ab936aa69a1cd20x0c50.0@<br>
 *10.0@<br>
 *1583043277586@<br>
 *af958f7a1796b1f1841587bebcf6cc05efec9d7483ead206a5fe053f9b5c05080x0a84ccdae0158f7995dfb1f0fb2f9768cfb7c75cd8cb8a62b82af9b27890ebbba0<br>
 * @author student
 * 
 */
public class Transaction {
	@Override
	public boolean equals(Object obj) {
		// TODO 自動生成されたメソッド・スタブ
		if(obj instanceof Transaction) {
			if(((Transaction)obj).transaction_sum==transaction_sum) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 認証に成功した場合、ハッシュ値が格納される。
	 * (sha-256)
	 */
	BigDecimal sum_minus=new BigDecimal(0.0);
	String hash;
	String input;
	String transaction_sum;
	long timestamp;
	String from;
	BigDecimal fee;
	Output[] out;
	boolean ok;
	Transaction(String s,BigDecimal source_balance){

		try {
			//System.out.println("[トランザクション]@有り原文 : "+s);
			from=s.split("@")[0];
			String output=s.split("@")[1];
			BigDecimal fee=new BigDecimal(s.split("@")[2]);
			long time=Long.parseLong(s.split("@")[3]);
			String sign=s.split("@")[4];
			String sum=from+""+output+""+fee+""+time;
			String hash1=hash(sum);
			//System.out.printf("[sign]hash = %s\r\n",sum);
			BigInteger[] sig= toBigInteger(sign,"0x0a",16);
			BigInteger[] pu= toBigInteger(from, "0x0a", 16);
			if(Bouncycastle_Secp256k1.verify(hash1.getBytes(), sig, pu)) {
				Main.console.put("TRANSACTION0","認証完了");
				out=getoutput(output.split("0x0a"));
				this.fee=fee;
				hash=hash1;
				input= from.split("0x0a")[0];
				transaction_sum=from+"@"+output+"@"+fee+"@"+time+"@"+sign;
				if(checkoutput(out,source_balance)) {
					ok=true;
					Main.console.put("TRANSACTION1","トランザクションの認証に成功-fee : "+fee);
				}else {Main.console.put("TRANSACTIONE-2","checkoutputに失敗");}
			}else {Main.console.put("TRANSACTIONE-3",sum+"\r\n"+from+"@"+output+"@"+fee+"@"+time+"@"+sign+"\r\n署名の検証に失敗");}
		}catch(Exception e) {int i=0;for(StackTraceElement ste:e.getStackTrace())Main.console.put("TRANSACTIONE-4-"+i++,ste.toString());e.printStackTrace();}
	}
	public boolean doTrade() {
		Main.utxo.put(input,Main.utxo.get(input).subtract(sum_minus));
		for(Output s:out) {
			BigDecimal d=Main.utxo.get(s.address[0].toString(16));
			if(d==null) {
				//System.out.println(d);
				Main.utxo.put(s.address[0].toString(16),s.amount);
			}else {
				Main.utxo.put(s.address[0].toString(16),(d.add(s.amount)));
			}
		}
		return true;
	}
	Output[] getoutput(String[] adou_adou){
		Output[] out=new Output[adou_adou.length];
		for(int i=0;i<adou_adou.length;i++) {
			out[i]=new Output(adou_adou[i]);
		}
		return out;
	}



	//同じアドレスだからアウトプットが適正に計算されない
	boolean checkoutput(Output[] out,BigDecimal source_balance){
		sum_minus=sum_minus.add(fee);
		for(Output element:out) {
			if(element.address[0].toString(16).equals(from.split("0x0a")[0])) {
				Main.console.put("TRANSACTIONE","]送金先と送金元が同じ");
				return false;
			}
			sum_minus=sum_minus.add(element.amount);
		}
		for(Transaction t:Main.pool) {
			if(t.input.equals(input)) {
				sum_minus=sum_minus.add(t.sum_minus);
			}
		}
		if(sum_minus.compareTo(source_balance)<=0) {
			Main.console.put("TRANSACTION07","残額チェックに成功しました");
			return true;
		}
		return false;
	}


	public static  BigInteger[] toBigInteger(String s,String split,int redix) {
		BigInteger[] b= {new BigInteger(s.split(split)[0],redix),new BigInteger(s.split(split)[1],redix)};
		return b;
	}
	public static String hash(String arg) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		byte[] hashInBytes = md.digest(arg.getBytes(StandardCharsets.UTF_8));
		StringBuilder sb = new StringBuilder();
		for (byte b : hashInBytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}
}
