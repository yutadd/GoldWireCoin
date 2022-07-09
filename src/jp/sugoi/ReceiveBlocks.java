package jp.sugoi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class ReceiveBlocks {
	public static void exec(String line) {
		System.out.println("ブロックス！："+line);

		String st=line.split("~")[1];
		String[] args=st.split("0x0f");
		Block[] blocks=new Block[args.length];
		int i=0;
		boolean ok=true;
		if(args[0]==null) {return;}
		Block bl=new Block(args[0],BigInteger.ZERO,new HashMap<String,BigDecimal>(),true);
		Map<String,BigDecimal> kiso_utxo=Main.readhash(bl.number-1);
		BigInteger kiso_diff=Main.getBlock(bl.number-1).diff;
		for(String s:args) {
			Block b=new Block(s,kiso_diff,kiso_utxo,false);
			if(b.ok) {
				for(Transaction t:b.ts) {
					if(t.ok) {
						kiso_utxo.put(t.from, kiso_utxo.get(t.from).subtract(t.sum_minus));
						for(Output o:t.out) {
							String addr=o.address[0].toString(16);
							kiso_utxo.put(addr,kiso_utxo.get(addr).add(o.amount));
						}
					}else {
						ok=false;
						break;
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
			if(!Main.mati) {
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
					Main.console.put("RECEIVEBLOCKSE-CON","処理中断：受け取ったブロックが自分の持っているブロックとつながらない。");
				}
			}else {
				Main.console.put("RECEIVEBLOCKSE-BLOCKED","ほかの処理が実行中だったため、中断");
			}
		}else {
			Main.console.put("RECEIVEBLOCKSE-ABORD","処理中断");
		}
	}
}
