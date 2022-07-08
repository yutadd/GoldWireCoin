package jp.sugoi;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Pay {
	//  pay 73217360x0a183718946238746 125.5
	public  Pay(String str) {
		String sum="";
		String[] args=str.split(" ");
		if(args.length>=3) {
			if(args[1]!=null&&args[1].contains("0x0a")) {
				if(args[2]!=null) {
					String sourceaddr=Main.w.address_0x0a.split("0x0a")[0];
					if(Main.utxo.containsKey(sourceaddr)) {
						if(Main.utxo.get(sourceaddr).compareTo(BigDecimal.valueOf(Double.parseDouble(args[2])))==1) {
							sum+=sourceaddr+"@"+args[1].replace("0x0a", "0x0b")+"0x0c"+args[2]+"@"+"0.1"+"@"+System.currentTimeMillis();
							String hash=Transaction.hash(sum.replace("@", ""));
							BigInteger[] sign0=Main.w.sign(hash.getBytes());
							String sign=sign0[0].toString(16)+"0x0a"+sign0[1].toString(16);
							Transaction tr=new Transaction(sum+"@"+sign, Main.utxo.get(sourceaddr));
							System.out.println("OK?:"+tr.ok);
							Main.pool.add(tr);
							for(User u:Main.u) {
								Network.share("trans~", tr.transaction_sum,u.s);
							}
							
						}else {
							System.out.println("[ペイ]残額が足りまへん");
						}
					}else {
						System.out.println("残額がありゃせん。");
					}
				}else {
					System.out.println("ちゃんと送金額つけてやり直せ。");
				}

			}else {
				System.out.println("[ペイ]使えるアドレスば貼っつけてくれ");
			}
		}else {
			System.out.println("[ペイ]引数が足らん。");
		}
	}
}
