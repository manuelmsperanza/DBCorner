package com.hoffnungland.db.corner.oracleconn;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

/**
 * Manage the connection pool with the Oracle database.
 * @author manuel.m.speranza
 * @since 20-02-2018
 * @version 0.1
 */

public class OracleUConnectionPoolManager {

	private static final Logger logger = LogManager.getLogger(OracleUConnectionPoolManager.class);
	protected PoolDataSource pds;
	/**
	 * Actually connect to the database. Disconnect the previous connection if open.
	 * @param connectionPropertyPath the path of the property file containing the connection string
	 * @throws IOException
	 * @throws SQLException
	 * @author manuel.m.speranza
	 * @since 20-02-2018
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
	 * @param connectionPropsFile The properties file containing at least user, password, minPoolSize and maxPoolSize
	 * @throws SQLException
	 * @author manuel.m.speranza
	 * @since 20-02-2018
	 */

	public void connect(String URL, Properties connectionPropsFile) throws SQLException{

		logger.traceEntry();
		
		logger.debug("Connecting to " + URL);
		this.pds = PoolDataSourceFactory.getPoolDataSource();
		this.pds.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
		this.pds.setURL(URL);
		String username = connectionPropsFile.getProperty("user", null);
		this.pds.setUser(username);
		String password = connectionPropsFile.getProperty("password", null);
		this.pds.setPassword(password);
		//String connectionPoolName = connectionPropsFile.getProperty("ConnectionPoolName", null);
		//this.pds.setConnectionPoolName(connectionPoolName);
		String minPoolSize = connectionPropsFile.getProperty("MinPoolSize", null);
		this.pds.setMinPoolSize(Integer.valueOf(minPoolSize));
		String maxPoolSize = connectionPropsFile.getProperty("MaxPoolSize", null);
		this.pds.setMaxPoolSize(Integer.valueOf(maxPoolSize));
		
		logger.traceExit();
	}
	
	/**
	 * @throws SQLException 
	 * @author manuel.m.speranza
	 * @since 20-02-2018
	 */
	
	public synchronized OrclConnectionManager getConnection() throws SQLException {
		logger.traceEntry();
		OrclConnectionManager connMng = new OrclConnectionManager();
		Connection conn = pds.getConnection();
		
		connMng.setConnection(conn);
		
		return logger.traceExit(connMng);
		
	}
	
	
	/**
	 * 
	 * @return the minimum pool size
	 * @author manuel.m.speranza
	 * @since 21-02-2018
	 */
	public int getMinPoolSize(){
		return logger.traceExit(this.pds.getMinPoolSize());
	}
	
	/**
	 * 
	 * @return the maximum pool size
	 * @author manuel.m.speranza
	 * @since 21-02-2018
	 */
	public int getMaxPoolSize(){
		return logger.traceExit(this.pds.getMaxPoolSize());
	}
	
	/**
	 * 
	 * @return the available connections count
	 * @author manuel.m.speranza
	 * @since 21-02-2018
	 */
	public int getAvailableConnectionsCount(){
		return logger.traceExit(this.pds.getMaxPoolSize());
	}
	
	
}
