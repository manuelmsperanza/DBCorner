# JavaDBConn

`JavaDBConn` provides Derby embedded and client JDBC connection managers. It is maintained in the repository but is currently excluded from the root Maven reactor.

## Build and test

Build and install the shared parent/database module first, then run the Derby module:

```shell
mvn -pl DBConn install
mvn -f JavaDBConn/pom.xml test
```

Generate the module's Javadocs with:

```shell
mvn -f JavaDBConn/pom.xml javadoc:javadoc
```

`JdbcLocalManager` uses Derby's embedded driver; `JdbcClientManager` uses the network client driver. `JdbcConnectionManager.getNextVal` retrieves a Derby sequence value. Derby sequences and local database paths must exist or be created before use.

See the [root README](../README.md) for project-wide requirements and connection handling guidance.
