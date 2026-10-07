# OracleConn

`OracleConn` extends the shared `DBConn` utilities with Oracle JDBC, Universal Connection Pool, and XML functionality.

## Connection properties

An Oracle connection properties file should define:

```properties
URL=jdbc:oracle:thin:@//db-host:1521/service-name
user=schema_name
password=replace_me
```

The URL can also use Oracle's SID or TNS descriptor syntax. To configure a pool, provide:

```properties
MinPoolSize=1
MaxPoolSize=8
```

Use `OracleUConnectionPoolManager` to configure the pool and borrow `OrclConnectionManager` instances. Close borrowed managers when finished; their close returns the connection to the pool. Never store production credentials in the repository.

## Oracle-specific features

`OrclConnectionManager` supports sequence values, CLOBs, XML generation through `DBMS_XMLGEN`, XML insert/update/delete through `DBMS_XMLSTORE`, Oracle XDK XML queries and saves, and table truncation. Its XML methods require the corresponding Oracle database packages and Oracle XDK libraries.

`TOMOrclConnManager` adds parameterized query helpers for Technical Order Management workloads. `TOMOrclUConnPoolManager` returns those specialized managers from a connection pool.

Oracle operations require access to an Oracle database and are not run by the regular unit-test suite. Run the module tests with `mvn -pl OracleConn test`. See the [root README](../README.md) for build and Javadoc instructions.
