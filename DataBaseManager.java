package dajaba;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

public class DataBaseManager {

	private Logger logger = Logger.getLogger(DataBaseManager.class);
	private double beginTime;
	private String url;
	private Connection conn = null;

	private DataFromDB tmpData;
	private Date nowDate = new Date();
	private String nDate = nowDate.toString();
	private Vector<DataFromDB> dbData = new Vector<DataFromDB>();

	public DataBaseManager(Vector<DataFromDB> _dbData, String dbDriverPath, String dbName) {
		dbData = _dbData;
		url = dbDriverPath + dbName;
		beginTime = System.nanoTime();
	}

	public void databaseConnect(String dbDriverPath, String dbFilePath, String dbName, String tableName)
			throws Exception {
		// ----------if you want to make new dbFile--------
		// String dirPath = dbFilePath;
		// File dbFile = new File(dirPath, dbName);
		// if (dbFile.exists()) {
		logger.info("connecting to " + dbName);
		try {
			conn = DriverManager.getConnection(url);
			logger.info("Connection to " + dbName + " has been established");

		} catch (SQLException e) {
		}

		// } //else {
		// createNewDb(dbDriverPath, dbName);
		// }
	}

	/*
	 * --------------if you want to make new db file---------- public void
	 * createNewDb(String dbDriverPath, String fileName) {
	 * 
	 * String url = dbDriverPath + fileName;
	 * 
	 * try { Connection conn = DriverManager.getConnection(url); if (conn != null) {
	 * DatabaseMetaData meta = conn.getMetaData(); logger.info("The driver name is "
	 * + meta.getDriverName()); logger.info("A new db has been created"); }
	 * 
	 * } catch (SQLException e) { System.out.println(e.getMessage()); }
	 * 
	 * }
	 */
	/*
	 * --------------if you want to new db table--------------
	 * 
	 * public void createNewTable(String tableName) { String sql =
	 * "CREATE TABLE IF NOT EXISTS " + tableName + "(\n" +
	 * "id integer PRIMARY KEY,\n" + "URL text,\n" + "checked_cnt integer,\n" +
	 * "added_date text,\n" + "checked_date text\n" + ");"; try { Connection conn =
	 * DriverManager.getConnection(url); Statement stmt = conn.createStatement();
	 * stmt.execute(sql);
	 * 
	 * } catch (SQLException e) { logger.warn(e.getMessage()); } }
	 */
	public void selectAll(String tableName) {
		String sql = "SELECT * FROM " + tableName;
		String tmp = null;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				tmp = rs.getString("URL");
				if (tmp != null) {
					tmpData = new DataFromDB(tmp, rs.getInt("id"), rs.getInt("checked_cnt"));
					dbData.add(tmpData);
				} else
					logger.info("There is no url in DB");
			}

		} catch (SQLException e) {
			logger.warn("sth wrong in selecting data from DB", e);
		}
	}

	public void insert(String tableName, List<String> urlList, String x) {

		PreparedStatement pstmt;
		String sql = "INSERT INTO " + tableName + "(URL, added_date, checked_date) VALUES(?,?,?)";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, x);
			pstmt.setString(2, nDate);
			pstmt.setString(3, nDate);
			pstmt.executeUpdate();
		} catch (SQLException e) {
		}
	}

	public void dbClose() {
		try {
			conn.close();
			logger.info("DB connection has been closed");
		} catch (SQLException e) {
		}
		double execTime = (System.nanoTime() - beginTime) / 1000000;
		logger.info("DB process time : " + execTime + " ms");
	}

	public int getCnt(int _id) {
		for (int i = 0; i < dbData.size(); i++) {
			if (dbData.elementAt(i).id == _id) {
				return dbData.elementAt(i).cnt;
			}
		}
		return -1;
	}

	public void update(String tableName, int _id, String x) throws SQLException {
		{ // already in the DB
			logger.debug("url is already in the DB");
			// cntChecked++
			PreparedStatement pstmt;
			String sql = "UPDATE " + tableName + " SET checked_date=?, checked_cnt=? WHERE id=?";
			try {
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, nDate);
				pstmt.setInt(2, this.getCnt(_id) + 1);
				pstmt.setInt(3, _id);
				pstmt.executeUpdate();

			} catch (SQLException e) {
			}

		}
	}
}
