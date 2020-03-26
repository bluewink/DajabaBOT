package dajaba;

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
	}

}
