package test.key;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;


public class FileUtil {
	private static String keyPath = null;

	public static boolean IsKeyExist(String keyPath) {
		File file = new File(keyPath);
		return file.exists();
	}

	/** * �ԳƼ���-������Կ */
	public static void generatorKey(KeyPair key, String keyPath) {

		try {
			// ��������ļ�,�����Ŀ¼�Ƕ�̬��,�����û�����������Ŀ¼
			ObjectOutputStream keyFile = new ObjectOutputStream(new FileOutputStream(keyPath));
			keyFile.writeObject(key);
			keyFile.close();
		} catch (IOException e4) {
			e4.printStackTrace();
			System.exit(0);
		}
	}

	/** * �ԳƼ���-��ȡ��Կ. */
	public static KeyPair getSecretKey(String keyPath) {
		// ����Կ�ļ��ж���Կ
		KeyPair key = null;
		try {
			ObjectInputStream keyFile = new ObjectInputStream(new FileInputStream(keyPath));
			key = (KeyPair) keyFile.readObject();
			keyFile.close();
		} catch (FileNotFoundException ey1) {
			ey1.printStackTrace();
			System.exit(0);
		} catch (Exception ey2) {
			ey2.printStackTrace();
		}
		return key;
	}
}
