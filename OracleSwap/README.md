#Create a new project
mvn archetype:generate -Dfilter=maven-archetype-quickstart -DgroupId=com.hoffnungland -DartifactId=OracleSwap -Dpackage=com.hoffnungland.db.corner.oracleswap -Dversion=0.0.1-SNAPSHOT
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
			<artifactId>OracleConn</artifactId>
			<version>0.0.10</version>
		</dependency>
	</dependencies>

#add .gitignore to mandatory empty directory
	# Ignore everything in this directory
	*
	# Except this file
	!.gitignore
