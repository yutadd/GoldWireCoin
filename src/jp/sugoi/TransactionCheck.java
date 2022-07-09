package jp.sugoi;

public class TransactionCheck {
	/*static boolean exec(String[] he_tr,Map<String,BigDecimal> utxo){
		boolean ok=true;
		try {
			for(int i=5;i<he_tr.length;i++) {
				Transaction t=new Transaction(he_tr[i],Main.checkNullAndGetValue(utxo, he_tr[i].split("@")[0].split("0x0a")[0]),false);
				if(!t.ok) {
					ok=false;
					break;
				}
			}
		}catch(Exception e) {Main.console.put("TransactionCheckE-00", e.getMessage());}
		return ok;
	}*/
}
