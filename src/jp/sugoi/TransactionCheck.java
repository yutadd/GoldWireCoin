package jp.sugoi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

public class TransactionCheck {
	static boolean exec(String[] he_tr,boolean check,Map<String,BigDecimal> temp_utxo,ArrayList<Transaction> ts){
		boolean ok=true;
		try {

			for(int i=5;i<he_tr.length;i++) {
				Transaction t=new Transaction(he_tr[i],temp_utxo);
				if(!t.ok) {
					
					ok=false;
					break;
				}
			}
		}catch(Exception e) {Main.console.put("TransactionCheckE-00", e.getMessage());}
		return ok;
	}
}
