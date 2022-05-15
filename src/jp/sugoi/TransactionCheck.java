package jp.sugoi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

public class TransactionCheck {
	static boolean exec(String[] he_tr,boolean check,Map<String,BigDecimal> temp_utxo,ArrayList<Transaction> ts){
		boolean ok=true;
		try {

			for(int i=5;i<he_tr.length;i++) {
				if(!check) {System.out.println("=======トランザクションのチェック======<<"+(i-4)+"/"+(he_tr.length-5)+">>======トランザクションのチェック=======");}
				if(!check)System.out.println("原文"+i+": "+he_tr[i]);
				Transaction t=new Transaction(he_tr[i],temp_utxo);
				if(!t.ok) {
					if(!check) {System.out.println("[ブロック]トランザクション"+i+"の認証に失敗");}
					ok=false;
					break;
				}
			}
		}catch(Exception e) {System.out.println("[ブロック]トランザクションの認証に失敗");e.printStackTrace();check=false;}
		return ok;
	}
}
