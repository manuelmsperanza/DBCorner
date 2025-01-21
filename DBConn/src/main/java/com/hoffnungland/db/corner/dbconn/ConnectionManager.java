package com.hoffnungland.db.corner.dbconn;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages the connection with the database and the statements.
 * @author manuel.m.speranza
 * @since 31-08-2016
 * @version 0.2
 */

public class ConnectionManager implements AutoCloseable {

	private static final Logger logger = LogManager.getLogger(ConnectionManager.class);
	protected static Driver myDriver;
	protected static String ls = System.getProperty("line.separator");

	protected Connection conn;

	protected StatementsCache<PreparedStatement> prepStms;
	protected StatementsCache<PreparedStatement> prepStmsJnt;
	protected StatementsCache<CallableStatement> cllbStms;

	/**
	 * Registers the database driver.
	 * @param myDriver Database driver
	 */
	protected static synchronized void registerDriver(Driver myDriver){
		logger.traceEntry();
		ConnectionManager.myDriver = myDriver;
		try {

			DriverManager.registerDriver(ConnectionManager.myDriver);
		} catch (SQLException e) {
			ConnectionManager.myDriver = null;
			logger.error("Error occurred during driver registration.", e);
		}finally{

			logger.traceExit();

		}
	}

	@Override
	public void close() {
		this.disconnect();
	}

	/**
	 * Connects to the database using the properties file.
	 * @param connectionPropertyPath Path to the properties file
	 * @throws IOException
	 * @throws SQLException
	 * @author manuel.m.speranza
	 * @since 31-08-2016
	 */
	public void connect(String connectionPropertyPath) throws IOException, SQLException{
		logger.traceEntry();

		FileInputStream connectionFile = new FileInputStream(connectionPropertyPath);
		Properties connectionPropsFile = new Properties();
		connectionPropsFile.load(connectionFile);
		connectionFile.close();
		String URL = connectionPropsFile.getProperty("URL", null);
		
		this.connect(URL, connectionPropsFile);

		logger.traceExit();
	}

	/**
	 * Connects to the database using the URL and properties.
	 * @param URL Database URL
	 * @param connectionPropsFile Properties file
	 * @throws SQLException
	 * @author manuel.m.speranza
	 * @since 09-05-2017
	 */
	public void connect(String URL, Properties connectionPropsFile) throws SQLException{

		logger.traceEntry();
		if (ConnectionManager.myDriver == null){
			throw new NullPointerException("Driver not registered.");
		}

		if (this.conn != null){
			this.disconnect();
		}
		logger.debug("Connecting to " + URL);
		this.setConnection(DriverManager.getConnection(URL, connectionPropsFile));

		logger.traceExit();
	}
	
	/**
	 * Sets the database connection.
	 * @param conn Database connection
	 * @throws SQLException
	 * @author manuel.m.speranza
	 * @since 20-02-2018
	 */
	public void setConnection(Connection conn) throws SQLException{
		logger.traceEntry();
		this.conn = conn;
		this.conn.setAutoCommit(false);

		this.prepStms = new StatementsCache<PreparedStatement>(this.conn, PreparedStatement.class);
		this.prepStmsJnt = new StatementsCache<PreparedStatement>(this.conn, PreparedStatement.class);
		this.cllbStms = new StatementsCache<CallableStatement>(this.conn, CallableStatement.class);
		logger.traceExit();
	}

	/**
	 * Disconnects from the database and clears all cached statements.
	 * @author manuel.m.speranza
	 * @since 31-08-2016
	 */
	public void disconnect(){

		logger.traceEntry();
		if(this.conn != null){

			this.prepStms.close();
			this.prepStmsJnt.close();
			this.cllbStms.close();

			try {
				this.conn.close();
			} catch (SQLException e) {
				logger.error("Error occurred during closure of connection.", e);
			}finally{
				this.conn = null;
			}

		}
		logger.traceExit();
	}
	/**
	 * Commits the current transaction.
	 * @throws SQLException
	 * @author manuel.m.speranza
	 * @since 08-05-2017
	 */
	public void commit() throws SQLException{
		logger.traceEntry();
		this.conn.commit();
		logger.traceExit();
	}
	/**
	 * Rolls back the current transaction.
	 * @throws SQLException
	 * @author manuel.m.speranza
	 * @since 08-05-2017
	 */
	public void rollback() throws SQLException{
		logger.traceEntry();
		this.conn.rollback();
		logger.traceExit();
	}

	/**
	 * Retrieves the prepared statement from the cache or loads it from a file.
	 * @param queryId File path of the SQL statement
	 * @return Cached prepared statement
	 * @throws SQLException
	 * @throws IOException
	 * @author manuel.m.speranza
	 * @since 04-05-2017
	 */
	public StatementCached<PreparedStatement> getPreparedStatement(String queryId) throws SQLException, IOException{
		logger.traceEntry();

		StatementCached<PreparedStatement> prepStm = this.prepStms.getStatementInFile(queryId);

		return logger.traceExit(prepStm);
	}

	/**
	 * Retrieves the callable statement from the cache or loads it from a file.
	 * @param queryId File path of the SQL statement
	 * @return Cached callable statement
	 * @throws SQLException
	 * @throws IOException
	 * @author manuel.m.speranza
	 * @since 04-05-2017
	 */
	public StatementCached<CallableStatement> getCallableStatement(String queryId) throws SQLException, IOException{
		logger.traceEntry();

		StatementCached<CallableStatement> cllbStm = this.cllbStms.getStatementInFile(queryId);
		return logger.traceExit(cllbStm);
	}

	/**
	 * Executes the plain query without parameters and returns the cached statement.
	 * @param queryId File path of the SQL statement
	 * @return Cached prepared statement with result set
	 * @throws SQLException
	 * @throws IOException 
	 * @author manuel.m.speranza
	 * @since 31-08-2016
	 */
	public StatementCached<PreparedStatement> executeQuery(String queryId) throws SQLException, IOException{
		logger.traceEntry();

		StatementCached<PreparedStatement> prepStm = this.prepStms.getStatementInFile(queryId);

		prepStm.getStm().executeQuery();

		return logger.traceExit(prepStm);
	}


	/**
	 * Generates and executes the query with junction and returns the cached statement.
	 * @param queryId File path of the SQL statement
	 * @return Cached prepared statement with result set
	 * @throws SQLException
	 * @throws IOException
	 * @author manuel.m.speranza 
	 * @since 10-11-2016
	 */
	public StatementCached<PreparedStatement> generateAndExecuteQueryWithJunction(String queryId) throws SQLException, IOException{

		logger.traceEntry();
		StatementCached<PreparedStatement> buildPrepStm = this.prepStms.getStatementInFile(queryId);

		ResultSet buildResRs = buildPrepStm.getStm().executeQuery();

		StringBuilder  stringBuilder = new StringBuilder();
		String prevStmJunction = null;
		while (buildResRs.next()) {

			if (prevStmJunction != null && !prevStmJunction.equals("")){
				stringBuilder.append( prevStmJunction );
				stringBuilder.append( ConnectionManager.ls );
			}

			String stm = buildResRs.getString("STM");
			stringBuilder.append( stm );
			stringBuilder.append( ConnectionManager.ls );

			prevStmJunction = buildResRs.getString("JUNCTION");
		}

		buildResRs.close();

		String stmBuild = stringBuilder.toString();
		logger.debug("Statement built: " + stmBuild);

		StatementCached<PreparedStatement> prepStm = new StatementCached<PreparedStatement>();

		prepStm.setName(buildPrepStm.getName());

		PreparedStatement stm = this.conn.prepareStatement(stmBuild);
		prepStm.setStm(stm);

		prepStm.getStm().executeQuery();

		return logger.traceExit(prepStm);
	}

	/**
	 * Executes the query with junction and returns the cached statement.
	 * @param queryId File path of the SQL statement
	 * @return Cached prepared statement with result set
	 * @throws SQLException
	 * @throws IOException
	 * @author manuel.m.speranza
	 * @since 04-05-2017
	 */
	public StatementCached<PreparedStatement> executeQueryWithJunction(String queryId) throws SQLException, IOException{

		logger.traceEntry();
		StatementCached<PreparedStatement> prepStm = this.prepStmsJnt.cache.get(queryId);

		if(prepStm == null){

			StatementCached<PreparedStatement> buildPrepStm = this.prepStms.getStatementInFile(queryId);

			ResultSet buildResRs = buildPrepStm.getStm().executeQuery();

			StringBuilder  stringBuilder = new StringBuilder();
			String prevStmJunction = null;
			while (buildResRs.next()) {

				if (prevStmJunction != null && !prevStmJunction.equals("")){
					stringBuilder.append( prevStmJunction );
					stringBuilder.append( ConnectionManager.ls );
				}

				String stm = buildResRs.getString("STM");
				stringBuilder.append( stm );
				stringBuilder.append( ConnectionManager.ls );

				prevStmJunction = buildResRs.getString("JUNCTION");
			}

			buildResRs.close();

			String stmBuild = stringBuilder.toString();
			logger.debug("Statement built: " + stmBuild);

			prepStm = new StatementCached<PreparedStatement>();

			prepStm.setName(buildPrepStm.getName());

			PreparedStatement stm = this.conn.prepareStatement(stmBuild);
			prepStm.setStm(stm);

			this.prepStmsJnt.cache.put(queryId, prepStm);

		}

		prepStm.getStm().executeQuery();

		return logger.traceExit(prepStm);
	}
	/**
	 * Builds a callable statement to invoke.
	 * @param invokeStm SQL statement to invoke
	 * @return Callable statement
	 * @throws SQLException
	 * @author manuel.m.speranza
	 * @since 22-05-2017
	 */
	public CallableStatement getCallableStm(String invokeStm) throws SQLException{
		logger.traceEntry();
		return logger.traceExit(this.conn.prepareCall(invokeStm));
	}
	/**
	 * Builds a prepared statement.
	 * @param tableStm SQL statement
	 * @return Prepared statement
	 * @throws SQLException
	 * @author manuel.m.speranza
	 * @since 22-05-2017
	 */
	public PreparedStatement getPreparedStm(String tableStm) throws SQLException{
		logger.traceEntry();
		return logger.traceExit(this.conn.prepareStatement(tableStm));
	}
	
	/**
	 * Prepares a query and caches the statement.
	 * @param queryId Query identifier
	 * @param tableStm SQL statement
	 * @return Cached prepared statement
	 * @throws SQLException
	 * @author manuel.m.speranza
	 * @since 12-05-2017
	 */
	public StatementCached<PreparedStatement> prepareQuery(String queryId, String tableStm) throws SQLException{
		logger.traceEntry();
		StatementCached<PreparedStatement> prepStm = this.prepStms.cache.get(queryId);

		if(prepStm == null){
			prepStm = new StatementCached<PreparedStatement>();

			prepStm.setName(queryId);

			PreparedStatement stm = this.getPreparedStm(tableStm);
			prepStm.setStm(stm);
			
			this.prepStms.cache.put(queryId, prepStm);
		}
		return logger.traceExit(prepStm);
	}
	
	/**
	 * Prepares an invocation and caches the statement.
	 * @param queryId Query identifier
	 * @param invokeStm SQL statement to invoke
	 * @return Cached callable statement
	 * @throws SQLException
	 * @author manuel.m.speranza
	 * @since 12-05-2017
	 */
	public StatementCached<CallableStatement> prepareInvoke(String queryId, String invokeStm) throws SQLException{
		logger.traceEntry();
		StatementCached<CallableStatement> prepStm = this.cllbStms.cache.get(queryId);

		if(prepStm == null){
			prepStm = new StatementCached<CallableStatement>();

			prepStm.setName(queryId);

			CallableStatement stm = this.getCallableStm(invokeStm);
			prepStm.setStm(stm);
			
			this.cllbStms.cache.put(queryId, prepStm);
		}
		return logger.traceExit(prepStm);
	} 
	
	/**
	 * Prepares a full table query and caches the statement.
	 * @param queryId Query identifier
	 * @param tableStm SQL statement
	 * @return Cached prepared statement
	 * @throws SQLException
	 * @author manuel.m.speranza
	 * @since 11-05-2017
	 */
	public StatementCached<PreparedStatement> prepareFullTableQuery(String queryId, String tableStm) throws SQLException{
		logger.traceEntry();
				
		StatementCached<PreparedStatement> prepStm = this.prepareQuery(queryId, "SELECT * FROM " + tableStm);

		return logger.traceExit(prepStm);
	}
	
	/**
	 * Executes a full table query and returns the cached statement.
	 * @param queryId Query identifier
	 * @param tableStm SQL statement
	 * @return Cached prepared statement with result set
	 * @throws SQLException
	 * @author manuel.m.speranza
	 * @since 04-05-2017
	 */
	public StatementCached<PreparedStatement> executeFullTableQuery(String queryId, String tableStm) throws SQLException{

		logger.traceEntry();
		StatementCached<PreparedStatement> prepStm = this.prepareFullTableQuery(queryId, tableStm);

		prepStm.getStm().executeQuery();

		return logger.traceExit(prepStm);
	}

}
