package jp.sugoi;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Output {
	BigDecimal amount;
	BigInteger[] address;
	public Output(String s) {
		//TO 0x0b TO 0x0c 2
		String[] ad_adamount=s.split("0x0c");
		address=Transaction.toBigInteger(ad_adamount[0],"0x0b", 16);
		amount=new BigDecimal(ad_adamount[1]);
	}
}