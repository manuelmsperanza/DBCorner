package com.hoffnungland.db.corner.dbconn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manage the connection with the PostgreSQL database and the statements.
 * @since 10-04-2024
 * @version 0.1
 */

public class PgConnectionManager extends ConnectionManager {
	private static final Logger logger = LogManager.getLogger(PgConnectionManager.class);

	/**
	 * Creates a PostgreSQL connection manager and registers the PostgreSQL JDBC driver.
	 */
	public PgConnectionManager() {
	}
	
	static {
		ConnectionManager.registerDriver(new org.postgresql.Driver());
	}
}
