package me.hoffnungland.db.corner.javadbconn;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateDB {

	private static final Logger logger = LogManager.getLogger(CreateDB.class);
	
	public static void main(String[] args) {
		Properties connectionProps = new Properties();
		connectionProps.put("user", "");
		connectionProps.put("password", "");
		//connectionProps.put("derby.language.sequence.preallocator", "1");

		String dbms = "derby";
		String dbName = "db/testJavaDBConn";
		//java -jar %DERBY_HOME%\lib\derbyrun.jar ij
		//CONNECT 'jdbc:derby:C:/eclipse-jee-mars-2-win32-x86_64/workspace/DBCorner/JavaDBConn/testJavaDBConn jdbc:derby:testJavaDBConn;create=true';
		//DISCONNECT;
		//EXIT;
		String urlString = "jdbc:" + dbms + ":" + dbName + ";create=true";
		//String urlString = "jdbc:" + dbms + ":" + dbName;
		connectionProps.put("URL", urlString);
		
		JdbLocalManager dbManager = null;
		try {
			dbManager = new JdbLocalManager();
			dbManager.connect(urlString, connectionProps);
			
			//EXECUTE CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('derby.language.sequence.preallocator','1');
			CallableStatement stmSet = dbManager.getCallableStatement("SYSCS_SET_DATABASE_PROPERTY.sql").getStm();
			stmSet.setString(1, "derby.language.sequence.preallocator");
			stmSet.setString(2, "1");
			stmSet.execute();
			CallableStatement stmGet = dbManager.getCallableStatement("SYSCS_GET_DATABASE_PROPERTY.sql").getStm();
			stmGet.registerOutParameter(1, java.sql.Types.VARCHAR);
			stmGet.setString(2, "derby.language.sequence.preallocator");
			stmGet.execute();
			
			logger.debug("derby.language.sequence.preallocator: " + stmGet.getString(1));
			
			dbManager.commit();
			
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}finally {
			if(dbManager != null){
				dbManager.disconnect();
			}
		}

	}

}
