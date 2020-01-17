package dajaba;

import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class Setting {

	public static int programCycle;
	public static String htmlFilePath;
	public static String databaseDriverPath;
	public static String databaseName;
	public static String databasePath;
	public static String tableName;
	public static String log4jConfPath;

	public void set() {
		Logger logger = Logger.getLogger(Setting.class);
		GetConfigFile getConfigFile = new GetConfigFile();
		try {
			getConfigFile.getPropValues();
			logger.info("setting is done");
		} catch (IOException e) {
			logger.warn("sth wrong in reading configFile", e);
		}
		logger.info("program starts");
		Vector<String> urlList = new Vector<String>();
		Parser parser = new Parser(htmlFilePath);
		try {
			urlList = parser.openDoc();
		} catch (Exception e) {
			logger.warn("sth wrong in opening html file", e);
		}

		DataBaseManager dbm = new DataBaseManager(databaseDriverPath, databaseName, urlList);
		try {
			dbm.databaseConnect(databaseDriverPath, databasePath, databaseName, tableName);
		} catch (Exception e) {
			logger.warn("sth wrong in db", e);
		}
		logger.info("program ends");

		Runnable runnable = new Runnable() {
			public void run() {
				try {
					set();
				} catch (Exception e) {
					logger.warn("sth wrong in scheduler", e);
				}
			}

		};
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(runnable, programCycle, programCycle, TimeUnit.HOURS);
	}

}
