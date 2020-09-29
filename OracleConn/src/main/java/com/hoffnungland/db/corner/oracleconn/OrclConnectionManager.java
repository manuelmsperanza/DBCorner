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
 * @version 0.2
 */
//TODO: WHERE ROWID = 'AAAW5TAAHAAEK8jAAA' AND ORA_ROWSCN = '14434883321763'

public class OrclConnectionManager extends ConnectionManager{

	private static final Logger logger = LogManager.getLogger(OrclConnectionManager.class);
	public static final String nslXsDateTimeFormat = "'YYYY-MM-DD\"T\"HH24:MI:SS'";
	
	private CallableStatement xmlGenStm = null;
	
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
		
		if(this.xmlGenStm == null) {
			this.xmlGenStm = this.prepareInvoke("DBMS_XMLGEN.GETXML", "{? = call DBMS_XMLGEN.GETXML(?)}").getStm();
		}
		this.xmlGenStm.registerOutParameter(1, java.sql.Types.CLOB);
		this.xmlGenStm.setString(2, selectStm);
		
		this.xmlGenStm.execute();
		
		Clob content = this.xmlGenStm.getClob(1);
		if(this.xmlGenStm.wasNull()){
			content = null;
		}
		
		return logger.traceExit(content);
		
	}
	
	public Clob getFullXmlOfQuery(String selectStm) throws SQLException{
		logger.traceEntry();
		
		String plsql = "DECLARE\r\n" + 
				"readCtx DBMS_XMLGEN.ctxType;\r\n" + 
				"rows NUMBER;\r\n" + 
				//"xmlDoc CLOB := ?;\r\n" + 
				"BEGIN\r\n" + 
				"readCtx := DBMS_XMLGEN.newContext(?);\r\n" + 
				"DBMS_XMLGEN.SETNULLHANDLING(readCtx, DBMS_XMLGEN.NULL_ATTR);\r\n" + 
				"? := DBMS_XMLGEN.GETXML(readCtx);\r\n" + 
				"DBMS_XMLSTORE.closeContext(readCtx);\r\n" + 
				"END;";
		
		CallableStatement cs = this.conn.prepareCall(plsql);
		cs.setString(1, selectStm);
		cs.registerOutParameter(2, java.sql.Types.CLOB);
		logger.trace("execute");
		cs.execute();		
		Clob content = cs.getClob(2);
		if(cs.wasNull()){
			content = null;
		}
		cs.close();
		
		return logger.traceExit(content);
	}
	
	public int xmlSave(Reader content, String tableName) throws SQLException {
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
		int rowcount = cs.getInt(2);
		logger.info(rowcount + " rows inserted.");
		cs.close();
		
		return logger.traceExit(rowcount);
	}
	
	public int xmlUpdate(Reader content, String tableName, String columnKey) throws SQLException {
		logger.traceEntry();
		
		String plsql = "DECLARE\r\n" + 
				"updCtx DBMS_XMLSTORE.ctxType;\r\n" + 
				"rows NUMBER;\r\n" + 
				//"xmlDoc CLOB := ?;\r\n" + 
				"BEGIN\r\n" + 
				"updCtx := DBMS_XMLSTORE.newContext(?);\r\n" + 
				"DBMS_XMLSTORE.clearUpdateColumnList(updCtx);\r\n" +
				"DBMS_XMLSTORE.setKeyColumn(updCtx,?);\r\n" +
				"? := DBMS_XMLSTORE.updateXML(updCtx, ?);\r\n" + 
				"DBMS_XMLSTORE.closeContext(updCtx);\r\n" + 
				"END;";
		
		CallableStatement cs = this.conn.prepareCall(plsql);
		cs.setString(1, tableName);
		cs.setString(2, columnKey);
		cs.registerOutParameter(3, Types.INTEGER);
		cs.setClob(4, content);
		logger.trace("execute");
		cs.execute();
		int rowcount = cs.getInt(3);
		logger.info(rowcount + " rows updated.");
		cs.close();
		
		return logger.traceExit(rowcount);
	}
	
	public int xmlFullUpdate(Reader content, String tableName, String columnKey) throws SQLException {
		logger.traceEntry();
		
		String plsql = "DECLARE\r\n" + 
				"updCtx DBMS_XMLSTORE.ctxType;\r\n" + 
				"rows NUMBER;\r\n" + 
				"tablename VARCHAR2(30);\r\n" +
				"keyColumn VARCHAR2(30);\r\n" + 
				"BEGIN\r\n" +
				"tablename := ?;" + 
				"updCtx := DBMS_XMLSTORE.newContext(tablename);\r\n" + 
				"DBMS_XMLSTORE.clearUpdateColumnList(updCtx);\r\n" +
				"keyColumn := ?;\r\n" + 
				"DBMS_XMLSTORE.setKeyColumn(updCtx,keyColumn);\r\n" +
				"for cur_col in (select column_name from cols where table_name = tablename and column_name <> keyColumn) loop\r\n" + 
				"DBMS_XMLSTORE.setUpdateColumn(updCtx, cur_col.column_name);\r\n" +
				"end loop;\r\n" +
				"? := DBMS_XMLSTORE.updateXML(updCtx, ?);\r\n" + 
				"DBMS_XMLSTORE.closeContext(updCtx);\r\n" + 
				"END;";
		
		CallableStatement cs = this.conn.prepareCall(plsql);
		cs.setString(1, tableName);
		cs.setString(2, columnKey);
		cs.registerOutParameter(3, Types.INTEGER);
		cs.setClob(4, content);
		logger.trace("execute");
		cs.execute();
		int rowcount = cs.getInt(3);
		logger.info(rowcount + " rows updated.");
		cs.close();
		
		return logger.traceExit(rowcount);
	}
	
	public int xmlDelete(Reader content, String tableName, String columnKey) throws SQLException {
		logger.traceEntry();
		
		String plsql = "DECLARE\r\n" + 
				"delCtx DBMS_XMLSTORE.ctxType;\r\n" + 
				"rows NUMBER;\r\n" + 
				//"xmlDoc CLOB := ?;\r\n" + 
				"BEGIN\r\n" + 
				"delCtx := DBMS_XMLSTORE.newContext(?);\r\n" + 
				"DBMS_XMLSTORE.clearUpdateColumnList(delCtx);\r\n" +
				"DBMS_XMLSTORE.setKeyColumn(delCtx,?);\r\n" +
				"? := DBMS_XMLSTORE.deleteXML(delCtx, ?);\r\n" + 
				"DBMS_XMLSTORE.closeContext(delCtx);\r\n" + 
				"END;";
		
		CallableStatement cs = this.conn.prepareCall(plsql);
		cs.setString(1, tableName);
		cs.setString(2, columnKey);
		cs.registerOutParameter(3, Types.INTEGER);
		cs.setClob(4, content);
		logger.trace("execute");
		cs.execute();
		int rowcount = cs.getInt(3);
		logger.info(rowcount + " rows deleted.");
		cs.close();
		
		return logger.traceExit(rowcount);
	}
	
	/**
	 * Straight invoke of DBMS_XMLSAVE
	 * @param doc the xml to insert
	 * @param tableName the target table name
	 * @author manuel.m.speranza
	 * @since 27-04-2018
	 */
	public void xmlSave(Document doc, String tableName, String[] updateColumnList, int batchSize, int commitBatchSize) {
		logger.traceEntry();
		
		OracleXMLSave sav = new OracleXMLSave(this.conn, tableName);
		sav.collectTimingInfo(true);
		if(batchSize > 0) {
			sav.setBatchSize(batchSize);
		}
		if(commitBatchSize > 0) {
			sav.setCommitBatch(commitBatchSize);
		}
		if(updateColumnList != null) {
			sav.setUpdateColumnList(updateColumnList);
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
	
	public void xmlSave(String xml, String tableName, String[] updateColumnList, int batchSize, int commitBatchSize) throws IOException {
		logger.traceEntry();
		
		OracleXMLSave sav = new OracleXMLSave(this.conn, tableName);
		if(batchSize > 0) {
			sav.setBatchSize(batchSize);
		}
		if(commitBatchSize > 0) {
			sav.setCommitBatch(commitBatchSize);
		}
		if(updateColumnList != null) {
			sav.setUpdateColumnList(updateColumnList);
		}
		sav.setDateFormat("dd/MM/yyyy HH:mm:ss");
		logger.trace("insertXML");
		sav.insertXML(xml);
		logger.trace("close");
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
	
	
	/**
	 * Straight invoke of DBMS_XMLSAVE
	 * @param doc the xml to update
	 * @param tableName the target table name
	 * @author manuel.m.speranza
	 * @since 19-06-2020
	 */
	public void xmlUpdate(Document doc, String tableName, String[] keyColumnList, String[] updateColumnList, int batchSize, int commitBatchSize) {
		logger.traceEntry();
		
		OracleXMLSave sav = new OracleXMLSave(this.conn, tableName);
		sav.collectTimingInfo(true);
		if(batchSize > 0) {
			sav.setBatchSize(batchSize);
		}
		if(commitBatchSize > 0) {
			sav.setCommitBatch(commitBatchSize);
		}
		if(updateColumnList != null) {
			sav.setUpdateColumnList(updateColumnList);
		}
		
		sav.setKeyColumnList(keyColumnList);
		
		sav.setDateFormat("dd/MM/yyyy HH:mm:ss");
		logger.trace("updateXML");
		sav.updateXML(doc);
		logger.trace("close");
		sav.close();
		
		logger.traceExit();
		
	}
	
	/**
	 * Straight invoke of DBMS_XMLSAVE
	 * @param doc the xml to update
	 * @param tableName the target table name
	 * @author manuel.m.speranza
	 * @since 19-06-2020
	 */
	
	public void xmlUpdate(String xml, String tableName, String[] keyColumnList, String[] updateColumnList, int batchSize, int commitBatchSize) throws IOException {
		logger.traceEntry();
		
		OracleXMLSave sav = new OracleXMLSave(this.conn, tableName);
		if(batchSize > 0) {
			sav.setBatchSize(batchSize);
		}
		if(commitBatchSize > 0) {
			sav.setCommitBatch(commitBatchSize);
		}
		
		if(updateColumnList != null) {
			sav.setUpdateColumnList(updateColumnList);
		}
		
		sav.setKeyColumnList(keyColumnList);
		
		sav.setDateFormat("dd/MM/yyyy HH:mm:ss");
		logger.trace("updateXML");
		sav.updateXML(xml);
		
		logger.trace("close");
		sav.close();
		
		logger.traceExit();
		
	}
	
}
