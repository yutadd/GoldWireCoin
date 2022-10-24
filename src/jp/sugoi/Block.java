package jp.sugoi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;

public class Block {

	ArrayList<Transaction> ts=new ArrayList<Transaction>();
	public String fullText="";
	public String  previousHash;
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
			fullText=string;
			String[] he_tr=string.split(",");
			miner=he_tr[1];
			number=Integer.parseInt(he_tr[3]);
			time=Long.parseLong(he_tr[4]);
			for(int i=5;i<he_tr.length;i++) {
				ts.add(new Transaction(he_tr[i],Main.checkNullAndGetValue(utxo,he_tr[i].split("@")[0].split("0x0a")[0]),PassCheck));
			}
			this.diff=diff;
			this.he_tr=he_tr;
			previousHash=he_tr[0];
			BigInteger result=new BigInteger(Mining.hash(fullText),16);
			if(!PassCheck) {
				boolean ts_check=true;
				for(Transaction t:ts) {
					if(!t.ok)ts_check=false;
				}
				if(ts_check) {
					//トランザクションの認証成功
					if(diff!=null) {
						if(result.compareTo(diff)<=0) {
							//難易度の認証成功
							ok=true;
						}else {
							Main.console.put("BLOCK09E-DIFF","Block difficulty wrong.\r\nこのブロック : "+result.toString(16)+"\r\n"+"現在の難易度 : "+diff.toString(16));
						}
					}else {
						Main.console.put("BLOCKE-DIFNUL","DIFFがnullです");
					}
				}else {
					Main.console.put("BLOCKE-TRANS","transactioncheck失敗");
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	boolean give_utxo(){
		if(he_tr==null) {//nullのブロック(unbelievable)
			throw new NullPointerException();
		}

		BigDecimal d=(Main.utxo.get(he_tr[1].split("0x0a")[0])!=null)? Main.utxo.get(he_tr[1].split("0x0a")[0]):new BigDecimal(0.0);
		for(Transaction t:this.ts) {
			d=d.add(t.fee);
		}
		Main.utxo.put(he_tr[1].split("0x0a")[0], d.add(new BigDecimal(50.0)));
		return true;
	}
	boolean remove_utxo(boolean check){
		BigDecimal d=(Main.utxo.get(he_tr[1].split("0x0a")[0])!=null)? Main.utxo.get(he_tr[1].split("0x0a")[0]):new BigDecimal(0.0);
		for(Transaction t:this.ts) {
			d=d.add(t.fee);
		}
		Main.utxo.put(he_tr[1].split("0x0a")[0], d.add(new BigDecimal(50.0)));
		return true;
	}



}
