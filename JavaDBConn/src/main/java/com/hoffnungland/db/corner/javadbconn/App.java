package com.hoffnungland.db.corner.javadbconn;

import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Hello world!
 */
public class App 
{
	private static final Logger logger = LogManager.getLogger(App.class);


	public static void main( String[] args )
	{
		Properties connectionProps = new Properties();
		connectionProps.put("user", "");
		connectionProps.put("password", "");
		//connectionProps.put("derby.language.sequence.preallocator", "1");

		String dbms = "derby";
		String dbName = "db/testJavaDBConn";
		//java -jar %DERBY_HOME%\lib\derbyrun.jar ij
		//CONNECT 'jdbc:derby:C:/eclipse-jee-mars-2-win32-x86_64/workspace/DBCorner/JavaDBConn/testJavaDBConn jdbc:derby:testJavaDBConn;create=true';
		//DISCONNECT;
		//EXIT;
		//String urlString = "jdbc:" + dbms + ":" + dbName + ";create=true";
		String urlString = "jdbc:" + dbms + ":" + dbName;
		connectionProps.put("URL", urlString);
		
		JdbLocalManager dbManager = null;
		try {
			dbManager = new JdbLocalManager();
			dbManager.connect(urlString, connectionProps);
			
			long nOrdAmmVal = dbManager.getNextVal("DUMMY_SEQ");
			logger.debug("N_ORD_AMM_VAL: " + nOrdAmmVal);
			
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}finally {
			if(dbManager != null){
				dbManager.disconnect();
			}
		}
	}
}
