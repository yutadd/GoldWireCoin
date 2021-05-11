package jp.sugoi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Block {

	ArrayList<Transaction> ts=new ArrayList<>();
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
	public Block(String string,boolean check,BigInteger diff) {
		try {
			if(!check) {System.out.println("\t\t=====[ブロック]=======\r\n"+string.replace(",", "\r\n")+"\r\n\t\t=======[ブロック]=====\r\n");}
			sum=string;
			String[] he_tr=string.split(",");
			boolean transaction_ok=transcheck(he_tr,check);
			miner=he_tr[2];
			number=Integer.parseInt(he_tr[3]);
			time=Long.parseLong(he_tr[4]);
			previous_hash=he_tr[0];
			if(transaction_ok) {
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
			System.out.println("error"+string);
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
		if(!check)System.out.println("[ブロック]採掘者："+he_tr[0]);
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
	boolean transcheck(String[] he_tr,boolean check){
		boolean ok=true;
		try {
			HashMap<String,BigDecimal> list =new HashMap<>();
			for(int i=5;i<he_tr.length;i++) {
				if(!check) {System.out.println("=======トランザクションのチェック======<<"+(i-4)+"/"+(he_tr.length-5)+">>======トランザクションのチェック=======");}
				if(!check)System.out.println("原文"+i+": "+he_tr[i]);
				BigDecimal d=(list.get(he_tr[i].split("@")[0].split("0x0a")[0])==null)?new BigDecimal(0.0):list.get(he_tr[i].split("@")[0].split("0x0a")[0]);
				Transaction t=new Transaction(he_tr[i],d);
				if(!t.ok) {
					if(!check) {System.out.println("[ブロック]トランザクション"+i+"の認証に失敗");}
					ok=false;
					break;
				}else {
					BigDecimal amount=new BigDecimal(0.0);
					if(list.containsKey(t.input)) {
						amount=amount.add(t.amount);
					}else {
						amount=t.amount;
					}
					list.remove(t.input);
					list.put(t.input,amount);
					ts.add(t);
				}
			}
		}catch(Exception e) {System.out.println("[ブロック]トランザクションの認証に失敗");e.printStackTrace();check=false;}
		return ok;
	}

}
