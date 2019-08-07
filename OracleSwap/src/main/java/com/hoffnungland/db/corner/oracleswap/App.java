package com.hoffnungland.db.corner.oracleswap;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import com.hoffnungland.db.corner.oracleconn.OrclConnectionManager;

/**
 * Hello world!
 *
 */
public class App 
{
	private static final Logger logger = LogManager.getLogger(App.class);

	public static void main( String[] args )
	{
		logger.traceEntry();

		if(args.length < 3){
			logger.error("Wrong input parameters. Params are: SourceConnectionName TargetConnectionName TableName[ TableName]*");
			return;
		}

		String sourceConnectionName = args[0];
		String targetConnectionName = args[1];

		OrclConnectionManager sourceDbManager = new OrclConnectionManager();
		OrclConnectionManager targetDbManger = new OrclConnectionManager();

		try {
			logger.info("Source DB Manager connecting to " + sourceConnectionName);
			sourceDbManager.connect("./etc/connections/" + sourceConnectionName + ".properties");
			
			logger.info("Target DB Manager connecting to " + targetConnectionName);
			targetDbManger.connect("./etc/connections/" + targetConnectionName + ".properties");
			
			for(int argIdx = 2; argIdx < args.length; argIdx++) {
				String tableName = args[argIdx];
				logger.info("Getting " + tableName);
				Document tableDoc = sourceDbManager.xmlQueryDocument("SELECT * FROM " + tableName);
				
				CallableStatement replyStm = targetDbManger.getCallableStm("DELETE " + tableName);
				
				logger.info("Cleaning " + tableName);
				replyStm.execute();
				
				logger.info("Saving into " + tableName);
				targetDbManger.xmlSave(tableDoc, tableName, 0, 0);
				logger.info("Loading to " + tableName + " is completed");
			}
			
			targetDbManger.commit();
			
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		
		sourceDbManager.disconnect();
		targetDbManger.disconnect();

		logger.traceExit();
	}
}
