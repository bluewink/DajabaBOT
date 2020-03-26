package dajaba;

import java.io.IOException;
import org.apache.log4j.Logger;

public class DajabaBot {

	//public static void startWork(String dataBaseDriverPath, String dataBaseName,Logger logger, DajabaThread exThread) {
	//}
	
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
		Thread DT = new Thread(new DajabaThread(setter.getProgramCycle(),setter.getDatabaseDriverPath(), setter.getDatabaseName(), setter.getDatabasePath(),setter.getHtmlFilePath(),logger), "DajabaBot");
		DT.start();
		if (Boolean.parseBoolean(System.getenv("RUNNING_IN_ECLIPSE"))) {
		    System.out.println("You're using Eclipse; click in this console and " +
		            "press ENTER to call System.exit() and run the shutdown routine.");
		    try {
		        System.in.read();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		    System.exit(0);
		}
		
		// ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		// scheduler.scheduleAtFixedRate(new DajabaThread(setter, logger), 0,
		// setter.getProgramCycle(), TimeUnit.SECONDS);
		
	}

	
}
