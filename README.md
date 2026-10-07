# DBCorner

DBCorner is a Java 25 Maven multi-module project providing reusable JDBC connection and statement utilities, with database-specific modules for H2, Oracle, and PostgreSQL. The Derby module is maintained in the repository but is not enabled in the root Maven reactor.

## Modules

| Module | Purpose |
| --- | --- |
| `DBConn` | Shared JDBC connection, transaction, SQL-file, and statement-cache utilities. |
| `H2DBConn` | H2 driver integration and H2 database utilities. |
| `OracleConn` | Oracle JDBC/Universal Connection Pool integration and XML helpers. |
| `OracleSwap` | Command-line Oracle table data transfer utilities. |
| `PGDBConn` | PostgreSQL driver integration. |
| `JavaDBConn` | Derby embedded and client connection managers; build separately. |

## Requirements

- JDK 25
- Maven 3.9 or later

## Build and tests

From the repository root:

```shell
mvn test
```

Build all enabled reactor modules:

```shell
mvn package
```

The Derby module is not part of the root reactor. Install the shared DBConn dependency, then build and test it separately:

```shell
mvn -pl DBConn install
mvn -f JavaDBConn/pom.xml test
```

## API documentation

Generate aggregated Javadocs for the enabled reactor modules:

```shell
mvn javadoc:aggregate
```

The output is written to `target/reports/apidocs`. Generate the Derby module's Javadocs separately with:

```shell
mvn -f JavaDBConn/pom.xml javadoc:javadoc
```

## Connection configuration

Connection managers accept a JDBC URL and `Properties`, or load them from a properties file. Set at least `URL`, `user`, and `password` as required by the database and driver. For Oracle connection pools, also set `MinPoolSize` and `MaxPoolSize`.

Example (keep real credentials out of source control):

```properties
URL=jdbc:postgresql://localhost:5432/example
user=example_user
password=replace_me
```

`DBConn` reads SQL files using the platform default charset; use a relative or absolute path. Use `ConnectionManager` as an `AutoCloseable` resource to close statements and the underlying connection. Explicitly call `commit()` or `rollback()` to finish transactions.

## OracleSwap

OracleSwap expects Oracle connection property files under `etc/connections`, named `<connection-name>.properties`. Invoke its main class with a source connection name, target connection name, and one or more table names:

```shell
java -cp <application-classpath> com.hoffnungland.db.corner.oracleswap.App source target TABLE_A TABLE_B
```

Both databases must be reachable, and destination tables must have compatible schemas. See [OracleSwap](OracleSwap/README.md) for details.

## More information

- [Project features](FEATURE.md)
- [DBConn](DBConn/README.md)
- [H2DBConn](H2DBConn/README.md)
- [OracleConn](OracleConn/README.md)
- [OracleSwap](OracleSwap/README.md)
- [PGDBConn](PGDBConn/README.md)
- [JavaDBConn](JavaDBConn/README.md)
