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
	private Vector<String> urlList;

	public class DataFromDB {
		public String urlDB;
		public int id;
		public int cnt;

		public DataFromDB(String url, int _id, int _cnt) {
			urlDB = url;
			id = _id;
			cnt = _cnt;
		}
	}

	private Vector<DataFromDB> dbData = new Vector<DataFromDB>();
	private DataFromDB tmpData;
	private Date nowDate = new Date();
	private String nDate = nowDate.toString();

	public DataBaseManager(String dbDriverPath, String dbName, Vector<String> _urlList) {
		url = dbDriverPath + dbName;
		beginTime = System.nanoTime();
		urlList = _urlList;
	}

	public void databaseConnect(String dbDriverPath, String dbFilePath, String dbName, String tableName)
			throws Exception {
		// ----------if you want to make new dbFile--------
		// String dirPath = dbFilePath;
		// File dbFile = new File(dirPath, dbName);
		// if (dbFile.exists()) {
		logger.info("db named " + dbName + "already exists.");
		logger.info("connecting to " + dbName);
		try {
			conn = DriverManager.getConnection(url);
			logger.info("Connection to " + dbName + " has been established");
			this.selectAll(tableName);
		} catch (SQLException e) {
			logger.warn("sth wroing in connecting DB", e);
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
					/*
					 * tmpData.urlDB = tmp; tmpData.id = rs.getInt("id"); tmpData.cnt =
					 * rs.getInt("checked_cnt"); System.out.println(tmpData.urlDB); why
					 * not!?!!@?!?@!?
					 */
					dbData.add(tmpData);
				} else
					logger.info("There is no url in DB");
			}

			this.insert(tableName, urlList);

		} catch (SQLException e) {
			logger.warn("sth wrong in selecting data from DB", e);
		}
	}

	public void insert(String tableName, List<String> urlList) {

		PreparedStatement pstmt;
		Integer res;
		for (String x : urlList) {
			res = repCheck(x);
			if (res == -1) { // if it is new URL
				String sql = "INSERT INTO " + tableName + "(URL, added_date, checked_date) VALUES(?,?,?)";
				try {
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, x);
					pstmt.setString(2, nDate);
					pstmt.setString(3, nDate);
					pstmt.executeUpdate();
				} catch (SQLException e) {
					logger.warn(e.getMessage());
				}
				logger.info("new URL " + x + " has been added to DB");
			} else { // already in the DB
				logger.debug("url is already in the DB");
				// cntChecked++
				String sql = "UPDATE " + tableName + " SET checked_date=?, checked_cnt=? WHERE id=?";
				try {
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, nDate);
					pstmt.setInt(2, dbData.elementAt(res).cnt + 1);
					pstmt.setInt(3, dbData.elementAt(res).id);
					pstmt.executeUpdate();
					logger.debug("cnt for " + dbData.elementAt(res).urlDB + " has been updated");
				} catch (SQLException e) {
					logger.warn("sth wrong in inserting to DB", e);
				}

			}
		}

		this.dbClose();

	}

	public void dbClose() {
		try {
			conn.close();
			logger.info("DB connection has been closed");
		} catch (SQLException e) {
			logger.warn(e.getMessage());
		}
		double execTime = (System.nanoTime() - beginTime) / 1000000;
		logger.info("DB process time : " + execTime + " ms");
	}

	public int repCheck(String url) {

		for (int i = 0; i < dbData.size(); i++) {
			if (dbData.elementAt(i).urlDB.contentEquals(url)) {
				return i;
			}
		}
		return -1;
	}

	public void update(String tableName) throws SQLException {
		String sql = "UPDATE " + tableName + " SET dateChecked=?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, nDate);
		pstmt.executeUpdate();
	}
}
