# PostgresDBConn

The project extends the DBConn functionalities, providing the PostgreSQL DB implementation.

## Create a new project
	mvn archetype:generate -Dfilter="org.apache.maven.archetypes:maven-archetype-quickstart" -DgroupId="com.hoffnungland" -DartifactId=PGDBConn -Dpackage="com.hoffnungland.db.corner.pgdbconn" -Dversion="0.0.1-SNAPSHOT"
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
		<!-- https://mvnrepository.com/artifact/org.postgresql/postgresql -->
		<dependency>
		    <groupId>org.postgresql</groupId>
		    <artifactId>postgresql</artifactId>
		    <version>42.7.3</version>
		</dependency>
	</dependencies>


## Configure the pom.xml

	<plugin>
		<artifactId>maven-assembly-plugin</artifactId>
		<configuration>
			<descriptorRefs>
				<descriptorRef>jar-with-dependencies</descriptorRef>
			</descriptorRefs>
			<appendAssemblyId>false</appendAssemblyId>
			<finalName>${project.name}</finalName>
			<archive>
				<manifest>
					<mainClass>com.hoffnungland.db.corner.pgdbconn.App</mainClass>
				</manifest>
			</archive>
		</configuration>
	</plugin>

## Execute the maven assembly single

### Get the last tag version
	
	git checkout <<tag name>>

### Create the jar with dependencies

	mvn install assembly:single
	
or 

	mvn package assembly:single

### Come back to the previous commit

	git switch -

### Come back to the main branch
	
	git checkout main

#add .gitignore to mandatory empty directory
	# Ignore everything in this directory
	*
	# Except this file
	!.gitignore
