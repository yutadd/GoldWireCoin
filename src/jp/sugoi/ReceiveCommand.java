package jp.sugoi;

import java.io.IOException;
import java.util.Scanner;

public class ReceiveCommand {

	public ReceiveCommand() {
		Thread th_ = new Thread() {
			@Override
			public void run() {
				Scanner sc = new Scanner(System.in);
				//InputStreamReader isr=new InputStreamReader(System.in);
				//char c;
				while (true) {
					System.out.print("\033[34m┌──(\033[31mGWC\033[37m x💀x \033[31mCMD\033[34m)-[\033[37mBlock Size :"
							+ Main.size + "\033[34m]\r\n└─#\033[37m");
					String s = sc.nextLine();
					String cmd = s.split(" ")[0];
					if (cmd.equals("pay")) {
						new Pay(s);
					} else if (cmd.equals("mining")) {
						if (Main.mining) {
							Main.mining = false;
							System.out.println("\033[31mMINING STOPPED.");
						} else {
							Main.mining = true;
							new Mining();
							System.out.println("\033[32mMINING STARTED.");
						}
					} else if (cmd.equals("stats")) {
						Main.showStats();
					} else if (cmd.equals("help")) {
						System.out.println();
						System.out.println(Main.man);
						System.out.println();
					} else if (cmd.equals("clear") || cmd.equals("cls")) {
						Main.console_clear();
					} else if (cmd.equals("exit")) {
						System.exit(0);
					} else {
						System.out.println("execute→" + s);
						try {
							if (System.getProperty("os.name").contains("Windows")) {
								new ProcessBuilder("cmd", "/c", s).inheritIO().start().waitFor();
							} else {
								new ProcessBuilder(s.split(" ")).inheritIO().start().waitFor();
							}
						} catch (IOException | InterruptedException ex) {
						}
					}
				}
			}
		};
	th_.start();
	}

}
