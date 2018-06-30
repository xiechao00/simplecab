package com.dr.test.dao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.util.Date;

import com.dr.test.util.Constant;
import com.dr.test.util.Util;
import com.ibatis.common.jdbc.ScriptRunner;

public class MySQLLocalDemoDaoImpl implements DAO {
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String URI = "jdbc:mysql://localhost:3306/";
	private static final String SCHEMA = "ny_cab_data";
	private static final String TABLE = "cab_trip_data";
	private static final String USER = "root";
	private static final String PWD = "root";

	public MySQLLocalDemoDaoImpl() throws ClassNotFoundException {
		Class.forName(JDBC_DRIVER);
	}

	@Override
	public int getTripCountsByIdAndDate(String id, Date date) throws SQLException {
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			con = DriverManager.getConnection(URI + SCHEMA, USER, PWD);
			stmt = con.prepareStatement("select count(*) from " + TABLE
					+ " where medallion = ?"
					+ " and DATE(pickup_datetime) = ?");
			stmt.setString(1, id);
			stmt.setString(2, Util.dateToString(date, Constant.DATE_FORMAT));
			rs = stmt.executeQuery();
			while(rs.next()) {
				return rs.getInt(1);
			}
			return 0;
		} finally {
			if (con != null) {
				con.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
	}

	@Override
	public void runSqlScript(String filePath)
			throws IOException, SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {

		//initialize object for ScripRunner
		ScriptRunner sr = new ScriptRunner();
		sr.setDriver(JDBC_DRIVER);
		sr.setUrl(URI + SCHEMA);
		sr.setUsername(USER);
		sr.setPassword(PWD);

		//give the input file to Reader
		Reader reader = new BufferedReader(
				new FileReader(filePath));

		//execute script
		sr.runScript(reader);
	}
}
