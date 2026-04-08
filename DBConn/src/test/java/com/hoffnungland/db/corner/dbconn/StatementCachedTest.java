package com.hoffnungland.db.corner.dbconn;

import java.sql.PreparedStatement;

import org.junit.Assert;
import org.junit.Test;

public class StatementCachedTest {

	@Test
	public void closeClosesUnderlyingStatement() throws Exception {
		JdbcTestSupport.TestPreparedStatement preparedStatement = new JdbcTestSupport.TestPreparedStatement("SELECT 1", null);
		StatementCached<PreparedStatement> cached = new StatementCached<PreparedStatement>();
		cached.setName("sample");
		cached.setStm(preparedStatement.proxy());

		cached.close();

		Assert.assertEquals("sample", cached.getName());
		Assert.assertSame(preparedStatement.proxy(), cached.getStm());
		Assert.assertEquals(1, preparedStatement.closeCalls);
	}

	@Test
	public void closeDoesNothingWhenStatementIsMissing() throws Exception {
		StatementCached<PreparedStatement> cached = new StatementCached<PreparedStatement>();

		cached.close();

		Assert.assertNull(cached.getStm());
	}
}
