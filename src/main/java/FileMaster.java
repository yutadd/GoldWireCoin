package main.java;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by sfaxi19 on 12.10.16.
 */
public class FileMaster {

    public static String loadTextFromFile(String filepath) throws IOException {
        File f = new File(filepath);
        BufferedReader fin = new BufferedReader(new FileReader(f));
        StringBuffer text = new StringBuffer();
        String line;
        while ((line = fin.readLine()) != null) {
            text.append(line);
        }
        fin.close();
        return text.toString();
    }

    public static void saveTextToFile(String filepath, String text) throws IOException {
        File f = new File(filepath);
        BufferedWriter fout = new BufferedWriter(new FileWriter(f));
        fout.write(text);
        fout.flush();
        fout.close();
        System.out.println("save - " + text.length());
    }

    public static byte[] loadBytesFromFile(String filepath) throws IOException {
        File f = new File(filepath);
        BufferedReader fin = new BufferedReader(new FileReader(f));
        StringBuffer text = new StringBuffer();
        String line;
        // fin.rea
        while ((line = fin.readLine()) != null) {
            text.append(line);
        }
        fin.close();
        return text.toString().getBytes();
    }

}
