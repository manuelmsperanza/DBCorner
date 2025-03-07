package com.hoffnungland.db.corner.javadbconn;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hoffnungland.db.corner.dbconn.ConnectionManager;

/**
 * Manage the connection with the Derby Java database and the statements.
 * @author manuel.m.speranza
 * @since 10-05-2017
 * @version 0.1
 */

public class JdbcConnectionManager extends ConnectionManager {
	private static final Logger logger = LogManager.getLogger(JdbcConnectionManager.class);

	
	/**
	 * Retrieve the next value of the sequence specified.
	 * @param sequenceName The name of the sequence
	 * @return the next value
	 * @throws SQLException if a database access error occurs
	 * @author manuel.m.speranza
	 * @since 12-05-2017
	 */
	//TODO verify data type
	public long getNextVal(String sequenceName) throws SQLException{
		logger.traceEntry();
	
		PreparedStatement stm = this.prepareQuery(sequenceName, "VALUES(NEXT VALUE FOR " + sequenceName + ")").getStm();
		ResultSet rs = stm.executeQuery();
		rs.next();
		return logger.traceExit(rs.getLong(1));
	}

}
