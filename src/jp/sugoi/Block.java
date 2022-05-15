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
	public Block(String string,boolean check,BigInteger diff,Map<String,BigDecimal> utxo,boolean PassTransactionCheck) {
		try {
			if(!check) {System.out.println("\t\t=====[ブロック]=======\r\n"+string.replace(",", "\r\n")+"\r\n\t\t=======[ブロック]=====\r\n");}
			sum=string;
			String[] he_tr=string.split(",");
			boolean transaction_ok=TransactionCheck.exec(he_tr,check,utxo,ts);
			miner=he_tr[2];
			number=Integer.parseInt(he_tr[3]);
			time=Long.parseLong(he_tr[4]);
			previous_hash=he_tr[0];
			if(PassTransactionCheck||transaction_ok) {
				if(!check) {System.out.println("[ブロック]トランザクションの認証に成功");}
				BigInteger result=new BigInteger(Mining.hash(sum),16);
				if(!check||diff==null||result.compareTo(diff)==-1) {
					if(!check) {System.out.println("[ブロック]ブロックの認証成功");}
					this.he_tr=he_tr;
					ok=true;
					this.diff=diff;
				}else {
					if(!check)System.out.println("このブロック : "+result.toString(10)+"\r\n"+"現在の難易度 : "+Main.shoki.toString(10));
					if(!check) {System.out.println("[ブロック]認証失敗");}
				}
			}else {
				if(!check) {System.out.println("[ブロック]transactioncheck失敗");}
			}
		}catch(Exception e) {
			System.out.println("[ブロック]error :"+string);
			e.printStackTrace();
		}
	}
	boolean give_utxo(boolean check){
		if(he_tr==null) {
			System.out.print("=====↓エラー発生↓=====\r\nhe_tr : ");
			System.out.println("全配列: "+he_tr);
			BigInteger result=new BigInteger(Mining.hash(sum),16);
			System.out.println("原文: "+sum);
			if(!check)System.out.println("難易度比較:\r\nこのブロック→\t"+result.toString(10)+"\r\n現在の難易度→\t"+Main.shoki.toString(10));
			System.out.print("=====↑エラー発生↑=====\r\nhe_tr : ");
			throw new NullPointerException();
		}
		if(!check)System.out.println("[ブロック]採掘者："+he_tr[1]);
		BigDecimal d=(Main.utxo.get(he_tr[1].split("0x0a")[0])!=null)? Main.utxo.get(he_tr[1].split("0x0a")[0]):new BigDecimal(0.0);
		for(Transaction t:this.ts) {
			d=d.add(t.fee);
		}
		Main.utxo.put(he_tr[1].split("0x0a")[0], d.add(new BigDecimal(50.0)));
		if(!check) {System.out.println("[ブロック]give_utxo : OK");}
		if(!check) {System.out.println("[ブロック]マイナーの残額 : "+Main.utxo.get(he_tr[1].split("0x0a")[0]));}
		return true;
	}
	boolean remove_utxo(boolean check){
		BigDecimal d=(Main.utxo.get(he_tr[1].split("0x0a")[0])!=null)? Main.utxo.get(he_tr[1].split("0x0a")[0]):new BigDecimal(0.0);
		for(Transaction t:this.ts) {
			d=d.add(t.fee);
		}
		Main.utxo.put(he_tr[1].split("0x0a")[0], d.add(new BigDecimal(50.0)));
		if(!check) {System.out.println("[ブロック] remove_utxo : OK");}
		if(!check) {System.out.println("[ブロック]マイナーの残額 : "+Main.utxo.get(he_tr[1].split("0x0a")[0]));}
		return true;
	}

	
	
}
