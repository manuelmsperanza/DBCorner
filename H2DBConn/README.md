# H2DBConn

`H2DBConn` provides `H2ConnectionManager`, a `DBConn.ConnectionManager` configured with the H2 JDBC driver.

## Connect to H2

```java
Properties properties = new Properties();
properties.setProperty("user", "sa");
properties.setProperty("password", "");

try (H2ConnectionManager manager = new H2ConnectionManager()) {
    manager.connect("jdbc:h2:mem:example;DB_CLOSE_DELAY=-1", properties);
    // Prepare and execute SQL with the shared ConnectionManager API.
    manager.commit();
}
```

For a file database, use an H2 URL such as `jdbc:h2:~/example`. Add suitable H2 encryption options only when using an encrypted database.

Run tests from the repository root with `mvn -pl H2DBConn test`. See the [root README](../README.md) for requirements and Javadoc generation.
