package com.hoffnungland.db.corner.oracleswap;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import com.hoffnungland.db.corner.oracleconn.OrclConnectionManager;

public class TestInventoryGroupExport {

	private static final Logger logger = LogManager.getLogger(TestInventoryGroupExport.class);

	public static void main( String[] args )
	{
		logger.traceEntry();

		
		if(args.length < 3){
			logger.error("Wrong input parameters. Params are: SourceConnectionName TargetConnectionName");
			return;
		}

		String sourceConnectionName = args[0];
		String targetConnectionName = args[1];
		
		//String sourceConnectionName = "***REMOVED***";
		//String targetConnectionName = "***REMOVED***";

		OrclConnectionManager sourceDbManager = new OrclConnectionManager();
		OrclConnectionManager targetDbManger = new OrclConnectionManager();

		try {
			logger.info("Source DB Manager connecting to " + sourceConnectionName);
			sourceDbManager.connect("./etc/connections/" + sourceConnectionName + ".properties");
			
			CallableStatement sourceSessionTimestampStm = sourceDbManager.getCallableStm("ALTER SESSION SET NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SSXFF'");
			sourceSessionTimestampStm.execute();
			
			logger.info("Target DB Manager connecting to " + targetConnectionName);
			targetDbManger.connect("./etc/connections/" + targetConnectionName + ".properties");
			
			CallableStatement targetSessionTimestampStm = targetDbManger.getCallableStm("ALTER SESSION SET NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SSXFF'");
			targetSessionTimestampStm.execute();
			{
				String tableName = "INVENTORYGROUP";
				logger.info("Getting " + tableName);
				//Document tableDoc = sourceDbManager.xmlQuery("SELECT * FROM " + tableName);

				Document tableDoc = sourceDbManager.xmlQuery("select ENTITYID, " + 
						"ENTITYCLASS, " + 
						"to_char(CREATEDDATE, 'YYYY-MM-DD HH24:MI:SSXFF') CREATEDDATE, " + 
						"CREATEDUSER, " + 
						"DESCRIPTION, " + 
						"to_char(ENDDATE, 'YYYY-MM-DD HH24:MI:SSXFF') ENDDATE, " + 
						"ENTITYVERSION, " + 
						"to_char(LASTMODIFIEDDATE, 'YYYY-MM-DD HH24:MI:SSXFF') LASTMODIFIEDDATE, " + 
						"LASTMODIFIEDUSER, " + 
						"NAME, " + 
						"NOSPEC, " + 
						"OWNER, " + 
						"PARTITION, " + 
						"PERMISSIONS, " + 
						"to_char(STARTDATE, 'YYYY-MM-DD HH24:MI:SSXFF') STARTDATE, " + 
						"SPECIFICATION " + 
						"from INVENTORYGROUP");

				CallableStatement replyStm = targetDbManger.getCallableStm("DELETE " + tableName);

				logger.info("Cleaning " + tableName);
				replyStm.execute();
				targetDbManger.commit();
				
				logger.info("Saving into " + tableName);
				targetDbManger.xmlSave(tableDoc, tableName, 100, 500);
				logger.info("Loading to " + tableName + " is completed");
				targetDbManger.commit();
			}
			
			{
				String tableName = "INVENTORYGROUP_CHAR";
				logger.info("Getting " + tableName);
				//Document tableDoc = sourceDbManager.xmlQuery("SELECT * FROM " + tableName);

				Document tableDoc = sourceDbManager.xmlQuery("select ENTITYID, " + 
						"ENTITYCLASS, " + 
						"to_char(CREATEDDATE, 'YYYY-MM-DD HH24:MI:SSXFF') CREATEDDATE, " + 
						"CREATEDUSER, " + 
						"to_char(ENDDATE, 'YYYY-MM-DD HH24:MI:SSXFF') ENDDATE, " + 
						"ENTITYVERSION, " + 
						"LABEL, " + 
						"to_char(LASTMODIFIEDDATE, 'YYYY-MM-DD HH24:MI:SSXFF') LASTMODIFIEDDATE, " + 
						"LASTMODIFIEDUSER, " + 
						"NAME, " + 
						"to_char(STARTDATE, 'YYYY-MM-DD HH24:MI:SSXFF') STARTDATE, " + 
						"VALUE, " + 
						"CHAROWNER, " + 
						"CHARACTERISTICSPECIFICATION " + 
						"from INVENTORYGROUP_CHAR");

				CallableStatement replyStm = targetDbManger.getCallableStm("DELETE " + tableName);

				logger.info("Cleaning " + tableName);
				replyStm.execute();
				targetDbManger.commit();
				
				logger.info("Saving into " + tableName);
				targetDbManger.xmlSave(tableDoc, tableName, 100, 500);
				logger.info("Loading to " + tableName + " is completed");
				targetDbManger.commit();
			}

			


		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} finally {
			logger.debug("sourceDbManager disconect");
			sourceDbManager.disconnect();
			logger.debug("targetDbManger disconect");
			targetDbManger.disconnect();
		}

		logger.traceExit();
	}
}
