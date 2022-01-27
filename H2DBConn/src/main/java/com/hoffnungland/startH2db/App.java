package com.hoffnungland.startH2db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App 
{
	private static final Logger logger = LogManager.getLogger(App.class);
	
    public static void main( String[] args )
    {
    	logger.traceEntry();
    	try {
    		
    		String keyStorePasswordVariable = System.getProperty("keyStorePasswordVariable");
    		if(keyStorePasswordVariable != null) {
    			String keyStorePassword = System.getenv(keyStorePasswordVariable);
    			System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
    		}
    		
    		for(int argIdx = 0; argIdx < args.length; argIdx++) {
    			switch(args[argIdx]) {
    			case "-tcpPassword":
    			case "-webAdminPassword":
    				String passwdPropertyVariable = args[argIdx+1];
    				String password = System.getenv(passwdPropertyVariable);
    				args[argIdx+1] = password;
    				break;
    			}
    		}
    		
			org.h2.tools.Server.main(args);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(e);
		}
    	logger.traceExit();
    }
}
