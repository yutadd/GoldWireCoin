package jp.sugoi.test;

import jp.sugoi.Main;

public class SaveBlockTest extends Main{

	public static void main(String[] args) {
		
		Main.delfrom(2);
		for(int i=2;i<50;i++) {
			Main.addBlock("1,1,1,"+i+",1");
		}
		Main.delfrom(10);
	}

}
