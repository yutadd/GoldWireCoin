package jp.sugoi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class SendBlocks {
	public static void exec(String line,Socket s) {
		try {
			String[] args=line.split("~");
			Block receivedBlock=new Block(args[1],BigInteger.ZERO,new HashMap<String,BigDecimal>(),true);
			int receivedNumber=receivedBlock.number;
			String localHash=Main.getHash(receivedNumber);
			Main.console.put("[SENDBLOCK-RECE]",String.valueOf(receivedNumber));
			if(localHash!=null) {
				if(localHash.equals(Mining.hash(receivedBlock.fullText))) {
					//一致したブロック以降のブロックを送信する
					receivedNumber+=1;
					ArrayList<Block> list=new ArrayList<Block>();
					boolean ok=true;
					//
					for(;receivedNumber<=Main.getBlockSize();receivedNumber++) {
						Block b=Main.getBlock(receivedNumber);
						if(b!=null) {
							list.add(b);
						}else {
							Main.console.put("[SENDBLOCKE-NUL]","b is null");
							ok=false;
							break;
						}
					}
					if(ok) {
						Main.console.put("[SENDBLOCK-READY]","Created blocks is okay ready to send");
						int i=0;
						StringBuilder sb=new StringBuilder();
						for(Block b:list) {
							if(i==0) {
								sb.append(b.fullText);
							}else {
								sb.append("0x0f"+b.fullText);
							}
							i++;
						}
						s.getOutputStream().write(("blocks~"+sb.toString()+"\r\n").getBytes());
						Main.console.put("[SENDBLOCK-RET]","Returned blocks");
					}
				}else{
					//receivenumberはifブロック内で増えるので、-1でいい。
					Block b=Main.getBlock(receivedNumber-1);
					if(b!=null) {
						s.getOutputStream().write(("isSame~"+(b.fullText)+"\r\n").getBytes());
						Main.console.put("[SENDBLOCK-ISAME]",String.valueOf(receivedNumber-1));
					}else {
						Main.console.put("[SENDBLOCK-NSC]","Not Same Chain");
					}
				}
			}else {
				Main.console.put("MainE-","受け取ったブロックから番号を推測しました（"+receivedNumber+"）が、ローカルにその番号のブロックが見つかりませんでした。");
			}
		}catch(Exception e) {e.printStackTrace();}
	}
}