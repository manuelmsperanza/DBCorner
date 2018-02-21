package ***REMOVED***.db.corner.oracleconn;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TOMOrclUConnPoolManager extends OracleUConnectionPoolManager {
	private static final Logger logger = LogManager.getLogger(TOMOrclUConnPoolManager.class);
	
	/**
	 * @throws SQLException 
	 * @author manuel.m.speranza
	 * @since 20-02-2018
	 */
	
	public synchronized TOMOrclConnManager getConnection() throws SQLException {
		logger.traceEntry();
		TOMOrclConnManager connMng = new TOMOrclConnManager();
		connMng.setConnection(pds.getConnection());
		return logger.traceExit(connMng);
	}
}
