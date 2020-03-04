package jp.sugoi;

public class Stats {
	String[][] stats= new String[10][10];
	public Stats(String[][] args) {
		stats=args;
	}
	public Stats() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	void add(String key,String value) {
		stats[stats.length+1][0]=key;
		stats[stats.length+1][1]=value;
	}

}
