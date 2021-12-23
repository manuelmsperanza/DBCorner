# JavaDBConn

The project extends the DBConn functionalities, providing the Derby DB implementation.

## Create a new project
	mvn archetype:generate -Dfilter="org.apache.maven.archetypes:maven-archetype-quickstart" -DgroupId="com.hoffnungland" -DartifactId=H2DBConn -Dpackage="com.hoffnungland.db.corner.h2dbconn" -Dversion="0.0.1-SNAPSHOT"
#Build settings
##Remove junit:junit:3.8.1


#Relationship
##Add the dependencies
###Derby jdbc dependencies

	<dependencies>
		<dependency>
			<groupId>com.hoffnungland</groupId>
			<artifactId>DBConn</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
		<!-- https://mvnrepository.com/artifact/com.h2database/h2 -->
		<dependency>
		    <groupId>com.h2database</groupId>
		    <artifactId>h2</artifactId>
		    <version>1.4.200</version>
		    <scope>test</scope>
		</dependency>
	</dependencies>

#add .gitignore to mandatory empty directory
	# Ignore everything in this directory
	*
	# Except this file
	!.gitignore
