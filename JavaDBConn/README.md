# JavaDBConn

The project extends the DBConn functionalities, providing the Derby DB implementation.

## Create a new project
	mvn archetype:generate -Dfilter="org.apache.maven.archetypes:maven-archetype-quickstart" -DgroupId="com.hoffnungland" -DartifactId=JavaDBConn -Dpackage="com.hoffnungland.db.corner.javadbconn" -Dversion="0.0.1-SNAPSHOT"
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
		<!-- https://mvnrepository.com/artifact/org.apache.derby/derby -->
		<dependency>
			<groupId>org.apache.derby</groupId>
			<artifactId>derby</artifactId>
			<version>10.13.1.1</version>
		</dependency>
	</dependencies>

#add .gitignore to mandatory empty directory
	# Ignore everything in this directory
	*
	# Except this file
	!.gitignore
