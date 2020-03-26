package dajaba;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class DajabaThread implements Runnable {

	private boolean isAlive = true;
	Logger logger;
	private String htmlFilePath;
	private String dataBaseDriverPath;
	private String dataBaseName;
	private int programCycle;
	DajabaThread(int _programCycle, String _databaseDriverPath, String _databaseName, String _databasePath,String _htmlFilePath, Logger _logger) {
		logger = _logger;
		htmlFilePath = _htmlFilePath;
		dataBaseDriverPath = _databaseDriverPath;
		dataBaseName = _databaseName;
		programCycle = _programCycle;
	}

	@Override
	public void run() {
		String thName = Thread.currentThread().getName();
		Thread shutDownHook = new ShutDownHook(Thread.currentThread(), "Shutdown");
		Runtime.getRuntime().addShutdownHook(shutDownHook);

		while (isAlive()) {
			try {
				while (isAlive()) {
					logger.info("process starts");
					Vector<String> urlList = new Vector<String>();
					Parser parser = new Parser(htmlFilePath);
					try {
						urlList = parser.openDoc();
					} catch (Exception e) {
						logger.warn("sth wrong in opening html file", e);
					}
					Vector<DataFromDB> dbData = new Vector<DataFromDB>();

					DataBaseManager dbm = new DataBaseManager(dbData, dataBaseDriverPath,
							dataBaseName);

					dbm.selectAll(dataBaseName);
					int res;
					for (String x : urlList) {
						res = repCheck(x, dbData);
						if (res == -1) { // if it is new URL
							try {
								dbm.insert(urlList, x, dataBaseName);
							} catch (Exception e) {
								logger.warn("sth wrong in inserting URL to DB", e);
							}
							logger.info("new URL " + x + " has been added to DB");
						} else { // already in the DB
							logger.debug("url is already in the DB");
							try {
								dbm.update(res, x, dataBaseName);
								logger.debug("cnt for " + x + " has been updated");
							} catch (Exception e) {
								logger.warn("sth wrong in updating DB", e);
							}

						}
					}
					try {
						dbm.dbClose();
					} catch (Exception e) {
						logger.warn("sth wrong in closing DB", e);
					}

					logger.info("process has been completed");
					//if (isAlive() == false)
						//break;

					long timeToSleep = (long) programCycle;
					TimeUnit time = TimeUnit.SECONDS;
					long count =0;
					try {
						while(count != timeToSleep && isAlive() ) {
						time.sleep(1);
						count++;
						}
					} catch (InterruptedException e) {
						logger.error("sth wrong in program sleep", e);
					}
				}

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
				logger.error("sth wrong in shutdown process", e);
			}
		}

	}
	
	public int repCheck(String url, Vector<DataFromDB> dbData) {

		for (int i = 0; i < dbData.size(); i++) {
			if (dbData.elementAt(i).urlDB.contentEquals(url)) {
				return dbData.elementAt(i).id;
			}
		}
		return -1;
	}


}
