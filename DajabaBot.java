package dajaba;

<<<<<<< HEAD
public class DajabaBot {

	public static void main(String[] args) throws Exception {
		Setting setting = new Setting();
		setting.set();
	}
=======
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class DajabaBot {

	public static void startWork(Setting setter, Logger logger, DajabaThread exThread) {
		
		while (exThread.isAlive()) {
			
			logger.info("process starts");
			Vector<String> urlList = new Vector<String>();
			Parser parser = new Parser(setter.getHtmlFilePath());
			try {
				urlList = parser.openDoc();
			} catch (Exception e) {
				logger.warn("sth wrong in opening html file", e);
			}
			Vector<DataFromDB> dbData = new Vector<DataFromDB>();

			DataBaseManager dbm = new DataBaseManager(dbData, setter.getDatabaseDriverPath(), setter.getDatabaseName());
			try {
				dbm.databaseConnect(setter.getDatabaseDriverPath(), setter.getDatabasePath(), setter.getDatabaseName(),
						setter.getTableName());
			} catch (Exception e) {
				logger.warn("sth wrong in connecting db", e);
			}
			dbm.selectAll(setter.getTableName());
			int res;
			for (String x : urlList) {
				res = repCheck(x, dbData);
				if (res == -1) { // if it is new URL
					try {
						dbm.insert(setter.getTableName(), urlList, x);
					} catch (Exception e) {
						logger.warn("sth wrong in inserting URL to DB", e);
					}
					logger.info("new URL " + x + " has been added to DB");
				} else { // already in the DB
					logger.debug("url is already in the DB");
					try {
						dbm.update(setter.getTableName(), res, x);
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
			if(exThread.isAlive() == false)
				break;
			
			long timeToSleep = (long)setter.getProgramCycle();
			TimeUnit time = TimeUnit.SECONDS;
			try {
				time.sleep(timeToSleep);
			} catch (InterruptedException e) {
				logger.error("sth wrong in program sleep",e);
			}
		}
	}
	

	public static void main(String[] args) {
		
		Logger logger = Logger.getLogger(DajabaBot.class);
		Setting setter = new Setting();
		try {
			setter.getPropValues();
			logger.info("setting is done");
		} catch (IOException e) {
			logger.error("sth wrong in reading configFile", e);
			return;
		}
		Thread DT = new Thread(new DajabaThread(setter,logger), "DajabaBot");
		DT.start();
	/*	if (Boolean.parseBoolean(System.getenv("RUNNING_IN_ECLIPSE"))) {
		    System.out.println("You're using Eclipse; click in this console and " +
		            "press ENTER to call System.exit() and run the shutdown routine.");
		    try {
		        System.in.read();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		    System.exit(0);
		}
	*/	
		// ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		// scheduler.scheduleAtFixedRate(new DajabaThread(setter, logger), 0,
		// setter.getProgramCycle(), TimeUnit.SECONDS);
		
	}

	public static int repCheck(String url, Vector<DataFromDB> dbData) {

		for (int i = 0; i < dbData.size(); i++) {
			if (dbData.elementAt(i).urlDB.contentEquals(url)) {
				return dbData.elementAt(i).id;
			}
		}
		return -1;
	}

>>>>>>> db9f257e5343158965539a7872ba2cf9396a9244
}
