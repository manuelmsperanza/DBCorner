package com.hoffnungland.db.corner.oracleswap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.hoffnungland.db.corner.oracleconn.OrclConnectionManager;

public class TestInventoryGroupExport {

	private static final Logger logger = LogManager.getLogger(TestInventoryGroupExport.class);

	public static void main( String[] args )
	{
		logger.traceEntry();

		
		if(args.length < 2){
			logger.error("Wrong input parameters. Params are: SourceConnectionName TargetConnectionName");
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
			
			{
				String tableName = "INVENTORYGROUP";
				logger.info("Getting " + tableName);
				
				Clob inContent = sourceDbManager.getXmlOfQuery("SELECT * FROM " + tableName);
				
				if (inContent != null){
					
					CallableStatement replyStm = targetDbManger.getCallableStm("DELETE " + tableName);
					logger.info("Cleaning " + tableName);
					replyStm.execute();
					targetDbManger.commit();
					
					logger.info("Saving into " + tableName);
					targetDbManger.xmlSave(inContent.getCharacterStream(), tableName);
					logger.info("Loading to " + tableName + " is completed");
					targetDbManger.commit();
					inContent.free();
				}
				
				
			}
			
			{
				String tableName = "INVENTORYGROUP_CHAR";
				logger.info("Getting " + tableName);
				
				Clob inContent = sourceDbManager.getXmlOfQuery("SELECT * FROM " + tableName);
				
				if (inContent != null){
					
					CallableStatement replyStm = targetDbManger.getCallableStm("DELETE " + tableName);
					logger.info("Cleaning " + tableName);
					replyStm.execute();
					targetDbManger.commit();
					
					logger.info("Saving into " + tableName);
					targetDbManger.xmlSave(inContent.getCharacterStream(), tableName);
					logger.info("Loading to " + tableName + " is completed");
					targetDbManger.commit();
					inContent.free();
				}
			}

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (SQLException e) {
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
