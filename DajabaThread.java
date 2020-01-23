package dajaba;

import org.apache.log4j.Logger;

public class DajabaThread implements Runnable {

	private boolean isAlive = true;
	Setting setter = new Setting();
	Logger logger;

	DajabaThread(Setting _setter, Logger _logger) {
		setter = _setter;
		logger = _logger;
	}

	@Override
	public void run() {
		String thName = Thread.currentThread().getName();
		Thread shutDownHook = new ShutDownHook(Thread.currentThread(), "Shutdown");
		Runtime.getRuntime().addShutdownHook(shutDownHook);

		while (isAlive()) {
			try {
				DajabaBot.startWork(setter, logger, this);
			} catch (Exception e) {
				logger.error("sth wrong in scheduler", e);
			}
		}
		logger.info(thName + " is terminated");
	}

	public void shutdown() {
		logger.info("[" + Thread.currentThread().getName() + "] called shutdown");
		setAlive(false);

	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	private class ShutDownHook extends Thread {
		private Thread target;

		public ShutDownHook(Thread target, String name) {
			super(name);
			this.target = target;
		}

		public void run() {
			shutdown();
			
			try {
				target.join();
			} catch (InterruptedException e) {
				logger.error("sth wrong in shutdown process",e);
			}
		}

	}

}
