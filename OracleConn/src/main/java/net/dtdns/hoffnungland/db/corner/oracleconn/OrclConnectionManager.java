package net.dtdns.hoffnungland.db.corner.oracleconn;

import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

import net.dtdns.hoffnungland.db.corner.dbconn.ConnectionManager;
import oracle.xml.sql.dml.OracleXMLSave;

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
	/**
	 * Straight invoke of DBMS_XMLSAVE
	 * @param doc the xml to insert
	 * @param tableName the target table name
	 * @author manuel.m.speranza
	 * @since 27-04-2018
	 */
	public void xmlSave(Document doc, String tableName) {
		logger.traceEntry();
		
		OracleXMLSave sav = new OracleXMLSave(this.conn, tableName);
		sav.setDateFormat("dd/MM/yyyy HH:mm:ss");
		sav.insertXML(doc);
		
		sav.close();
		
		logger.traceExit();
		
	}
	
}
