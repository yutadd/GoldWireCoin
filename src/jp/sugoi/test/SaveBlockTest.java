package jp.sugoi.test;

import jp.sugoi.Main;

public class SaveBlockTest extends Main{

	public static void main(String[] args) {
		for(int i=1;i<50;i++) {
			Main.addBlock("1,1,1,"+i+",1");
		}
	}

}
