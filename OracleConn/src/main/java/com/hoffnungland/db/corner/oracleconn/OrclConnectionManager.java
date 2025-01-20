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
 * Manages the connection with the Oracle database and the statements.
 * Extends ConnectionManager.
 * @author manuel.m.speranza
 * @since 05-05-2017
 * @version 0.2
 */
public class OrclConnectionManager extends ConnectionManager {

	private static final Logger logger = LogManager.getLogger(OrclConnectionManager.class);
	public static final String nslXsDateTimeFormat = "'YYYY-MM-DD\"T\"HH24:MI:SS'";
	
	private CallableStatement xmlGenStm = null;
	
	static {
		ConnectionManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
	}
	
	/**
	 * Retrieves the next value of the specified sequence.
	 * @param sequenceName the name of the sequence.
	 * @return the next value.
	 * @throws SQLException if a database access error occurs.
	 * @author manuel.m.speranza
	 * @since 12-05-2017
	 */
	public long getNextVal(String sequenceName) throws SQLException {
		logger.traceEntry();
		PreparedStatement stm = this.prepareQuery(sequenceName, "SELECT " + sequenceName + ".NEXTVAL FROM DUAL").getStm();
		ResultSet rs = stm.executeQuery();
		rs.next();
		return logger.traceExit(rs.getLong(1));
	}
	
	/**
	 * Creates a CLOB.
	 * @return a new CLOB.
	 * @throws SQLException if a database access error occurs.
	 * @author manuel.m.speranza
	 * @since 12-04-2018
	 */
	public Clob getClob() throws SQLException {
		return this.conn.createClob();
	}
	
	/**
	 * Retrieves the XML of a query as a CLOB.
	 * @param selectStm the select statement.
	 * @return the XML content as a CLOB.
	 * @throws SQLException if a database access error occurs.
	 */
	public Clob getXmlOfQuery(String selectStm) throws SQLException {
		logger.traceEntry();
		if (this.xmlGenStm == null) {
			this.xmlGenStm = this.prepareInvoke("DBMS_XMLGEN.GETXML", "{? = call DBMS_XMLGEN.GETXML(?)}").getStm();
		}
		this.xmlGenStm.registerOutParameter(1, java.sql.Types.CLOB);
		this.xmlGenStm.setString(2, selectStm);
		this.xmlGenStm.execute();
		Clob content = this.xmlGenStm.getClob(1);
		if (this.xmlGenStm.wasNull()) {
			content = null;
		}
		return logger.traceExit(content);
	}
	
	/**
	 * Retrieves the full XML of a query as a CLOB.
	 * @param selectStm the select statement.
	 * @return the full XML content as a CLOB.
	 * @throws SQLException if a database access error occurs.
	 */
	public Clob getFullXmlOfQuery(String selectStm) throws SQLException {
		logger.traceEntry();
		String plsql = "DECLARE\n" + 
				"readCtx DBMS_XMLGEN.ctxType;\n" + 
				"rows NUMBER;\n" + 
				"BEGIN\n" + 
				"readCtx := DBMS_XMLGEN.newContext(?);\n" + 
				"DBMS_XMLGEN.SETNULLHANDLING(readCtx, DBMS_XMLGEN.NULL_ATTR);\n" + 
				"? := DBMS_XMLGEN.GETXML(readCtx);\n" + 
				"DBMS_XMLSTORE.closeContext(readCtx);\n" + 
				"END;";
		CallableStatement cs = this.conn.prepareCall(plsql);
		cs.setString(1, selectStm);
		cs.registerOutParameter(2, java.sql.Types.CLOB);
		logger.trace("execute");
		cs.execute();		
		Clob content = cs.getClob(2);
		if (cs.wasNull()) {
			content = null;
		}
		cs.close();
		return logger.traceExit(content);
	}
	
	/**
	 * Saves XML content to a table.
	 * @param content the XML content as a Reader.
	 * @param tableName the target table name.
	 * @return the number of rows inserted.
	 * @throws SQLException if a database access error occurs.
	 */
	public int xmlSave(Reader content, String tableName) throws SQLException {
		logger.traceEntry();
		String plsql = "DECLARE\n" + 
				"insCtx DBMS_XMLSTORE.ctxType;\n" + 
				"rows NUMBER;\n" + 
				"BEGIN\n" + 
				"insCtx := DBMS_XMLSTORE.newContext(?);\n" + 
				"DBMS_XMLSTORE.clearUpdateColumnList(insCtx);\n" + 
				"? := DBMS_XMLSTORE.insertXML(insCtx, ?);\n" + 
				"DBMS_XMLSTORE.closeContext(insCtx);\n" + 
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
	
	/**
	 * Updates XML content in a table.
	 * @param content the XML content as a Reader.
	 * @param tableName the target table name.
	 * @param columnKey the key column.
	 * @param columnList the list of columns to update.
	 * @return the number of rows updated.
	 * @throws SQLException if a database access error occurs.
	 */
	public int xmlUpdate(Reader content, String tableName, String columnKey, String[] columnList) throws SQLException {
		logger.traceEntry();
		StringBuilder columnListString = null;
		if (columnList != null) {
			columnListString = new StringBuilder();
			for (String curColumnName : columnList) {
				columnListString.append("DBMS_XMLSTORE.setUpdateColumn(updCtx, '" + curColumnName + "');\n");
			}
		}
		String plsql = "DECLARE\n" + 
				"updCtx DBMS_XMLSTORE.ctxType;\n" + 
				"rows NUMBER;\n" + 
				"BEGIN\n" + 
				"updCtx := DBMS_XMLSTORE.newContext(?);\n" + 
				"DBMS_XMLSTORE.clearUpdateColumnList(updCtx);\n" +
				"DBMS_XMLSTORE.setKeyColumn(updCtx,?);\n" +
				((columnListString == null) ? "" : columnListString.toString()) +
				"? := DBMS_XMLSTORE.updateXML(updCtx, ?);\n" + 
				"DBMS_XMLSTORE.closeContext(updCtx);\n" + 
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
	
	/**
	 * Performs a full update of XML content in a table.
	 * @param content the XML content as a Reader.
	 * @param tableName the target table name.
	 * @param columnKey the key column.
	 * @return the number of rows updated.
	 * @throws SQLException if a database access error occurs.
	 */
	public int xmlFullUpdate(Reader content, String tableName, String columnKey) throws SQLException {
		logger.traceEntry();
		String plsql = "DECLARE\n" + 
				"updCtx DBMS_XMLSTORE.ctxType;\n" + 
				"rows NUMBER;\n" + 
				"tablename VARCHAR2(30);\n" +
				"keyColumn VARCHAR2(30);\n" + 
				"BEGIN\n" +
				"tablename := ?;" + 
				"updCtx := DBMS_XMLSTORE.newContext(tablename);\n" + 
				"DBMS_XMLSTORE.clearUpdateColumnList(updCtx);\n" +
				"keyColumn := ?;\n" + 
				"DBMS_XMLSTORE.setKeyColumn(updCtx,keyColumn);\n" +
				"for cur_col in (select column_name from cols where table_name = tablename and column_name <> keyColumn) loop\n" + 
				"DBMS_XMLSTORE.setUpdateColumn(updCtx, cur_col.column_name);\n" +
				"end loop;\n" +
				"? := DBMS_XMLSTORE.updateXML(updCtx, ?);\n" + 
				"DBMS_XMLSTORE.closeContext(updCtx);\n" + 
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
	
	/**
	 * Deletes XML content from a table.
	 * @param content the XML content as a Reader.
	 * @param tableName the target table name.
	 * @param columnKey the key column.
	 * @return the number of rows deleted.
	 * @throws SQLException if a database access error occurs.
	 */
	public int xmlDelete(Reader content, String tableName, String columnKey) throws SQLException {
		logger.traceEntry();
		String plsql = "DECLARE\n" + 
				"delCtx DBMS_XMLSTORE.ctxType;\n" + 
				"rows NUMBER;\n" + 
				"BEGIN\n" + 
				"delCtx := DBMS_XMLSTORE.newContext(?);\n" + 
				"DBMS_XMLSTORE.clearUpdateColumnList(delCtx);\n" +
				"DBMS_XMLSTORE.setKeyColumn(delCtx,?);\n" +
				"? := DBMS_XMLSTORE.deleteXML(delCtx, ?);\n" + 
				"DBMS_XMLSTORE.closeContext(delCtx);\n" + 
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
	 * Saves XML content to a table using DBMS_XMLSAVE.
	 * @param doc the XML document to insert.
	 * @param tableName the target table name.
	 * @param updateColumnList the list of columns to update.
	 * @param batchSize the batch size.
	 * @param commitBatchSize the commit batch size.
	 * @author manuel.m.speranza
	 * @since 27-04-2018
	 */
	public void xmlSave(Document doc, String tableName, String[] updateColumnList, int batchSize, int commitBatchSize) {
		logger.traceEntry();
		OracleXMLSave sav = new OracleXMLSave(this.conn, tableName);
		sav.collectTimingInfo(true);
		if (batchSize > 0) {
			sav.setBatchSize(batchSize);
		}
		if (commitBatchSize > 0) {
			sav.setCommitBatch(commitBatchSize);
		}
		if (updateColumnList != null) {
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
	 * Saves XML content to a table using DBMS_XMLSAVE.
	 * @param xml the XML content as a string.
	 * @param tableName the target table name.
	 * @param updateColumnList the list of columns to update.
	 * @param batchSize the batch size.
	 * @param commitBatchSize the commit batch size.
	 * @throws IOException if an I/O error occurs.
	 * @author manuel.m.speranza
	 * @since 06-08-2019
	 */
	public void xmlSave(String xml, String tableName, String[] updateColumnList, int batchSize, int commitBatchSize) throws IOException {
		logger.traceEntry();
		OracleXMLSave sav = new OracleXMLSave(this.conn, tableName);
		if (batchSize > 0) {
			sav.setBatchSize(batchSize);
		}
		if (commitBatchSize > 0) {
			sav.setCommitBatch(commitBatchSize);
		}
		if (updateColumnList != null) {
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
	 * Executes a query and returns the result as a DOM document.
	 * @param query the query statement.
	 * @return the result as a DOM document.
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
	 * Executes a query and returns the result as an XML string.
	 * @param query the query statement.
	 * @return the result as an XML string.
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
	 * Executes the truncate table statement.
	 * @param tableName the name of the table to truncate.
	 * @throws SQLException if a database access error occurs.
	 * @since 23-12-2019
	 */
	public void truncateTable(String tableName) throws SQLException {
		logger.traceEntry();
		this.conn.prepareStatement("TRUNCATE TABLE " + tableName).execute();
		logger.traceExit();
	}
	
	/**
	 * Updates XML content in a table using DBMS_XMLSAVE.
	 * @param doc the XML document to update.
	 * @param tableName the target table name.
	 * @param keyColumnList the list of key columns.
	 * @param updateColumnList the list of columns to update.
	 * @param batchSize the batch size.
	 * @param commitBatchSize the commit batch size.
	 * @author manuel.m.speranza
	 * @since 19-06-2020
	 */
	public void xmlUpdate(Document doc, String tableName, String[] keyColumnList, String[] updateColumnList, int batchSize, int commitBatchSize) {
		logger.traceEntry();
		OracleXMLSave sav = new OracleXMLSave(this.conn, tableName);
		sav.collectTimingInfo(true);
		if (batchSize > 0) {
			sav.setBatchSize(batchSize);
		}
		if (commitBatchSize > 0) {
			sav.setCommitBatch(commitBatchSize);
		}
		if (updateColumnList != null) {
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
	 * Updates XML content in a table using DBMS_XMLSAVE.
	 * @param xml the XML content as a string.
	 * @param tableName the target table name.
	 * @param keyColumnList the list of key columns.
	 * @param updateColumnList the list of columns to update.
	 * @param batchSize the batch size.
	 * @param commitBatchSize the commit batch size.
	 * @throws IOException if an I/O error occurs.
	 * @author manuel.m.speranza
	 * @since 19-06-2020
	 */
	public void xmlUpdate(String xml, String tableName, String[] keyColumnList, String[] updateColumnList, int batchSize, int commitBatchSize) throws IOException {
		logger.traceEntry();
		OracleXMLSave sav = new OracleXMLSave(this.conn, tableName);
		if (batchSize > 0) {
			sav.setBatchSize(batchSize);
		}
		if (commitBatchSize > 0) {
			sav.setCommitBatch(commitBatchSize);
		}
		if (updateColumnList != null) {
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
