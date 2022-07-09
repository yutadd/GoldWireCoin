package jp.sugoi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;

public class Block {

	ArrayList<Transaction> ts=new ArrayList<Transaction>();
	public String sum="";
	public String  previous_hash;
	public int number=0;
	boolean ok=false;
	String miner;
	String[] he_tr;
	long time;
	BigInteger diff;

	//https://kasobu.com/blockchain-mining/#i
	// previous_hash,miner_address,nans,block_number,time,transaction,transaction,...
	public Block(String string,BigInteger diff,Map<String,BigDecimal> utxo,boolean PassCheck) {
		try {
			String[] args=string.split(",");
			Main.console.put("BLOCK00","\t\t============");
			Main.console.put("BLOCK01", args[0]);
			Main.console.put("BLOCK02", args[1]);
			Main.console.put("BLOCK03", args[2]);
			Main.console.put("BLOCK04", args[3]);
			Main.console.put("BLOCK05", args[4]);
			Main.console.put("BLOCK06","\t\t============");
			sum=string;
			String[] he_tr=string.split(",");
			miner=he_tr[2];
			number=Integer.parseInt(he_tr[3]);
			time=Long.parseLong(he_tr[4]);
			for(int i=5;i<he_tr.length;i++) {
				Main.console.put("BlockE-ERR", he_tr[i]);
				ts.add(new Transaction(he_tr[i],Main.checkNullAndGetValue(utxo,he_tr[i].split("@")[0].split("0x0a")[0]),PassCheck));
			}
			this.diff=diff;
			this.he_tr=he_tr;
			previous_hash=he_tr[0];
			BigInteger result=new BigInteger(Mining.hash(sum),16);
			if(!PassCheck) {
				boolean ts_check=true;
				for(Transaction t:ts) {
					if(!t.ok)ts_check=false;
				}
				if(ts_check) {
					Main.console.put("BLOCK07","トランザクションの認証に成功");
					if(diff!=null) {
						if(result.compareTo(diff)<=0) {
							Main.console.put("BLOCK08","ブロックの認証成功");
							ok=true;
						}else {
							Main.console.put("BLOCK09E-DIFF","このブロック : "+result.toString(10)+"\r\n"+"現在の難易度 : "+diff.toString(10));
							Main.console.put("BLOCK10E-DIFF2","認証失敗");
						}
					}else {
						Main.console.put("BLOCKE-DIFNUL","DIFFがnullです");
					}
				}else {
					Main.console.put("BLOCKE-TRANS","transactioncheck失敗");
				}
			}
		}catch(Exception e) {
			Main.console.put("BLOCKE-ERROR",e.getMessage());
		}
	}
	boolean give_utxo(boolean check){
		if(he_tr==null) {
			//System.out.print("=====↓エラー発生↓=====\r\nhe_tr : ");
			Main.console.put("BLOCK13-E","全配列: "+he_tr);
			//BigInteger result=new BigInteger(Mining.hash(sum),16);
			Main.console.put("BLOCK14-E","原文: "+sum);
			//if(!check)System.out.println("難易度比較:\r\nこのブロック→\t"+result.toString(10)+"\r\n現在の難易度→\t"+Main.shoki.toString(10));
			//System.out.print("=====↑エラー発生↑=====\r\nhe_tr : ");
			throw new NullPointerException();
		}
		Main.console.put("BLOCK15","採掘者："+he_tr[1]);
		BigDecimal d=(Main.utxo.get(he_tr[1].split("0x0a")[0])!=null)? Main.utxo.get(he_tr[1].split("0x0a")[0]):new BigDecimal(0.0);
		for(Transaction t:this.ts) {
			d=d.add(t.fee);
		}
		Main.utxo.put(he_tr[1].split("0x0a")[0], d.add(new BigDecimal(50.0)));
		Main.console.put("BLOCK16","give_utxo : OK");
		Main.console.put("BLOCK17","マイナーの残額 : "+Main.utxo.get(he_tr[1].split("0x0a")[0]));
		return true;
	}
	boolean remove_utxo(boolean check){
		BigDecimal d=(Main.utxo.get(he_tr[1].split("0x0a")[0])!=null)? Main.utxo.get(he_tr[1].split("0x0a")[0]):new BigDecimal(0.0);
		for(Transaction t:this.ts) {
			d=d.add(t.fee);
		}
		Main.utxo.put(he_tr[1].split("0x0a")[0], d.add(new BigDecimal(50.0)));
		Main.console.put("BLOCK17"," remove_utxo : OK");
		Main.console.put("BLOCK18","マイナーの残額 : "+Main.utxo.get(he_tr[1].split("0x0a")[0]));
		return true;
	}



}
