package jp.sugoi;

import java.net.Socket;

public class SendBlocks {
	public static void exec(String line,Socket s) {
		try {
			String hash=line.split("~")[1];
			int block_number=Main.getNumber(hash);
			if(!(block_number<0)) {//                                                 3
				Block[] list=new Block[(Main.getBlockSize()+1)-block_number];
				int shoki=block_number;
				boolean ok=true;
				for(;block_number<=Main.getBlockSize();block_number++) {
					Block b=Main.getBlock(block_number);
					if(b==null) {System.out.println("[ユーザー]ブロックを読み込めず。");ok=false;break;}
					if(b.ok) {
						//4?                     4
						list[block_number-shoki]=b;
					}else {
						ok=false;
						break;
					}
				}
				System.out.println("["+Main.getfrom+"][block]ok? : " + ok);
				if(ok) {
					int i=0;
					StringBuilder sb=new StringBuilder();
					for(Block b:list) {
						if(i==0) {
							sb.append(b.sum);
						}else {
							sb.append("0x0f"+b.sum);
						}
						i++;
					}
					s.getOutputStream().write(("blocks~"+sb.toString()+"\r\n").getBytes());
					System.out.println("["+Main.getfrom+"][ユーザー]お繰り返した");
				}
			}else {
				s.getOutputStream().write("notfound\r\n".getBytes());
				System.out.println("["+Main.getfrom+"][ユーザー]hashが見つかりませんでした.");
			}
		}catch(Exception e) {e.printStackTrace();}
	}
}