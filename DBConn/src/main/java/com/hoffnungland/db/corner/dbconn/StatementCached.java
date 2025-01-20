package com.hoffnungland.db.corner.dbconn;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents a cached SQL statement.
 * @param <T> Type of the statement (PreparedStatement or CallableStatement)
 */
public class StatementCached<T extends PreparedStatement> {
	
	private static final Logger logger = LogManager.getLogger(StatementCached.class);
	
	private String name;
	private T stm;
	
	
	
	/**
	 * Gets the name of the statement.
	 * @return Statement name
	 * @author manuel.m.speranza
	 * @since 31-08-2016
	 */
	public String getName() {
		logger.traceEntry();
		return this.name;
	}
	
	/**
	 * Sets the name of the statement.
	 * @param name Statement name
	 * @author manuel.m.speranza
	 * @since 21-09-2016
	 */
	void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the SQL statement.
	 * @return SQL statement
	 * @author manuel.m.speranza
	 * @since 31-08-2016
	 */
	public T getStm() {
		return this.stm;
	}
	
	/**
	 * Sets the SQL statement.
	 * @param stm SQL statement
	 * @author manuel.m.speranza
	 * @since 04-05-2017
	 */
	void setStm(T stm) {
		this.stm = stm;
	}
	
	/**
	 * Closes the current statement.
	 * @throws SQLException
	 * @author manuel.m.speranza
	 * @since 04-05-2017
	 */
	public void close() throws SQLException{
		if(stm != null){
			stm.close();
		}
	}
	
}
