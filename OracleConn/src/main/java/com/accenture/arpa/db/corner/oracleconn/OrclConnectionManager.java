package ***REMOVED***.db.corner.oracleconn;

import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ***REMOVED***.db.corner.dbconn.ConnectionManager;

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
	
}
