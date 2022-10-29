package jp.sugoi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public  class ReceiveBlocks {
	public static void exec(String line) {
		try {
			
			System.out.println("ブロックス！："+line);
			String st=line.split("~")[1];
			String[] args=st.split("0x0f");
			Block[] blocks=new Block[args.length];
			int i=0;
			boolean ok=true;
			if(args[0]==null) {return;}
			Block bl=new Block(args[0],BigInteger.ZERO,new HashMap<String,BigDecimal>(),true);
			Entry<BigInteger,HashMap<String,BigDecimal>> kiso=Main.readhash(bl.number-1);
			BigInteger kiso_diff=kiso.getKey();
			Map<String,BigDecimal> kiso_utxo=kiso.getValue();
			for(String s:args) {
				Block b=new Block(s,kiso_diff,kiso_utxo,false);
				System.out.println("diff : "+b.diff.toString(16));
				if(b.ok) {
					for(Transaction t:b.ts) {
						if(t.ok) {
							kiso_utxo.put(t.input, kiso_utxo.get(t.input).subtract(t.sum_minus));
							for(Output o:t.out) {
								String addr=o.address[0].toString(16);
								kiso_utxo.put(addr,Main.checkNullAndGetValue(kiso_utxo, addr).add(o.amount));
							}
						}else {
							Main.console.put("RECEIVEBLOCKSE-Trans","Transaction check failed");
							ok=false;
							break;
						}
					}
					blocks[i]=b;
					//iは受け取ったブロックのインデックスを表すため、0だったら、ローカルのブロックからtimeを持ってくる。
					kiso_diff=Main.getMin(kiso_diff,b.time,(i==0)?Main.getBlock(b.number-1).time:blocks[i-1].time);
				}else {
					Main.console.put("RECEIVEBLOCKSE-INV","BLOCK "+i+"INVALID");
					ok=false;
					break;
				}
				i++;
			}
			if(ok) {
				if(!Main.mati) {
					Main.mati=true;
					if(blocks[0].previousHash.equals(Mining.hash( Main.getBlock(blocks[0].number-1).fullText))) {
						if(blocks[blocks.length-1].number>Main.getBlockSize()) {
							Main.mati=false;
							Main.delfrom(blocks[0].number);
							Main.console.put("RECEIVEBLOCKS-SAVE","書き込み開始");
							for(Block b:blocks) {
								Main.console.put("ReceiveBlocks", b.number+"");
								Main.addBlock(b.fullText);
							}
							Main.readHash();
							Main.mati=false;
							
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
		}catch(Exception e) {e.printStackTrace();}
		Main.mati=false;
	}
	
}
