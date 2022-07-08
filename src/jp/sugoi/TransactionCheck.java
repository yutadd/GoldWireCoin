package jp.sugoi;

import java.math.BigDecimal;
import java.util.Map;

public class TransactionCheck {
	static boolean exec(String[] he_tr,boolean check,Map<String,BigDecimal> utxo){
		boolean ok=true;
		try {

			for(int i=5;i<he_tr.length;i++) {
				BigDecimal source_balance=utxo.get(he_tr[i].split("@")[0]);
				Transaction t=new Transaction(he_tr[i],source_balance);
				if(!t.ok) {
					
					ok=false;
					break;
				}
			}
		}catch(Exception e) {Main.console.put("TransactionCheckE-00", e.getMessage());}
		return ok;
	}
}
