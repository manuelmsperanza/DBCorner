package com.hoffnungland.db.corner.javadbconn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hoffnungland.db.corner.dbconn.ConnectionManager;

/**
 * Manage the connection with the local Derby Java database.
 */
public class JdbcLocalManager extends JdbcConnectionManager {
	
	private static final Logger logger = LogManager.getLogger(JdbcLocalManager.class);
	
	static {
		ConnectionManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());
	}
}
