package com.hoffnungland.db.corner.oracleconn;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the Oracle connection pool for Technical Order Management systems.
 * Extends OracleUConnectionPoolManager.
 * @since 20-02-2018
 */
public class TOMOrclUConnPoolManager extends OracleUConnectionPoolManager {
	private static final Logger logger = LogManager.getLogger(TOMOrclUConnPoolManager.class);
	
	/**
	 * Retrieves a connection from the pool.
	 * @return TOMOrclConnManager instance with a connection from the pool.
	 * @throws SQLException if a database access error occurs.
	 * @author manuel.m.speranza
	 * @since 20-02-2018
	 */
	public synchronized TOMOrclConnManager getConnection() throws SQLException {
		logger.traceEntry();
		TOMOrclConnManager connMng = new TOMOrclConnManager();
		connMng.setConnection(pds.getConnection());
		return logger.traceExit(connMng);
	}
}
