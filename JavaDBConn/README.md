#Create a new project
mvn archetype:generate -DarchetypeCatalog=org.apache.maven.archetypes -Dfilter=maven-archetype-quickstart -DgroupId=me.hoffnungland -DartifactId=JavaDBConn -Dpackage=me.hoffnungland.db.corner.javadbconn -Dversion=0.0.1-SNAPSHOT
#Build settings
##Remove junit:junit:3.8.1


#Relationship
##Add the dependencies
###Derby jdbc dependencies

	<dependencies>
		<dependency>
			<groupId>me.hoffnungland</groupId>
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
