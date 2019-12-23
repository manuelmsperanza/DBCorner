package com.hoffnungland.db.corner.oracleconn;

import java.io.IOException;
import java.io.Reader;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import com.hoffnungland.db.corner.dbconn.ConnectionManager;

import oracle.xml.sql.dml.OracleXMLSave;
import oracle.xml.sql.query.OracleXMLQuery;

/**
 * Manage the connection with the Oracle database and the statements.
 * @author manuel.m.speranza
 * @since 05-05-2017
 * @version 0.1
 */
//TODO: WHERE ROWID = 'AAAW5TAAHAAEK8jAAA' AND ORA_ROWSCN = '14434883321763'

public class OrclConnectionManager extends ConnectionManager{

	private static final Logger logger = LogManager.getLogger(OrclConnectionManager.class);
	public static final String nslXsDateTimeFormat = "'YYYY-MM-DD\"T\"HH24:MI:SS'";
		
	static {
		ConnectionManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
	}
	
	/**
	 * Retrieve the next value of the sequence specified
	 * @param sequenceName The name of the sequence
	 * @return the next value
	 * @throws SQLException
	 * @author manuel.m.speranza
	 * @since 12-05-2017
	 */
	public long getNextVal(String sequenceName) throws SQLException{
		logger.traceEntry();
	
		PreparedStatement stm = this.prepareQuery(sequenceName, "SELECT " + sequenceName + ".NEXTVAL FROM DUAL").getStm();
		ResultSet rs = stm.executeQuery();
		rs.next();
		return logger.traceExit(rs.getLong(1));
	}
	
	/**
	 * Create clob
	 * @return
	 * @throws SQLException
	 * @author manuel.m.speranza
	 * @since 12-04-2018
	 */
	public Clob getClob() throws SQLException{
		return this.conn.createClob();
	}
	
	public Clob getXmlOfQuery(String selectStm) throws SQLException{
		logger.traceEntry();
		
		CallableStatement resStm = this.prepareInvoke("DBMS_XMLGEN.GETXML", "{? = call DBMS_XMLGEN.GETXML(?)}").getStm();
		
		resStm.registerOutParameter(1, java.sql.Types.CLOB);
		resStm.setString(2, selectStm);
		
		resStm.execute();
		
		Clob content = resStm.getClob(1);
		if(resStm.wasNull()){
			content = null;
		}
		
		return logger.traceExit(content);
		
	}
	
	public void xmlSave(Reader content, String tableName) throws SQLException {
		logger.traceEntry();
		
		String plsql = "DECLARE\r\n" + 
				"insCtx DBMS_XMLSTORE.ctxType;\r\n" + 
				"rows NUMBER;\r\n" + 
				//"xmlDoc CLOB := ?;\r\n" + 
				"BEGIN\r\n" + 
				"insCtx := DBMS_XMLSTORE.newContext(?);\r\n" + 
				"DBMS_XMLSTORE.clearUpdateColumnList(insCtx);\r\n" + 
				"? := DBMS_XMLSTORE.insertXML(insCtx, ?);\r\n" + 
				"DBMS_XMLSTORE.closeContext(insCtx);\r\n" + 
				"END;";
		
		CallableStatement cs = this.conn.prepareCall(plsql);
		cs.setString(1, tableName);
		cs.registerOutParameter(2, Types.INTEGER);
		cs.setClob(3, content);
		logger.trace("execute");
		cs.execute();

		logger.info(cs.getInt(2) + " rows inserted.");
		
		logger.traceExit();
	}
	
	/**
	 * Straight invoke of DBMS_XMLSAVE
	 * @param doc the xml to insert
	 * @param tableName the target table name
	 * @author manuel.m.speranza
	 * @since 27-04-2018
	 */
	public void xmlSave(Document doc, String tableName, int batchSize, int commitBatchSize) {
		logger.traceEntry();
		
		OracleXMLSave sav = new OracleXMLSave(this.conn, tableName);
		sav.collectTimingInfo(true);
		if(batchSize > 0) {
			sav.setBatchSize(batchSize);
		}
		if(commitBatchSize > 0) {
			sav.setCommitBatch(commitBatchSize);
		}
		sav.setDateFormat("dd/MM/yyyy HH:mm:ss");
		logger.trace("insertXML");
		sav.insertXML(doc);
		logger.trace("close");
		sav.close();
		
		logger.traceExit();
		
	}
	
	/**
	 * Straight invoke of DBMS_XMLSAVE
	 * @param doc the xml to insert
	 * @param tableName the target table name
	 * @author manuel.m.speranza
	 * @since 06-08-2019
	 */
	
	public void xmlSave(String xml, String tableName, int batchSize, int commitBatchSize) throws IOException {
		logger.traceEntry();
		
		OracleXMLSave sav = new OracleXMLSave(this.conn, tableName);
		if(batchSize > 0) {
			sav.setBatchSize(batchSize);
		}
		if(commitBatchSize > 0) {
			sav.setCommitBatch(commitBatchSize);
		}
		sav.setDateFormat("dd/MM/yyyy HH:mm:ss");
		sav.insertXML(xml);
		
		sav.close();
		
		logger.traceExit();
		
	}
	
	/**
	 * Straight invoke of DBMS_XMLQUERY
	 * @param query The statement used to extract data
	 * @return the w3c dom document containing the query result
	 * @author manuel.m.speranza
	 * @since 06-08-2019
	 */
	public Document xmlQueryDocument(String query) {
		logger.traceEntry();
		
		OracleXMLQuery que = new OracleXMLQuery(this.conn, query);
		que.setDateFormat("dd/MM/yyyy HH:mm:ss");
		
		return logger.traceExit(que.getXMLDOM());
	}
	
	/**
	 * Straight invoke of DBMS_XMLQUERY
	 * @param query The statement used to extract data
	 * @return the xml string containing the query result
	 * @author manuel.m.speranza
	 * @since 07-08-2019
	 */
	
	public String xmlQuery(String query) {
		logger.traceEntry();
		
		OracleXMLQuery que = new OracleXMLQuery(this.conn, query);
		que.setDateFormat("dd/MM/yyyy HH:mm:ss");
		
		return logger.traceExit(que.getXMLString());
	}
	
	/**
	 * Execute the truncate table
	 * @param tableName the name of the table to truncate
	 * @throws SQLException
	 * @since 23-12-2019
	 */
	public void truncateTable(String tableName) throws SQLException {
		logger.traceEntry();
		
		this.conn.prepareStatement("TRUNCATE TABLE " + tableName).execute();
		
		logger.traceExit();
	}

	
}
