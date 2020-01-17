package dajaba;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GetConfigFile {
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
			Setting.programCycle = Integer.parseInt(prop.getProperty("programCycle"));
			Setting.htmlFilePath = prop.getProperty("htmlFilePath");
			Setting.databaseDriverPath = prop.getProperty("databaseDriverPath");
			Setting.databaseName = prop.getProperty("databaseName");
			Setting.databasePath = prop.getProperty("databasePath");
			Setting.tableName = prop.getProperty("tableName");
			Setting.log4jConfPath = prop.getProperty("log4jConfPath");

		} catch (Exception e) {

		}
	}

}
