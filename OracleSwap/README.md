# OracleSwap

OracleSwap take benefits from OracleConn XDK wrapper to move data from an Oracle database to another.

The App main method has the following input paramters:
* source db connection name
* target db connection name
* list of tables (whitespace separated values)

The algorithm assumes the target table DDL is the same (or fit) the source table DDL.

## Property files
Connection file name must be in etc/connections directory and has the same rules listed in OracleConn.

## Create a new project
	mvn archetype:generate -Dfilter=maven-archetype-quickstart -DgroupId=com.hoffnungland -DartifactId=OracleSwap -Dpackage=com.hoffnungland.db.corner.oracleswap -Dversion=0.0.1-SNAPSHOT

## Build settings
### Remove junit:junit:3.8.1

## Relationship
### Add the dependencies
#### Oracle jdbc dependencies
[Add the Oracle Maven Repository](http://docs.oracle.com/middleware/1213/core/MAVEN/config_maven_repo.htm#MAVEN9010)

#### Instruction to encrypt the password on maven settings.xml
[Encryption guide](http://maven.apache.org/guides/mini/guide-encryption.html)<br>
Add log4j, jdbc e POI update jUnit<br>


	<dependencies>
		<dependency>
			<groupId>com.hoffnungland</groupId>
			<artifactId>OracleConn</artifactId>
			<version>0.0.11</version>
		</dependency>
	</dependencies>

# Run with Maven
	
	start mvn exec:java -Dexec.mainClass="com.hoffnungland.db.corner.oracleswap.App" -Dlog4j.configurationFile=src/main/resources/log4j2.xml

# Create Jar with dependencies

## Configure the pom.xml

	<plugin>
		<artifactId>maven-assembly-plugin</artifactId>
		<configuration>
			<descriptorRefs>
				<descriptorRef>jar-with-dependencies</descriptorRef>
			</descriptorRefs>
			<appendAssemblyId>false</appendAssemblyId>
			<finalName>${project.artifactId}</finalName>
			<archive>
				<manifest>
					<mainClass>com.hoffnungland.db.corner.oracleswap.App</mainClass>
				</manifest>
			</archive>
		</configuration>
	</plugin>

## Execute the maven assembly single

	mvn assembly:single

#add .gitignore to mandatory empty directory
	# Ignore everything in this directory
	*
	# Except this file
	!.gitignore
