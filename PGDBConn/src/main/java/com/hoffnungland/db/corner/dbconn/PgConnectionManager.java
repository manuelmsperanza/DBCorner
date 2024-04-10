package com.hoffnungland.db.corner.dbconn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manage the connection with the PostgreSQL database and the statements.
 * @author manuel.m.speranza
 * @since 10-04-2024
 * @version 0.1
 */

public class PgConnectionManager extends ConnectionManager {
	private static final Logger logger = LogManager.getLogger(PgConnectionManager.class);
	
	static {
		ConnectionManager.registerDriver(new org.postgresql.Driver());
	}
}
