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

		proxy("using input-file " + path);

		String[] cmd = new String[args.length - 1];
		System.arraycopy(args, 1, cmd, 0, cmd.length);

		StringBuilder sb = new StringBuilder("running command:");
		for (String c : cmd) {
			sb.append(" ");
			sb.append(c);
		}

		proxy(sb.toString());

		final Process process = Runtime.getRuntime().exec(cmd);
		final BufferedReader out_reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		final BufferedReader err_reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		final PrintWriter in_writer = new PrintWriter(process.getOutputStream());

		Thread outputProxy = new Thread(() -> {
			proxy("output thread started");
			String line = null;
			try {
				while ((line = out_reader.readLine()) != null && !Thread.interrupted()) {
					server(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			proxy("output thread terminated");
		});
		Thread errorProxy = new Thread(() -> {
			proxy("error thread started");
			String line = null;
			try {
				while ((line = err_reader.readLine()) != null && !Thread.interrupted()) {
					server(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			proxy("error thread terminated");
		});
		Thread inputProxy = new Thread(() -> {
			proxy("input thread started");
			while (!Thread.interrupted()) {
				File f = new File(path);
				if (f.exists() && f.canRead() && f.isFile()) {
					try {
						List<String> lines = Files.readAllLines(f.toPath());
						for (String string : lines) {
							proxy(string);
							in_writer.println(string);
							in_writer.flush();
						}
						f.delete();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
				}
			}
			proxy("input thread terminated");
		});

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (!process.isAlive())
				return;
			proxy("received shutdown signal");
			for (int i = 10; i > 0; i--) {
				String c = String.format("say server stopping in %2d seconds", i);
				proxy(c);
				in_writer.println(c);
				in_writer.flush();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			proxy("stop");
			in_writer.println("stop");
			in_writer.flush();
			try {
				int exit = process.waitFor();
				proxy("server exited with status " + exit);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}));

		outputProxy.setDaemon(true);
		errorProxy.setDaemon(true);
		inputProxy.setDaemon(true);

		outputProxy.start();
		errorProxy.start();
		inputProxy.start();

		int exit = process.waitFor();
		proxy("server exited with status " + exit);
	}

	static void server(String line) {
		System.out.println("[server] " + line);
	}

	static void proxy(String line) {
		System.out.println("[proxy ] " + line);
	}

}
