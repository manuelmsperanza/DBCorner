package com.hoffnungland.db.corner.h2dbconn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hoffnungland.db.corner.dbconn.ConnectionManager;

/**
 * Manages H2 database connections.
 */
public class H2ConnectionManager extends ConnectionManager {
	
	private static final Logger logger = LogManager.getLogger(H2ConnectionManager.class);
	
	static {
		ConnectionManager.registerDriver(new org.h2.Driver());
	}
}
