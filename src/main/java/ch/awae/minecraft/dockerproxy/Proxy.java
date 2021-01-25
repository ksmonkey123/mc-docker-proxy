package ch.awae.minecraft.dockerproxy;

import java.io.IOException;

public class Proxy {

	public static void main(String[] args) throws IOException, InterruptedException {

		final String path = args[0];

		Log.proxy("using input-file " + path);

		String[] cmd = new String[args.length - 1];
		System.arraycopy(args, 1, cmd, 0, cmd.length);

		StringBuilder sb = new StringBuilder("running command:");
		for (String c : cmd) {
			sb.append(" ");
			sb.append(c);
		}

		Log.proxy(sb.toString());

		final Process process = Runtime.getRuntime().exec(cmd);

		OutputThread outputProxy = new OutputThread(process.getInputStream());
		OutputThread errorProxy  = new OutputThread(process.getErrorStream());
		InputThread inputProxy = new InputThread(path, process.getOutputStream());

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (!process.isAlive())
				return;
			Log.proxy("received shutdown signal");
			for (int i = 10; i > 0; i--) {
				String c = String.format("say server stopping in %2d seconds", i);
				Log.proxy(c);
				inputProxy.relay(c);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Log.proxy("stop");
			inputProxy.relay("stop");
			try {
				int exit = process.waitFor();
				Log.proxy("server exited with status " + exit);
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
		Log.proxy("server exited with status " + exit);
	}

}
