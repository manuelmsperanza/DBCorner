package com.hoffnungland.db.corner.h2dbconn;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main application class for H2 database connection.
 */
public class App 
{
	private static final Logger logger = LogManager.getLogger(App.class);
	
	/**
	 * Main method to run the application.
	 * @param args Command line arguments
	 */
	public static void main( String[] args )
    {
    	Properties connectionProps = new Properties();
		connectionProps.put("user", "h2dbuser");
		connectionProps.put("password", "h2dbfilePasswd h2dbuserPasswd");

		String dbms = "h2";
		String dbName = "~/h2dbEnc;CIPHER=AES";

		String urlString = "jdbc:" + dbms + ":" + dbName;
		connectionProps.put("URL", urlString);
		
		H2ConnectionManager dbManager = null;
		try{
		    dbManager = new H2ConnectionManager();
			dbManager.connect(urlString, connectionProps);
			
			CallableStatement stmSet = dbManager.getCallableStatement("ddl/tables.sql").getStm();
			stmSet.execute();
			
			CallableStatement stmCompact = dbManager.getCallableStm("SHUTDOWN COMPACT");
			stmCompact.execute();
			
			
		} catch (SQLException | IOException e) {
			logger.error(e.getMessage(), e);
		}finally {
			if(dbManager != null){
				dbManager.disconnect();
				dbManager.close();
			}
		}
		
    }
}
