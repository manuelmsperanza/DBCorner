# OracleSwap

OracleSwap transfers table data between Oracle databases using the `OracleConn` XML helpers. Source and destination tables must have compatible schemas and the credentials need the required read/write privileges.

## Configuration

Create one properties file per connection under `etc/connections`, for example:

```text
etc/connections/source.properties
etc/connections/target.properties
```

Each file must define the Oracle JDBC `URL`, `user`, and `password`. OracleSwap resolves connection names to these files by appending `.properties`.

## Run

Pass source name, target name, and one or more table names:

```shell
java -cp <application-classpath> com.hoffnungland.db.corner.oracleswap.App source target TABLE_A TABLE_B
```

The application deletes existing destination rows for each table before inserting the source data, then commits the destination transaction. Review the selected tables and target before running; destination data is replaced.

`InventoryGroupExport` is a separate utility for the `INVENTORYGROUP` and `INVENTORYGROUP_CHAR` tables. It uses the same connection configuration and also replaces destination data.

Run the module's unit tests from the repository root with `mvn -pl OracleSwap test`. See the [root README](../README.md) for requirements and Javadoc generation.
