package com.hoffnungland.db.corner.dbconn;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;

import org.junit.Assert;
import org.junit.Test;

public class StatementsCacheTest {

	private interface UnsupportedPreparedStatement extends PreparedStatement {
	}

	@Test
	public void setQueryFileUsesPreparedStatements() throws Exception {
		JdbcTestSupport.TestConnection connection = JdbcTestSupport.connection();
		StatementsCache<PreparedStatement> cache = new StatementsCache<PreparedStatement>(connection.proxy(), PreparedStatement.class);
		Path sqlFile = createSqlFile("select-all.sql", "SELECT *", "FROM SAMPLE");

		StatementCached<PreparedStatement> cached = cache.setQueryFile(sqlFile.toString());

		Assert.assertEquals("select-all", cached.getName());
		Assert.assertSame(connection.preparedStatements.get(0).proxy(), cached.getStm());
		Assert.assertEquals("SELECT *\nFROM SAMPLE\n", connection.preparedSql.get(0));
	}

	@Test
	public void getStatementInFileReusesCachedStatementAndClosesResultSet() throws Exception {
		JdbcTestSupport.TestConnection connection = JdbcTestSupport.connection();
		StatementsCache<PreparedStatement> cache = new StatementsCache<PreparedStatement>(connection.proxy(), PreparedStatement.class);
		Path sqlFile = createSqlFile("cached.sql", "SELECT 1");
		JdbcTestSupport.TestResultSet resultSet = JdbcTestSupport.resultSet();
		connection.enqueuePreparedResult(resultSet);

		StatementCached<PreparedStatement> first = cache.getStatementInFile(sqlFile.toString());
		StatementCached<PreparedStatement> second = cache.getStatementInFile(sqlFile.toString());

		Assert.assertSame(first, second);
		Assert.assertEquals(1, connection.preparedSql.size());
		Assert.assertEquals(1, resultSet.closeCalls);
	}

	@Test
	public void setQueryFileUsesCallableStatements() throws Exception {
		JdbcTestSupport.TestConnection connection = JdbcTestSupport.connection();
		StatementsCache<CallableStatement> cache = new StatementsCache<CallableStatement>(connection.proxy(), CallableStatement.class);
		Path sqlFile = createSqlFile("invoke.sql", "{call sample_proc()}");

		StatementCached<CallableStatement> cached = cache.setQueryFile(sqlFile.toString());

		Assert.assertEquals("invoke", cached.getName());
		Assert.assertSame(connection.callableStatements.get(0).proxy(), cached.getStm());
		Assert.assertEquals("{call sample_proc()}\n", connection.callableSql.get(0));
	}

	@Test
	public void setQueryFileRejectsUnsupportedStatementTypes() throws Exception {
		JdbcTestSupport.TestConnection connection = JdbcTestSupport.connection();
		StatementsCache<UnsupportedPreparedStatement> cache =
				new StatementsCache<UnsupportedPreparedStatement>(connection.proxy(), UnsupportedPreparedStatement.class);
		Path sqlFile = createSqlFile("unsupported.sql", "SELECT 1");

		NullPointerException exception = Assert.assertThrows(
				NullPointerException.class,
				() -> cache.setQueryFile(sqlFile.toString()));

		Assert.assertTrue(exception.getMessage().contains("not supported"));
	}

	private Path createSqlFile(String fileName, String... lines) throws Exception {
		Path sqlFile = Files.createTempDirectory("dbcorner-statements-cache").resolve(fileName);
		Files.write(sqlFile, String.join("\n", lines).getBytes(StandardCharsets.UTF_8));
		sqlFile.toFile().deleteOnExit();
		return sqlFile;
	}
}
