package com.hoffnungland.db.corner.oracleconn;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hoffnungland.db.corner.dbconn.StatementCached;

/**
 * Manages the Oracle connection for Technical Order Management systems.
 * Extends OrclConnectionManager.
 * @since 05-05-2017
 * @version 0.1
 */
public class TOMOrclConnManager extends OrclConnectionManager {

	private static final Logger logger = LogManager.getLogger(TOMOrclConnManager.class);
	public static final String nlsTomDateFormat = "'DD/MM/YYYY HH24:MI:SS'";
	
	/**
	 * Executes a plain query with one parameter of type long in the where condition and saves the ResultSet within.
	 * @param queryId the relative or absolute path of the file containing the SQL statement.
	 * @param objSsoId the service order id, usually the first key of the composite PK and the partition key of the table.
	 * @return the cached prepared statement with the new result-set value.
	 * @throws SQLException if a database access error occurs.
	 * @throws IOException if an I/O error occurs.
	 * @author manuel.m.speranza
	 * @since 05-05-2017
	 */
	public StatementCached<PreparedStatement> executeSsoBasedQuery(String queryId, long objSsoId) throws SQLException, IOException {
		logger.traceEntry();
		StatementCached<PreparedStatement> prepStm = this.prepStms.getStatementInFile(queryId);
		PreparedStatement stm = prepStm.getStm();
		stm.setLong(1, objSsoId);
		stm.executeQuery();
		return logger.traceExit(prepStm);
	}
	
	/**
	 * Executes a plain statement with one parameter of type long in the where condition.
	 * @param queryId the relative or absolute path of the file containing the SQL statement.
	 * @param objSsoId the service order id, usually the first key of the composite PK and the partition key of the table.
	 * @return the cached callable statement with the new result-set value.
	 * @throws SQLException if a database access error occurs.
	 * @throws IOException if an I/O error occurs.
	 * @author manuel.m.speranza
	 * @since 08-05-2017
	 */
	public StatementCached<CallableStatement> executeSsoBasedCallableStatement(String queryId, long objSsoId) throws SQLException, IOException {
		logger.traceEntry();
		StatementCached<CallableStatement> clbStm = this.cllbStms.getStatementInFile(queryId);
		PreparedStatement stm = clbStm.getStm();
		stm.setLong(1, objSsoId);
		stm.execute();
		return logger.traceExit(clbStm);
	}
	
	/**
	 * Builds and executes a SELECT * FROM statement with one parameter of type long in the where condition.
	 * The generated statement is cached and then executed.
	 * @param queryId the table name to extract.
	 * @param tableStm the statement to append to SELECT * FROM.
	 * @param objSsoId the service order id, usually the first key of the composite PK and the partition key of the table.
	 * @return a cached prepared statement with the new result-set value.
	 * @throws SQLException if a database access error occurs.
	 * @author manuel.m.speranza
	 * @since 11-05-2017
	 */
	public StatementCached<PreparedStatement> executeSsoBasedFullTableQuery(String queryId, String tableStm, long objSsoId) throws SQLException {
		logger.traceEntry();
		StatementCached<PreparedStatement> prepStm = super.prepareFullTableQuery(queryId, tableStm);
		PreparedStatement stm = prepStm.getStm();
		stm.setLong(1, objSsoId);
		stm.executeQuery();
		return logger.traceExit(prepStm);
	}
}
