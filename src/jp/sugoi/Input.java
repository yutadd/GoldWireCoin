package jp.sugoi;

import java.math.BigDecimal;

public class Input {
	BigDecimal 総インプット=new BigDecimal(0);
	public Input(String inputs) {
		if(Main.utxo.containsKey(inputs)){
			総インプット=総インプット.add(Main.utxo.get(inputs));
		}
	}

}
