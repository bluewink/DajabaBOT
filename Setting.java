package dajaba;

<<<<<<< HEAD
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
=======
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Setting {
	private int programCycle;
	private String htmlFilePath;
	private String databaseDriverPath;
	private String databaseName;
	private String databasePath;
	private String tableName;

	InputStream inputStream;

	public void getPropValues() throws IOException {
		try {
			Properties prop = new Properties();
			String propFileName = "config.properties";

			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file " + propFileName + " not found in the classpath");
			}
			setProgramCycle(Integer.parseInt(prop.getProperty("programCycle")));
			setHtmlFilePath(prop.getProperty("htmlFilePath"));
			setDatabaseDriverPath(prop.getProperty("databaseDriverPath"));
			setDatabaseName(prop.getProperty("databaseName"));
			setDatabasePath(prop.getProperty("databasePath"));
			setTableName(prop.getProperty("tableName"));

		} catch (Exception e) {
		}
	}

	public String getDatabaseDriverPath() {
		return databaseDriverPath;
	}

	public void setDatabaseDriverPath(String databaseDriverPath) {
		this.databaseDriverPath = databaseDriverPath;
	}

	public String getHtmlFilePath() {
		return htmlFilePath;
	}

	public void setHtmlFilePath(String htmlFilePath) {
		this.htmlFilePath = htmlFilePath;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getDatabasePath() {
		return databasePath;
	}

	public void setDatabasePath(String databasePath) {
		this.databasePath = databasePath;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getProgramCycle() {
		return programCycle;
	}

	public void setProgramCycle(int programCycle) {
		this.programCycle = programCycle;
>>>>>>> db9f257e5343158965539a7872ba2cf9396a9244
	}

}
