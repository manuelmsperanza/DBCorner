package com.hoffnungland.db.corner.dbconn;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConnectionManagerTest {

	@Test
	public void setConnectionDisablesAutoCommitAndInitializesCaches() throws Exception {
		ConnectionManager manager = new ConnectionManager();
		JdbcTestSupport.TestConnection connection = JdbcTestSupport.connection();

		manager.setConnection(connection.proxy());

		assertEquals(Boolean.FALSE, connection.autoCommitValue);
		assertNotNull(manager.prepStms);
		assertNotNull(manager.prepStmsJnt);
		assertNotNull(manager.cllbStms);
	}

	@Test
	public void prepareQueryCachesPreparedStatementByQueryId() throws Exception {
		ConnectionManager manager = new ConnectionManager();
		JdbcTestSupport.TestConnection connection = JdbcTestSupport.connection();
		manager.setConnection(connection.proxy());

		StatementCached<PreparedStatement> first = manager.prepareQuery("sample", "SELECT * FROM sample");
		StatementCached<PreparedStatement> second = manager.prepareQuery("sample", "SELECT * FROM ignored");

		assertSame(first, second);
		assertEquals(1, connection.preparedSql.size());
		assertEquals("SELECT * FROM sample", connection.preparedSql.get(0));
	}

	@Test
	public void prepareInvokeCachesCallableStatementByQueryId() throws Exception {
		ConnectionManager manager = new ConnectionManager();
		JdbcTestSupport.TestConnection connection = JdbcTestSupport.connection();
		manager.setConnection(connection.proxy());

		StatementCached<CallableStatement> first = manager.prepareInvoke("invoke", "{call sample_proc()}");
		StatementCached<CallableStatement> second = manager.prepareInvoke("invoke", "{call ignored_proc()}");

		assertSame(first, second);
		assertEquals(1, connection.callableSql.size());
		assertEquals("{call sample_proc()}", connection.callableSql.get(0));
	}

	@Test
	public void executeQueryWithJunctionBuildsAndCachesCombinedStatement() throws Exception {
		ConnectionManager manager = new ConnectionManager();
		JdbcTestSupport.TestConnection connection = JdbcTestSupport.connection();
		connection.enqueuePreparedResult(JdbcTestSupport.resultSet(
				JdbcTestSupport.row("STM", "SELECT * FROM FIRST", "JUNCTION", "UNION"),
				JdbcTestSupport.row("STM", "SELECT * FROM SECOND", "JUNCTION", "")));
		manager.setConnection(connection.proxy());
		Path sqlFile = createSqlFile("junction.sql", "SELECT STM, JUNCTION FROM BUILD");

		StatementCached<PreparedStatement> first = manager.executeQueryWithJunction(sqlFile.toString());
		StatementCached<PreparedStatement> second = manager.executeQueryWithJunction(sqlFile.toString());

		assertSame(first, second);
		assertEquals(2, connection.preparedSql.size());
		assertEquals("SELECT STM, JUNCTION FROM BUILD\n", connection.preparedSql.get(0));
		assertEquals(
				"SELECT * FROM FIRST" + ConnectionManager.ls
						+ "UNION" + ConnectionManager.ls
						+ "SELECT * FROM SECOND" + ConnectionManager.ls,
				connection.preparedSql.get(1));
		assertEquals(1, connection.preparedStatements.get(0).executeQueryCalls);
		assertEquals(2, connection.preparedStatements.get(1).executeQueryCalls);
		assertEquals(1, connection.preparedStatements.get(0).resultSet.closeCalls);
	}

	@Test
	public void disconnectClosesCachedStatementsAndConnection() throws Exception {
		ConnectionManager manager = new ConnectionManager();
		JdbcTestSupport.TestConnection connection = JdbcTestSupport.connection();
		manager.setConnection(connection.proxy());
		manager.prepareQuery("sample", "SELECT * FROM sample");
		manager.prepareInvoke("invoke", "{call sample_proc()}");

		manager.disconnect();

		assertEquals(1, connection.preparedStatements.get(0).closeCalls);
		assertEquals(1, connection.callableStatements.get(0).closeCalls);
		assertEquals(1, connection.closeCalls);
		assertNull(manager.conn);
	}

	private Path createSqlFile(String fileName, String... lines) throws Exception {
		Path sqlFile = Files.createTempDirectory("dbcorner-connection-manager").resolve(fileName);
		Files.write(sqlFile, String.join("\n", lines).getBytes(StandardCharsets.UTF_8));
		sqlFile.toFile().deleteOnExit();
		return sqlFile;
	}
}
