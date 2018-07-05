package me.hoffnungland.db.corner.dbconn;

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
 * Manage the connection with the Oracle database and the statements.
 * @author manuel.m.speranza
 * @since 31-08-2016
 * @version 0.2
 */

public class ConnectionManager {

	private static final Logger logger = LogManager.getLogger(ConnectionManager.class);
	protected static Driver myDriver;
	protected static String ls = System.getProperty("line.separator");

	protected Connection conn;

	protected StatementsCache<PreparedStatement> prepStms;
	protected StatementsCache<PreparedStatement> prepStmsJnt;
	protected StatementsCache<CallableStatement> cllbStms;

	protected static synchronized void registerDriver(Driver myDriver){
		logger.traceEntry();
		ConnectionManager.myDriver = myDriver;
		try {

			DriverManager.registerDriver(ConnectionManager.myDriver);
		} catch (SQLException e) {
			ConnectionManager.myDriver = null;
			logger.error("Error occurred during oracle driver registration.", e);
		}finally{

			logger.traceExit();

		}
	}

	@Override
	protected void finalize() throws Throwable {
		this.disconnect();
		super.finalize();
	}

	/**
	 * Actually connect to the database. Disconnect the previous connection if open.
	 * @param connectionPropertyPath the path of the property file containing the connection string
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
	 * Actually connect to the Oracle database. Disconnect the previous connection if open.
	 * @param URL The jdbc:oracle:thin:@ connection URL
	 * @param connectionPropsFile The properties file containing at least user and password
	 * @throws SQLException
	 * @author manuel.m.speranza
	 * @since 09-05-2017
	 */

	public void connect(String URL, Properties connectionPropsFile) throws SQLException{

		logger.traceEntry();
		if (ConnectionManager.myDriver == null){
			throw new NullPointerException("Oracle driver not registered.");
		}

		if (this.conn != null){
			this.disconnect();
		}
		logger.debug("Connecting to " + URL);
		this.setConnection(DriverManager.getConnection(URL, connectionPropsFile));

		logger.traceExit();
	}
	
	/**
	 * 
	 * @param conn
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
	 * Clear all cached statements and disconnect from the Oracle database.
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
	 * Manage the transaction: execute commit
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
	 * Manage the transaction: execute rollback
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
	 * Retrieve the prepared statement. The first time create the PreparedStatement instance associated to the queryId.  
	 * @param queryId corresponds to the relative or absolute path of file containing the SQL statement.
	 * @return The cached prepared statement with result-set reseted
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
	 * Retrieve the callable statement. The first time create the CallableStatement instance associated to the queryId.  
	 * @param queryId corresponds to the relative or absolute path of file containing the SQL statement.
	 * @return The cached callable statement with result-set reseted 
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
	 * Execute the plain query without parameters in where condition and save the ResultSet within.
	 * @param queryId corresponds to the relative or absolute path of file containing the SQL statement.
	 * @return The cached prepared statement with the new result-set value
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
	 * Execute the input query without parameters and build a new plain statement.
	 * The generated statement (non-cached) is executed.
	 * @param queryId corresponds to the relative or absolute path of file containing the SQL statement.
	 * @return An instance of type cached prepared statement (but non-actually cached in a list) with the new result-set value.
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
	 * Execute the input query without parameters and build a new plain statement.
	 * The generated statement is cached and then executed.
	 * @param queryId corresponds to the relative or absolute path of file containing the SQL statement.
	 * @return A cached prepared statement with the new result-set value.
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
	 * Build a statement to invoke
	 * @param invokeStm
	 * @return A non-cached callable statement.
	 * @throws SQLException
	 * @author manuel.m.speranza
	 * @since 22-05-2017
	 */
	public CallableStatement getCallableStm(String invokeStm) throws SQLException{
		logger.traceEntry();
		return logger.traceExit(this.conn.prepareCall(invokeStm));
	}
	/**
	 * Build a statement
	 * @param tableStm
	 * @return A non-cached prepared statement.
	 * @throws SQLException
	 * @author manuel.m.speranza
	 * @since 22-05-2017
	 */
	public PreparedStatement getPreparedStm(String tableStm) throws SQLException{
		logger.traceEntry();
		return logger.traceExit(this.conn.prepareStatement(tableStm));
	}
	
	/**
	 * Build a statement
	 * The generated statement is cached and then executed.
	 * @param queryId corresponds to the table name to extract.
	 * @param tableStm the statement 
	 * @return A cached prepared statement with the new result-set value.
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
	 * Build a statement to invoke.
	 * The generated statement is cached and then executed.
	 * @param queryId
	 * @param invokeStm
	 * @return
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
	 * Build a SELECT * FROM statement
	 * The generated statement is cached and then executed.
	 * @param queryId corresponds to the table name to extract.
	 * @param tableStm the statement 
	 * @return A cached prepared statement with the new result-set value.
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
	 * Build and execute a SELECT * FROM statement
	 * The generated statement is cached and then executed.
	 * @param queryId corresponds to the table name to extract.
	 * @param tableStm the statement you append to SELECT * FROM
	 * @return A cached prepared statement with the new result-set value.
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
