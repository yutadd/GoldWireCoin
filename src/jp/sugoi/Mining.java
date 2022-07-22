package jp.sugoi;

import static jcuda.driver.JCudaDriver.*;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.stream.IntStream;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.CUcontext;
import jcuda.driver.CUdevice;
import jcuda.driver.CUdeviceptr;
import jcuda.driver.CUfunction;
import jcuda.driver.CUjitInputType;
import jcuda.driver.CUlinkState;
import jcuda.driver.CUmodule;
import jcuda.driver.JCudaDriver;
import jcuda.driver.JITOptions;

public class Mining extends Thread {
	private static final String SIMPLEKERNEL = "sha256_cuda";
	String source = "000001d5c27c1236020883c226ccec0d38ee13442493208e9927c3b715ea6e0b,b0bb15df4e3b489c5601b6a9c2d1aea66396b992653f547c22662a69e21bc8ec,439161493,13,1657514400652,b0bb15df4e3b489c5601b6a9c2d1aea66396b992653f547c22662a69e21bc8ec0x0a4240b89c8d901235772ff3882ac3a24dcf0124f5c5f6a903d9005e45029868db@8c34d885b883597c17790d7e20def48ee700884eee1f72c0c245557750ee5ca60x0be9f572f6f96b461c4a229f32fc71a24ea57e4872b21819321bb03d6305a029030x0c50@0.1@1657514354883@2192c25da262ae1c9d01b1df6de0458433cf02f6e4a853b39dde18ffbe47304b0x0a4e104d7dd521b088e6520f2225d3f3dfa91b41f010a1065a6a9a750323a3212b";

	// previous_hash,miner_address,nans,block_number,transaction,transaction,...
	//min:66611349253966442813730644663330183884399686815584447189708332380985641
	/*static String b="212885b2dc656b57bcf2397b0a14c3755365adc7b446879db1e2b1c3aa98f929,"
			+ "8c34d885b883597c17790d7e20def48ee700884eee1f72c0c245557750ee5ca6,"
			+ "nans,"
			+ "1,"//トランザクション引数、区切りは'@'で表す
			+ "8c34d885b883597c17790d7e20def48ee700884eee1f72c0c245557750ee5ca60x0ae9f572f6f96b461c4a229f32fc71a24ea57e4872b21819321bb03d6305a02903@8c34d885b883597c17790d7e20def48ee700884eee1f72c0c245557750ee5ca6@8c34d885b883597c17790d7e20def48ee700884eee1f72c0c245557750ee5ca60x0be9f572f6f96b461c4a229f32fc71a24ea57e4872b21819321bb03d6305a029030x0c49@1.0@625a95956ce07dcc60531515e38feba358304b6bc035e905706513e67fa9392d0x0a5e1fdc6831b3d8c0ef2da2cb5543a994f0cf24adb440a2856637824df0f83ef7";
	 */
	Mining() {
		Thread th = new Thread() {
			@Override
			public void run() {
				int n = 1;
				String rem = "";
				for (String s : Main.console.keySet()) {
					if (s.startsWith("MININGE-00")) {
						rem = s;
					}
				}
				Main.console.remove(rem);
				Main.console.put("MINING", "Now, I started mining!");
				Random ran = new Random();
				String before;
				while (Main.mining) {
					before = (Main.getlatesthash() + "," + Main.w.pub[0].toString(16) + "," + ran.nextInt() + ","
							+ (Main.getBlockSize() + 1) + "," + System.currentTimeMillis());
					for (Transaction t : Main.pool) {
						before = before + "," + t.transaction_sum;
					}
					BigInteger result = new BigInteger(hash(before), 16);
					if (result.compareTo(Main.diff) < 0) {
						Main.console.put("MINING", "mining完了 Hash: " + hash(before));
						Network.shareToNodes("block~" + before);
						if (Main.mati) {
							Main.addBlock(before);
							Main.pool.clear();
						}
					}
				}
				Main.console.remove("MINING");
				Main.console.put("MININGE-00", "Now, I stopped mining!");
			}
		};
		th.start();
	}

	public static String hash(String arg) {
		if (arg != null) {
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
		} else {
			return null;
		}
	}
}
