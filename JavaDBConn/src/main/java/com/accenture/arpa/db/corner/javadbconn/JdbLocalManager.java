package ***REMOVED***.db.corner.javadbconn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ***REMOVED***.db.corner.dbconn.ConnectionManager;

public class JdbLocalManager extends JdbConnectionManager {
	
	private static final Logger logger = LogManager.getLogger(JdbLocalManager.class);
	
	static {
		ConnectionManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());
	}
}
