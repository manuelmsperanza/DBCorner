# OracleConn

The project extends the DBConn functionalities, providing the Oracle DB implementation.

Furthermore, you can:
* create a connection pool.
* use the Oracle XML Developer's Kit

OrclConnectionManager use both Oracle XML Developer's Kit and BMS_XMLGEN and DBMS_XMLSTORE PL/SQL package functions:
* getXmlOfQuery uses DBMS_XMLGEN.GETXML
* getFullXmlOfQuery uses DBMS_XMLGEN.GETXML
* xmlSave (having Reader content input parameter) uses DBMS_XMLSTORE.insertXML
* xmlUpdate (having Reader content input parameter) uses DBMS_XMLSTORE.updateXML
* xmlFullUpdate uses DBMS_XMLSTORE.updateXML
* xmlDelete uses DBMS_XMLSTORE.deleteXML
* xmlSave (having Document doc input parameter) uses oracle.xml.sql.dml.OracleXMLSave
* xmlSave (having String xml input parameter) uses oracle.xml.sql.dml.OracleXMLSave
* xmlQueryDocument uses oracle.xml.sql.query.OracleXMLQuery
* xmlQuery uses oracle.xml.sql.query.OracleXMLQuery
* xmlUpdate (having Document doc input parameter) uses oracle.xml.sql.dml.OracleXMLSave
* xmlUpdate (having String xml input parameter) uses oracle.xml.sql.dml.OracleXMLSave

## Property file

Conventionally the property file can be named as follow: environment_name.properties.
The file contains these properties

* URL: jdbc:oracle:thin:@database_specifier
* user: schema name
* password: schema password
* MinPoolSiz: connection pool minimum
* MaxPoolSize: connection pool maximum size

Database specifier can be:
* host_name\[:port\]\[:SID\]
* //host_name\[:port\]\[/Service Name\]
* a plain TNS entry: (DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=host_name)(PORT=port))(CONNECT_DATA=(SERVICE_NAME or SID= Service Name or SID)))


# Create a new project
	
	mvn archetype:generate -Dfilter="org.apache.maven.archetypes:maven-archetype-quickstart" -DgroupId="com.hoffnungland" -DartifactId=OracleConn -Dpackage="com.hoffnungland.db.corner.oracleconn" -Dversion="0.0.1-SNAPSHOT"
	
#Build settings
##Remove junit:junit:3.8.1

#Relationship
##Add the dependencies
###Oracle jdbc dependencies
[Add the Oracle Maven Repository](http://docs.oracle.com/middleware/1213/core/MAVEN/config_maven_repo.htm#MAVEN9010)
###Instruction to encrypt the password on maven settings.xml
[Encryption guide](http://maven.apache.org/guides/mini/guide-encryption.html)<br>
Add log4j, jdbc e POI update jUnit<br>


	<dependencies>
		<dependency>
			<groupId>com.hoffnungland</groupId>
			<artifactId>DBConn</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
		<!-- https://maven.oracle.com -->
		<dependency>
			<groupId>com.oracle.jdbc</groupId>
			<artifactId>ojdbc7</artifactId>
			<version>12.1.0.2</version>
		</dependency>
	</dependencies>

#add .gitignore to mandatory empty directory
	# Ignore everything in this directory
	*
	# Except this file
	!.gitignore
