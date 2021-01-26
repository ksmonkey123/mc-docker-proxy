package ch.awae.minecraft.dockerproxy;

import ch.awae.minecraft.dockerproxy.threads.InputThread;
import ch.awae.minecraft.dockerproxy.threads.OutputThread;
import ch.awae.minecraft.dockerproxy.threads.WatchdogTimerImpl;

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

		InputThread inputProxy = new InputThread(path, process.getOutputStream());
		WatchdogTimerImpl watchdog = new WatchdogTimerImpl(inputProxy, process);
		OutputThread outputProxy = new OutputThread(process.getInputStream(), watchdog);
		OutputThread errorProxy  = new OutputThread(process.getErrorStream(), watchdog);

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
		watchdog.setDaemon(true);

		outputProxy.start();
		errorProxy.start();
		inputProxy.start();
		watchdog.start();

		int exit = process.waitFor();
		Log.proxy("server exited with status " + exit);
	}

}
