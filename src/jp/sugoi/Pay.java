package jp.sugoi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Base64.Decoder;

public class Pay {
	//  pay 73217360x0a183718946238746 125.5
	public  Pay(String str) {
		String spiliter="0x0a";
		String at="@";
		String[] args=str.split(" ");
		if(args.length>=3) {
			if(args[1]!=null&&args[1].contains("§")) {
				String[] ls=args[1].split("§");
				Decoder dec=Base64.getDecoder();
				args[1]=new BigInteger(dec.decode(ls[0])).toString(16)+spiliter+new BigInteger(dec.decode(ls[1]));
				if(args[2]!=null) {
					String sourceaddr=Main.w.address_0x0a.split(spiliter)[0];
					if(Main.utxo.containsKey(sourceaddr)) {
						if(Main.utxo.get(sourceaddr).compareTo(BigDecimal.valueOf(Double.parseDouble(args[2])))==1) {
							String sum=Main.w.address_0x0a+at+args[1].replace(spiliter, "0x0b")+"0x0c"+args[2]+at+"0.1"+at+System.currentTimeMillis();
							String hash=Transaction.hash(sum.replace(at, ""));
							BigInteger[] sign0=Main.w.sign(hash.getBytes());
							String sign=sign0[0].toString(16)+spiliter+sign0[1].toString(16);
							Transaction tr=new Transaction(sum+at+sign, Main.checkNullAndGetValue(Main.utxo,sourceaddr),false);
							System.out.println(sum+at+sign);
							System.out.println("OK?:"+tr.ok);
							if(tr.ok) {
								Main.pool.add(tr);
								Network.shareToNodes("trans~"+ tr.transaction_sum);
							}
						}else {
							System.out.println("[ペイ]残額が足りません");
						}
					}else {
						System.out.println("[ペイ]残額がありません。");
					}
				}else {
					System.out.println("[ペイ]送金額をつけてください。");
				}

			}else {
				System.out.println("[ペイ]使えるアドレスではありません");
			}
		}else {
			System.out.println("[ペイ]引数がたりません。");
		}
	}
}
