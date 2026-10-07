# DBConn

`DBConn` is the shared JDBC layer used by the database-specific modules.

## Capabilities

- Open a JDBC connection from a properties file or JDBC URL and `Properties`.
- Manage transactions with `commit()` and `rollback()`.
- Prepare and cache SQL statements by identifier.
- Load prepared or callable statements from SQL files using `getPreparedStatement` and `getCallableStatement`.
- Build and cache junction queries from a seed query that returns `STM` and `JUNCTION` columns.
- Close cached statements and the connection via `disconnect()` or try-with-resources.

The connection manager disables auto-commit when a connection is assigned. Transaction completion is the caller's responsibility.

```java
try (ConnectionManager manager = new ConnectionManager()) {
    manager.connect("jdbc:some-driver:database", properties);
    StatementCached<PreparedStatement> query =
            manager.executeQuery("sql/select-records.sql");
    // Consume query.getStm().getResultSet().
    manager.commit();
}
```

Database drivers are registered by the corresponding database module. Use `H2ConnectionManager`, `PgConnectionManager`, or `OrclConnectionManager` rather than the generic manager when a driver-specific connection is needed.

Run its unit tests from the repository root with `mvn -pl DBConn test`. See the [root README](../README.md) for requirements and Javadoc generation.
