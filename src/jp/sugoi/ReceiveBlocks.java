package jp.sugoi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Map.Entry;

public class ReceiveBlocks {
	public static void exec(String line) {
		System.out.println("ブロックス！："+line);
		if(!Main.mati) {
			String st=line.split("~")[1];
			String[] args=st.split("0x0f");
			Block[] blocks=new Block[args.length];
			int i=0;
			boolean ok=true;
			if(args[0]==null) {return;}
			Map<String,BigDecimal> temp_utxo=Main.utxo;
			
			Block bl=new Block(args[0],true,null,Main.utxo,true);
			if(bl.ok) {
				BigInteger temp_diff=Main.getBlock(bl.number-1).diff;
				for(Transaction t:bl.ts) {
					if(t.ok) {
						temp_utxo.put(t.from.split("0x0a")[0], temp_utxo.get(t.from.split("0x0a")[0]).subtract(t.amount.add(t.fee)));
						for(Entry<String,BigDecimal> add:t.Address_Amount.entrySet()) {
							temp_utxo.put(add.getKey(),temp_utxo.get(add.getKey()).add(add.getValue()));
						}
					}
				}
				for(String s:args) {
					Block b=new Block(s,false,temp_diff,temp_utxo,true);
					if(b.ok) {
						for(Transaction t:b.ts) {
							if(t.ok) {
								temp_utxo.put(t.from.split("0x0a")[0], temp_utxo.get(t.from.split("0x0a")[0]).subtract(t.amount.add(t.fee)));
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
	}
}
