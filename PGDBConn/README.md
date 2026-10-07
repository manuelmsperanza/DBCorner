# PGDBConn

`PGDBConn` provides `PgConnectionManager`, a `DBConn.ConnectionManager` configured with the PostgreSQL JDBC driver.

```java
Properties properties = new Properties();
properties.setProperty("user", "example_user");
properties.setProperty("password", "replace_me");

try (PgConnectionManager manager = new PgConnectionManager()) {
    manager.connect("jdbc:postgresql://localhost:5432/example", properties);
    // Use the shared query, statement-cache, and transaction APIs.
    manager.commit();
}
```

Run tests from the repository root with `mvn -pl PGDBConn test`. See the [root README](../README.md) for requirements and Javadoc generation.
