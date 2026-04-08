# Features

DBCorner is a multi-module JDBC utility library centered on reusable connection and statement management.

## Core capabilities

- Open JDBC connections from a properties file or an in-memory `Properties` object.
- Manage transactions with explicit `commit()` and `rollback()` support.
- Cache prepared and callable statements by query identifier.
- Load SQL text from files and bind it to cached statements.
- Build dynamic "junction" queries by concatenating SQL fragments returned from a seed query.

## Database modules

- `DBConn`: shared connection and statement-cache infrastructure.
- `H2DBConn`: H2-specific connection manager registration.
- `PGDBConn`: PostgreSQL-specific connection manager registration.
- `OracleConn`: Oracle-specific connection managers, connection pooling, and XML import/export helpers.
- `OracleSwap`: command-line utilities for moving Oracle table data through XML workflows.
- `JavaDBConn`: Derby-focused implementation kept in the repository but currently not enabled in the root Maven reactor.

## Oracle-specific support

- Oracle Universal Connection Pool integration.
- XML extraction through `DBMS_XMLGEN`.
- XML insert, update, and delete flows through `DBMS_XMLSTORE`.
- XML save and query helpers built on Oracle XDK classes such as `OracleXMLSave` and `OracleXMLQuery`.

## Packaging notes

- Root project is a Maven aggregator.
- Runtime logging is provided through Log4j 2.
- Each database module extends the shared `DBConn` abstractions instead of duplicating connection logic.
