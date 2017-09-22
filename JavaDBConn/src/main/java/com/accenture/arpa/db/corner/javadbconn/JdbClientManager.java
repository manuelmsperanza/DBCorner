package ***REMOVED***.db.corner.javadbconn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ***REMOVED***.db.corner.dbconn.ConnectionManager;

public class JdbClientManager extends JdbConnectionManager {
	
	private static final Logger logger = LogManager.getLogger(JdbClientManager.class);
	
	static {
		ConnectionManager.registerDriver(new org.apache.derby.jdbc.ClientDriver());
	}
	
}
