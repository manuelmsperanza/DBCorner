#Create a new project
mvn archetype:generate -DarchetypeCatalog=http://repo.maven.apache.org/maven2/archetype-catalog.xml -Dfilter=maven-archetype-quickstart -DgroupId=net.dtdns.hoffunungland -DartifactId=JavaDBConn -Dpackage=net.dtdns.hoffunungland.db.corner.javadbconn -Dversion=0.0.1-SNAPSHOT
#Build settings
##Remove junit:junit:3.8.1


#Relationship
##Add the dependencies
###Derby jdbc dependencies

	<dependencies>
		<dependency>
			<groupId>net.dtdns.hoffunungland</groupId>
			<artifactId>DBConn</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
		<!-- https://mvnrepository.com/artifact/org.apache.derby/derby -->
		<dependency>
			<groupId>org.apache.derby</groupId>
			<artifactId>derby</artifactId>
			<version>10.13.1.1</version>
		</dependency>
	</dependencies>