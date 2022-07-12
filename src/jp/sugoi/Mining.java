package jp.sugoi;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class Mining extends Thread{
	// previous_hash,miner_address,nans,block_number,transaction,transaction,...
	//min:66611349253966442813730644663330183884399686815584447189708332380985641
	/*static String b="212885b2dc656b57bcf2397b0a14c3755365adc7b446879db1e2b1c3aa98f929,"
			+ "8c34d885b883597c17790d7e20def48ee700884eee1f72c0c245557750ee5ca6,"
			+ "nans,"
			+ "1,"//トランザクション引数、区切りは'@'で表す
			+ "8c34d885b883597c17790d7e20def48ee700884eee1f72c0c245557750ee5ca60x0ae9f572f6f96b461c4a229f32fc71a24ea57e4872b21819321bb03d6305a02903@8c34d885b883597c17790d7e20def48ee700884eee1f72c0c245557750ee5ca6@8c34d885b883597c17790d7e20def48ee700884eee1f72c0c245557750ee5ca60x0be9f572f6f96b461c4a229f32fc71a24ea57e4872b21819321bb03d6305a029030x0c49@1.0@625a95956ce07dcc60531515e38feba358304b6bc035e905706513e67fa9392d0x0a5e1fdc6831b3d8c0ef2da2cb5543a994f0cf24adb440a2856637824df0f83ef7";
	 */
	Mining(){
		Thread th=new Thread() {
			@Override
			public void run() {
				String rem="";
				for(String s:Main.console.keySet()) {
					if(s.startsWith("MININGE-00")) {
						rem=s;
					}
				}
				Main.console.remove(rem);
				Main.console.put("MINING","Now, I started mining!");
				Random ran=new Random();
				String before;
				for(;Main.mining;) {
					before=(Main.getlatesthash()+","+Main.w.pub[0].toString(16)+","+ran.nextInt()+","+(Main.getBlockSize()+1)+","+System.currentTimeMillis());
					for(Transaction t:Main.pool) {
						before=before+","+t.transaction_sum;
					}
					BigInteger result=new BigInteger(hash(before),16);
					if(result.compareTo(Main.min)<0) {
						Main.console.put("MINING","mining完了 Hash: "+hash(before));
						Network.shareToNodes("block~"+before);

						/*記述*/
						Main.addBlock(before);
						Main.pool.clear();
					}
				}
				Main.console.remove("MINING");
				Main.console.put("MININGE-00","Now, I stopped mining!");
			}
		};
		th.start();
	}
	public static String hash(String arg) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		byte[] hashInBytes = md.digest(arg.getBytes(StandardCharsets.UTF_8));
		StringBuilder sb = new StringBuilder();
		for (byte b : hashInBytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}
}
