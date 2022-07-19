package jp.sugoi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Cudautl {
	static String preparePtxFile(String cuFileName) {
		try {
			int endIndex = cuFileName.lastIndexOf('.');
			if (endIndex == -1) {
				endIndex = cuFileName.length() - 1;
			}
			String ptxFileName = cuFileName.substring(0, endIndex + 1) + "ptx";
			File ptxFile = new File(ptxFileName);
			if (ptxFile.exists()) {
				return ptxFileName;
			}

			File cuFile = new File(cuFileName);
			if (!cuFile.exists()) {
				throw new IOException("Input file not found: " + cuFileName);
			}
			String modelString = "-m" + System.getProperty("sun.arch.data.model");
			String command = "nvcc " + modelString + " -ptx " +
					cuFile.getPath() + " -o " + ptxFileName;

			System.out.println("Executing\n" + command);
			Process process = Runtime.getRuntime().exec(command);

			String errorMessage = new String(toByteArray(process.getErrorStream()));
			String outputMessage = new String(toByteArray(process.getInputStream()));
			int exitValue = 0;
			try {
				exitValue = process.waitFor();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new IOException(
						"Interrupted while waiting for nvcc output", e);
			}

			if (exitValue != 0) {
				System.out.println("nvcc process exitValue " + exitValue);
				System.out.println("errorMessage:\n" + errorMessage);
				System.out.println("outputMessage:\n" + outputMessage);
				throw new IOException(
						"Could not create .ptx file: " + errorMessage);
			}

			System.out.println("Finished creating PTX file");
			return ptxFileName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static byte[] toByteArray(InputStream inputStream)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte buffer[] = new byte[8192];
		while (true) {
			int read = inputStream.read(buffer);
			if (read == -1) {
				break;
			}
			baos.write(buffer, 0, read);
		}
		return baos.toByteArray();
	}
}
