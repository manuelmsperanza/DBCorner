# DBConn

Generic wrapper implemention of database operations like

* connection
* transaction commit and rollback
* named cache of queries (identifier is queryId containing the file path, absolute or relative)
* creation of dynamic queries with set operator (union, union all...), called junction queries, starting from a query returning a list of tables (having the same columns)


## Create a new project
	mvn archetype:generate -Dfilter="org.apache.maven.archetypes:maven-archetype-quickstart" -DgroupId="com.hoffnungland" -DartifactId=DBConn -Dpackage="com.hoffnungland.db.corner.dbconn" -Dversion="0.0.1-SNAPSHOT"
## Build settings
### Remove junit:junit:3.8.1

#add .gitignore to mandatory empty directory
	# Ignore everything in this directory
	*
	# Except this file
	!.gitignore
