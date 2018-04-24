package net.dtdns.hoffunungland.db.corner.dbconn;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatementCached<T extends PreparedStatement> {
	
	private static final Logger logger = LogManager.getLogger(StatementCached.class);
	
	private String name;
	private T stm;
	
	
	
	/**
	 * @return the query name
	 * @author manuel.m.speranza
	 * @since 31-08-2016
	 */
	public String getName() {
		logger.traceEntry();
		return this.name;
	}
	
	/**
	 * @param name
	 * @author manuel.m.speranza
	 * @since 21-09-2016
	 */
	void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the SQL statement
	 * @author manuel.m.speranza
	 * @since 31-08-2016
	 */
	public T getStm() {
		return this.stm;
	}
	
	/**
	 * @param stm
	 * @author manuel.m.speranza
	 * @since 04-05-2017
	 */
	void setStm(T stm) {
		this.stm = stm;
	}
	
	/**
	 * Close the current statement
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
