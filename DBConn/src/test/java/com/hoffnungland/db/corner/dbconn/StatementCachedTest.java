package com.hoffnungland.db.corner.dbconn;

import java.sql.PreparedStatement;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
public class StatementCachedTest {

	@Test
	public void closeClosesUnderlyingStatement() throws Exception {
		JdbcTestSupport.TestPreparedStatement preparedStatement = new JdbcTestSupport.TestPreparedStatement("SELECT 1", null);
		StatementCached<PreparedStatement> cached = new StatementCached<PreparedStatement>();
		cached.setName("sample");
		cached.setStm(preparedStatement.proxy());

		cached.close();

		assertEquals("sample", cached.getName());
		assertSame(preparedStatement.proxy(), cached.getStm());
		assertEquals(1, preparedStatement.closeCalls);
	}

	@Test
	public void closeDoesNothingWhenStatementIsMissing() throws Exception {
		StatementCached<PreparedStatement> cached = new StatementCached<PreparedStatement>();

		cached.close();

		assertNull(cached.getStm());
	}
}
