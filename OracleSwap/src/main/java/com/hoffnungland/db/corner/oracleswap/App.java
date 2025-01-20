package com.hoffnungland.db.corner.oracleswap;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.hoffnungland.db.corner.oracleconn.OrclConnectionManager;

/**
 * Main application class for OracleSwap.
 */
public class App 
{
	private static final Logger logger = LogManager.getLogger(App.class);

	/**
	 * Main method to run the OracleSwap application.
	 * @param args Command line arguments: SourceConnectionName TargetConnectionName TableName[ TableName]*
	 */
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
			
			CallableStatement sourceSessionNumericStm = sourceDbManager.getCallableStm("ALTER SESSION SET NLS_NUMERIC_CHARACTERS = '.,'");
			sourceSessionNumericStm.execute();
			CallableStatement sourceSessionDateStm = sourceDbManager.getCallableStm("ALTER SESSION SET NLS_DATE_FORMAT = 'DD/MM/YYYY HH24:MI:SS'");
			sourceSessionDateStm.execute();
			CallableStatement sourceSessionTimestampStm = sourceDbManager.getCallableStm("ALTER SESSION SET NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SSXFF'");
			sourceSessionTimestampStm.execute();
			CallableStatement sourceSessionTimestampTzStm = sourceDbManager.getCallableStm("ALTER SESSION SET NLS_TIMESTAMP_TZ_FORMAT = 'YYYY-MM-DD HH24:MI:SSXFF TZR'");
			sourceSessionTimestampTzStm.execute();
			
			logger.info("Target DB Manager connecting to " + targetConnectionName);
			targetDbManger.connect("./etc/connections/" + targetConnectionName + ".properties");
			
			CallableStatement targetSessionNumericStm = targetDbManger.getCallableStm("ALTER SESSION SET NLS_NUMERIC_CHARACTERS = '.,'");
			targetSessionNumericStm.execute();
			CallableStatement targetSessionDateStm = targetDbManger.getCallableStm("ALTER SESSION SET NLS_DATE_FORMAT = 'DD/MM/YYYY HH24:MI:SS'");
			targetSessionDateStm.execute();
			CallableStatement targetSessionTimestampStm = targetDbManger.getCallableStm("ALTER SESSION SET NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SSXFF'");
			targetSessionTimestampStm.execute();
			CallableStatement targetSessionTimestampTzStm = targetDbManger.getCallableStm("ALTER SESSION SET NLS_TIMESTAMP_TZ_FORMAT = 'YYYY-MM-DD HH24:MI:SSXFF TZR'");
			targetSessionTimestampTzStm.execute();
			
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
			for(int argIdx = 2; argIdx < args.length; argIdx++) {
				String tableName = args[argIdx];
				logger.info("Getting " + tableName);
				Clob inContent = sourceDbManager.getXmlOfQuery("SELECT * FROM " + tableName);
				
				if (inContent != null){
					
					logger.debug("Convert XML to DOM");
					Document tableDoc = docBuilder.parse(new InputSource(inContent.getCharacterStream()));
					logger.debug(tableDoc.getNodeValue());
					CallableStatement replyStm = targetDbManger.getCallableStm("DELETE " + tableName);
					
					logger.info("Cleaning " + tableName);
					replyStm.execute();
					
					logger.info("Saving into " + tableName);
					targetDbManger.xmlSave(inContent.getCharacterStream(), tableName);
					logger.info("Loading to " + tableName + " is completed");
				}
			}
			
			targetDbManger.commit();
			
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			logger.error(e.getMessage(), e);
		} catch (SAXException e) {
			logger.error(e.getMessage(), e);
		} finally {
			logger.debug("sourceDbManager disconnect");
			sourceDbManager.disconnect();
			logger.debug("targetDbManger disconnect");
			targetDbManger.disconnect();
		}
		logger.traceExit();
	}
}
