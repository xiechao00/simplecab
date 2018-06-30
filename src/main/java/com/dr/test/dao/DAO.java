package com.dr.test.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

public interface DAO {
	int getTripCountsByIdAndDate(String id, Date date) throws SQLException;

	void runSqlScript(String filePath)
			throws IOException, SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException;
}
