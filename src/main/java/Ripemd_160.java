package main.java;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by sfaxi19 on 24.02.17.
 */
public class Ripemd_160 {

	private final int[] R1 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
	private final int[] R2 = {7, 4, 13, 1, 10, 6, 15, 3, 12, 0, 9, 5, 2, 14, 11, 8};
	private final int[] R3 = {3, 10, 14, 4, 9, 15, 8, 1, 2, 7, 0, 6, 13, 11, 5, 12};
	private final int[] R4 = {1, 9, 11, 10, 0, 8, 12, 4, 13, 3, 7, 15, 14, 5, 6, 2};
	private final int[] R5 = {4, 0, 5, 9, 7, 12, 2, 10, 14, 1, 3, 8, 11, 6, 15, 13};

	private final int[] R1_ = {5, 14, 7, 0, 9, 2, 11, 4, 13, 6, 15, 8, 1, 10, 3, 12};
	private final int[] R2_ = {6, 11, 3, 7, 0, 13, 5, 10, 14, 15, 8, 12, 4, 9, 1, 2};
	private final int[] R3_ = {15, 5, 1, 3, 7, 14, 6, 9, 11, 8, 12, 2, 10, 0, 4, 13};
	private final int[] R4_ = {8, 6, 4, 1, 3, 11, 15, 0, 5, 12, 2, 13, 9, 7, 10, 14};
	private final int[] R5_ = {12, 15, 10, 4, 1, 5, 8, 7, 6, 2, 13, 14, 0, 3, 9, 11};

	private final int[] S1 = {11, 14, 15, 12, 5, 8, 7, 9, 11, 13, 14, 15, 6, 7, 9, 8};
	private final int[] S2 = {7, 6, 8, 13, 11, 9, 7, 15, 7, 12, 15, 9, 11, 7, 13, 12};
	private final int[] S3 = {11, 13, 6, 7, 14, 9, 13, 15, 14, 8, 13, 6, 5, 12, 7, 5};
	private final int[] S4 = {11, 12, 14, 15, 14, 15, 9, 8, 9, 14, 5, 6, 8, 6, 5, 12};
	private final int[] S5 = {9, 15, 5, 11, 6, 8, 13, 12, 5, 12, 13, 14, 11, 8, 5, 6};

	private final int[] S1_ = {8, 9, 9, 11, 13, 15, 15, 5, 7, 7, 8, 11, 14, 14, 12, 6};
	private final int[] S2_ = {9, 13, 15, 7, 12, 8, 9, 11, 7, 7, 12, 7, 6, 15, 13, 11};
	private final int[] S3_ = {9, 7, 15, 11, 8, 6, 6, 14, 12, 13, 5, 14, 13, 13, 7, 5};
	private final int[] S4_ = {15, 5, 8, 11, 14, 14, 6, 14, 6, 9, 12, 9, 12, 5, 15, 8};
	private final int[] S5_ = {8, 5, 12, 9, 12, 5, 14, 6, 8, 13, 6, 5, 15, 13, 11, 11};

	private enum Intervals {
		Interval1,
		Interval2,
		Interval3,
		Interval4,
		Interval5,
		Unknown
	}

	public static void main(String[] args) throws IOException {
		Ripemd_160 ripe = new Ripemd_160();
		System.out.print(" ");
		Scanner scan = new Scanner(System.in);
		String data = scan.nextLine();
		// String dataFile = FileMaster.loadTextFromFile("image.bmp");
		byte[] hashCode = ripe.getHashCode(data);
	}

	private byte[] initData(String data) {
		int byteCount = data.getBytes().length;
		long bitCount = byteCount * 8;
		byte[] bytes = data.getBytes();
		int size = 0;
		if ((bytes.length % 64 + 1) <= 56) {
			size = bytes.length + (64 - bytes.length % 64);
		} else {
			size = bytes.length + 64 + (64 - bytes.length % 64);
		}
		System.out.println("\nInput data:\t\"" + data + "\"");
		System.out.println("Input bytes:\t" + byteCount);
		System.out.println("Input bits:\t" + byteCount * 8);
		System.out.println("Size:\t" + size);
		byte[] byteArray = new byte[size];
		for (int i = 0; i < byteArray.length; i++) {
			if (i < byteCount) {
				byteArray[i] = bytes[i];
			} else if (i == byteCount) {
				byteArray[i] = (byte) 0x80;
			} else {
				byteArray[i] = 0x00;
			}
		}
		byteArray[byteArray.length - 8] = (byte) (bitCount);
		byteArray[byteArray.length - 7] = (byte) (bitCount >> 8);
		byteArray[byteArray.length - 6] = (byte) (bitCount >> 16);
		byteArray[byteArray.length - 5] = (byte) (bitCount >> 24);
		byteArray[byteArray.length - 4] = (byte) (bitCount >> 32);
		byteArray[byteArray.length - 3] = (byte) (bitCount >> 40);
		byteArray[byteArray.length - 2] = (byte) (bitCount >> 48);
		byteArray[byteArray.length - 1] = (byte) (bitCount >> 56);
		for (int i = 0; i < byteArray.length; i++) {
			//System.out.println("byte[" + i + "] = " + Integer.toHexString(((int) byteArray[i]) & 0xff));
		}
		return byteArray;
	}

	private int[][] getWords(byte[] bytes) {
		int[][] words = new int[bytes.length / 64][16];
		int id = 0;
		for (int i = 0; i < bytes.length / 64; i++) {
			for (int j = 0; j < 16; j++) {
				words[i][j] = ((int) bytes[id]) & 0xff
						| (((int) bytes[id + 1]) & 0xff) << 8
						| (((int) bytes[id + 2]) & 0xff) << 16
						| (((int) bytes[id + 3]) & 0xff) << 24;
				id += 4;
				/* System.out.println("word " + j
                        + ": " + Integer.toHexString(words[i][j]));*/
			}
		}
		return words;
	}

	String printBin(String str, int bytes) {
		StringBuffer sb = new StringBuffer();
		for (int i = str.length(); i < bytes * 8; i++) {
			sb.append("0");
		}
		sb.append(str);
		return sb.toString();
	}

	public static String printByteArray(byte[] array) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			String value = Integer.toHexString(((int) array[i]) & 0xff);
			value = (value.length() == 1) ? ("0" + value) : value;
			sb.append(value);
		}
		return sb.toString();
	}

	public String getHashCodeString(String data) {
		return printByteArray(getHashCode(data));
	}

	public byte[] getHashCode(String data) {
		int h0 = 0x67452301;
		int h1 = 0xefcdab89;
		int h2 = 0x98badcfe;
		int h3 = 0x10325476;
		int h4 = 0xc3d2e1f0;
		byte[] bytesData = initData(data);

		int[][] words = getWords(bytesData);
		int size = bytesData.length / 64;
		int A, B, C, D, E;
		int A_, B_, C_, D_, E_;
		for (int i = 0; i < size; i++) {
			A = h0;
			B = h1;
			C = h2;
			D = h3;
			E = h4;
			A_ = h0;
			B_ = h1;
			C_ = h2;
			D_ = h3;
			E_ = h4;
			int T;
			for (int j = 0; j < 80; j++) {
				int sum1 = A + getF(B, C, D, j) + words[i][getR(j)] + getK(j);
				T = Integer.rotateLeft(sum1, getS(j)) + E;
				A = E;
				E = D;
				D = Integer.rotateLeft(C, 10);
				C = B;
				B = T;
				int sum2 = A_ + getF(B_, C_, D_, 79 - j) + words[i][getR_(j)] + getK_(j);
				T = Integer.rotateLeft(sum2, getS_(j)) + E_;
				A_ = E_;
				E_ = D_;
				D_ = Integer.rotateLeft(C_, 10);
				C_ = B_;
				B_ = T;
				/*System.out.println("\nround " + j + "");
                System.out.println("A_: " + Integer.toHexString(A_));
                System.out.println("B_: " + Integer.toHexString(B_));
                System.out.println("C_: " + Integer.toHexString(C_));
                System.out.println("D_: " + Integer.toHexString(D_));
                System.out.println("E_: " + Integer.toHexString(E_));*/
			}
			T = h1 + C + D_;
			h1 = h2 + D + E_;
			h2 = h3 + E + A_;
			h3 = h4 + A + B_;
			h4 = h0 + B + C_;
			h0 = T;
			/*System.out.println("\nh0 = " + Integer.toHexString(h0));
            System.out.println("h1 = " + Integer.toHexString(h1));
            System.out.println("h2 = " + Integer.toHexString(h2));
            System.out.println("h3 = " + Integer.toHexString(h3));
            System.out.println("h4 = " + Integer.toHexString(h4));*/
		}
		/*System.out.println("-----------------");
        System.out.println(Integer.toHexString(h0));
        System.out.println(Integer.toHexString(h1));
        System.out.println(Integer.toHexString(h2));
        System.out.println(Integer.toHexString(h3));
        System.out.println(Integer.toHexString(h4));*/
		byte[] hashCode = getByteArray(h0, h1, h2, h3, h4);
		System.out.println("hash: " + printByteArray(hashCode));
		//975a97bf25b458af5dc6ec6d956f918cef5fc76c
		return hashCode;
	}

	private byte[] getByteArray(int... h) {
		byte[] hashCode = new byte[h.length * 4];
		for (int i = 0; i < h.length; i++) {
			hashCode[4 * i] = (byte) (h[i]);
			hashCode[4 * i + 1] = (byte) (h[i] >> 8);
			hashCode[4 * i + 2] = (byte) (h[i] >> 16);
			hashCode[4 * i + 3] = (byte) (h[i] >> 24);
		}
		return hashCode;
	}

	private Intervals getInterval(int j) {
		if ((j >= 0) && (j <= 15)) {
			return Intervals.Interval1;
		} else if ((j >= 16) && (j <= 31)) {
			return Intervals.Interval2;
		} else if ((j >= 32) && (j <= 47)) {
			return Intervals.Interval3;
		} else if ((j >= 48) && (j <= 63)) {
			return Intervals.Interval4;
		} else if ((j >= 64) && (j <= 79)) {
			return Intervals.Interval5;
		}
		System.out.println("Bad interval");
		return Intervals.Unknown;
	}

	private int getF(int x, int y, int z, int j) {
		switch (getInterval(j)) {
		case Interval1:
			return x ^ y ^ z;
		case Interval2:
			return (x & y) | ((~x) & z);
		case Interval3:
			return (x | (~y)) ^ z;
		case Interval4:
			return (x & z) | (y & (~z));
		case Interval5:
			return x ^ (y | (~z));
		}
		System.out.println("error getF");
		return -1;
	}

	private int getK(int j) {
		switch (getInterval(j)) {
		case Interval1:
			return 0x00000000;
		case Interval2:
			return 0x5a827999;
		case Interval3:
			return 0x6ed9eba1;
		case Interval4:
			return 0x8f1bbcdc;
		case Interval5:
			return 0xa953fd4e;
		}
		System.out.println("error getK");
		return -1;
	}

	private int getK_(int j) {
		switch (getInterval(j)) {
		case Interval1:
			return 0x50A28BE6;
		case Interval2:
			return 0x5c4dd124;
		case Interval3:
			return 0x6d703ef3;
		case Interval4:
			return 0x7a6d76e9;
		case Interval5:
			return 0x00000000;
		}
		System.out.println("error getK_");
		return -1;
	}

	private int getR(int j) {
		switch (getInterval(j)) {
		case Interval1:
			return R1[j];
		case Interval2:
			return R2[j % 16];
		case Interval3:
			return R3[j % 16];
		case Interval4:
			return R4[j % 16];
		case Interval5:
			return R5[j % 16];
		}
		return -1;
	}

	private int getR_(int j) {
		switch (getInterval(j)) {
		case Interval1:
			return R1_[j % 16];
		case Interval2:
			return R2_[j % 16];
		case Interval3:
			return R3_[j % 16];
		case Interval4:
			return R4_[j % 16];
		case Interval5:
			return R5_[j % 16];
		}
		return -1;
	}

	private int getS(int j) {
		switch (getInterval(j)) {
		case Interval1:
			return S1[j % 16];
		case Interval2:
			return S2[j % 16];
		case Interval3:
			return S3[j % 16];
		case Interval4:
			return S4[j % 16];
		case Interval5:
			return S5[j % 16];
		}
		return -1;
	}

	private int getS_(int j) {
		switch (getInterval(j)) {
		case Interval1:
			return S1_[j % 16];
		case Interval2:
			return S2_[j % 16];
		case Interval3:
			return S3_[j % 16];
		case Interval4:
			return S4_[j % 16];
		case Interval5:
			return S5_[j % 16];
		}
		return -1;
	}

}
