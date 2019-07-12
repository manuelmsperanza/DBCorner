package com.hoffnungland.db.corner.oracleconn;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hoffnungland.db.corner.dbconn.StatementCached;

/**
 * Manage the oracle connection for Technical Order Management systems.
 * @author manuel.m.speranza
 * @since 05-05-2017
 * @version 0.1
 */

public class TOMOrclConnManager extends OrclConnectionManager {

	private static final Logger logger = LogManager.getLogger(TOMOrclConnManager.class);
	public static final String nlsTomDateFormat = "'DD/MM/YYYY HH24:MI:SS'";
	

	/**
	 * Execute the plain query with only one parameter of type long in where condition and save the ResultSet within.
	 * @param queryId corresponds to the relative or absolute path of file containing the SQL statement.
	 * @param objSsoId The service order id. Usually it is the first key of the composite PK and the partition key of table.
	 * @return The cached prepared statement with the new result-set value
	 * @throws SQLException
	 * @throws IOException 
	 * @author manuel.m.speranza
	 * @since 05-05-2017
	 */
	public StatementCached<PreparedStatement> executeSsoBasedQuery(String queryId, long objSsoId) throws SQLException, IOException{
		logger.traceEntry();

		StatementCached<PreparedStatement> prepStm = this.prepStms.getStatementInFile(queryId);
		PreparedStatement stm = prepStm.getStm();
		stm.setLong(1, objSsoId);
		stm.executeQuery();

		return logger.traceExit(prepStm);
	}
	
	/**
	 * Execute the plain statement with only one parameter of type long in where condition.
	 * @param queryId corresponds to the relative or absolute path of file containing the SQL statement.
	 * @param objSsoId The service order id. Usually it is the first key of the composite PK and the partition key of table.
	 * @return The cached prepared statement with the new result-set value
	 * @throws SQLException
	 * @throws IOException 
	 * @author manuel.m.speranza
	 * @since 08-05-2017
	 */
	public StatementCached<CallableStatement> executeSsoBasedCallableStatement(String queryId, long objSsoId) throws SQLException, IOException{
		logger.traceEntry();

		StatementCached<CallableStatement> clbStm = this.cllbStms.getStatementInFile(queryId);
		PreparedStatement stm = clbStm.getStm();
		stm.setLong(1, objSsoId);
		stm.execute();

		return logger.traceExit(clbStm);
	}
	
	/**
	 * Build and execute a SELECT * FROM statement with only one parameter of type long in where condition.
	 * The generated statement is cached and then executed.
	 * @param queryId corresponds to the table name to extract.
	 * @param tableStm the statement you append to SELECT * FROM
	 * @param objSsoId The service order id. Usually it is the first key of the composite PK and the partition key of table. 
	 * @return A cached prepared statement with the new result-set value.
	 * @throws SQLException
	 * @author manuel.m.speranza
	 * @since 11-05-2017
	 */
	public StatementCached<PreparedStatement> executeSsoBasedFullTableQuery(String queryId, String tableStm, long objSsoId) throws SQLException{

		logger.traceEntry();
		
		StatementCached<PreparedStatement> prepStm = super.prepareFullTableQuery(queryId, tableStm);
		
		PreparedStatement stm = prepStm.getStm();
		stm.setLong(1, objSsoId);
		stm.executeQuery();

		return logger.traceExit(prepStm);
	}
	
	
}
