package ch.awae.ioproxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;

public class Proxy {

	public static void main(String[] args) throws IOException, InterruptedException {

		final String path = args[0];

		System.out.println("%PROXY% using input-file " + path);

		String[] cmd = new String[args.length - 1];
		System.arraycopy(args, 1, cmd, 0, cmd.length);

		
		StringBuilder sb = new StringBuilder("%PROXY% running command:");
		for (String c : cmd) {
			sb.append(" ");
			sb.append(c);
		}

		System.out.println(sb.toString());

		
		final Process process = Runtime.getRuntime().exec(cmd);
		final BufferedReader out_reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		final BufferedReader err_reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		final PrintWriter in_writer = new PrintWriter(process.getOutputStream());

		Thread outputProxy = new Thread(() -> {
			String line = null;
			try {
				while ((line = out_reader.readLine()) != null) {
					System.out.println(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		Thread errorProxy = new Thread(() -> {
			String line = null;
			try {
				while ((line = err_reader.readLine()) != null) {
					System.err.println(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		Thread inputProxy = new Thread(() -> {
			while (true) {
				File f = new File(path);
				if (f.exists() && f.canRead() && f.isFile()) {
					try {
						List<String> lines = Files.readAllLines(f.toPath());
						for (String string : lines) {
							System.out.println("%PROXY% " + string);
							in_writer.println(string);
							in_writer.flush();
						}
						f.delete();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});

		outputProxy.setDaemon(true);
		errorProxy.setDaemon(true);
		inputProxy.setDaemon(true);

		outputProxy.start();
		errorProxy.start();
		inputProxy.start();

		int exit = process.waitFor();

		System.out.println("%PROXY% process exited with status " + exit);
	}

}
