package ***REMOVED***.db.corner.dbconn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StatementsCache<T extends PreparedStatement> {
	
	private static final Logger logger = LogManager.getLogger(StatementsCache.class);
	//private static String ls = System.getProperty("line.separator");
	
	private Connection conn;
	final Class<T> typeParameterClass;
	
	HashMap<String, StatementCached<T>> cache = new HashMap<String, StatementCached<T>>();
	
	public StatementsCache(Connection conn, Class<T> typeParameterClass) {
		super();
        this.typeParameterClass = typeParameterClass;
		this.conn = conn;
	}
	
	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}

	public void close(){
		logger.traceEntry();
		for (StatementCached<T> curStm : cache.values()){
			try {
				curStm.close();
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		this.conn = null;
		
		logger.traceExit();
	}
	/**
	 * Retrieve the statement of type T from the cache using the input parameter.
	 * If does not exist, retrieve the file and cache it as oracle prepared statement or callable statement 
	 * @param queryId corresponds to the relative or absolute file path.
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 * @author manuel.m.speranza
	 * @since 04-05-2017
	 */
	public StatementCached<T> getStatementInFile(String queryId) throws IOException, SQLException{
		logger.traceEntry();

		StatementCached<T> prepStm = this.cache.get(queryId);

		if(prepStm == null){
			prepStm = this.setQueryFile(queryId);
			this.cache.put(queryId, prepStm);

		} else {
			
			ResultSet resRs = prepStm.getStm().getResultSet();
			if(resRs != null){
				//resRs.getStatement().close();
				resRs.close();
			}
		}
		
		return logger.traceExit(prepStm);
	}
	
	/**
	 * Parse the queryFile to extract the SQL statement from the content and the name from the path. There is no validation of SQL statement.
	 * @param queryId corresponds to the relative or absolute file path. The file must contain a valid SQL query. The file name (without extension) will be the query name.
	 * @throws IOException
	 * @author manuel.m.speranza
	 * @throws SQLException 
	 * @since 31-08-2016
	 */
	public StatementCached<T> setQueryFile(String queryId) throws IOException, SQLException {
		
		logger.traceEntry();
		File sqlFile = new File(queryId);
		String sqlFileName = sqlFile.getName();
		StatementCached<T> prepStm = new StatementCached<T>();
		
		int suffixPos = sqlFileName.lastIndexOf('.');
		sqlFileName = sqlFileName.substring(0, suffixPos);
		logger.debug("name: " + sqlFileName);
		prepStm.setName(sqlFileName);
		
		BufferedReader reader = new BufferedReader( new FileReader (sqlFile));
		String         line = null;
		StringBuilder  stringBuilder = new StringBuilder();


		while( ( line = reader.readLine() ) != null ) {
			stringBuilder.append( line );
			stringBuilder.append( "\n" );
		}
		reader.close();
		
		String sqlStm = stringBuilder.toString();
		logger.debug("queryStm: " + sqlStm);
		
		if( this.typeParameterClass.equals(CallableStatement.class)){
			@SuppressWarnings("unchecked")
			T stm = (T) this.conn.prepareCall(sqlStm);
			prepStm.setStm(stm);
		}else if(this.typeParameterClass.equals(PreparedStatement.class)){
			@SuppressWarnings("unchecked")
			T stm = (T) this.conn.prepareStatement(sqlStm);
			prepStm.setStm(stm);	
		}else {
			throw new NullPointerException("Oracle statement of type " + this.typeParameterClass.getName() + " not supported.");
		}
		
		
		return logger.traceExit(prepStm);
	}
	
	
}
