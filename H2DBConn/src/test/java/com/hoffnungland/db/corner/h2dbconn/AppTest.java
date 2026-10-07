package com.hoffnungland.db.corner.h2dbconn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.PreparedStatement;
import java.util.Properties;

import org.junit.jupiter.api.Test;

class AppTest {

	@Test
	void h2ConnectionManagerExecutesQueriesAndCommits() throws Exception {
		Properties properties = new Properties();
		properties.setProperty("user", "sa");
		properties.setProperty("password", "");

		try (H2ConnectionManager manager = new H2ConnectionManager()) {
			manager.connect("jdbc:h2:mem:app-test", properties);
			manager.getPreparedStm("CREATE TABLE SAMPLE (SAMPLE_VALUE INTEGER)").execute();
			manager.getPreparedStm("INSERT INTO SAMPLE VALUES (42)").executeUpdate();
			manager.commit();

			PreparedStatement query = manager.prepareQuery("sample-query", "SELECT * FROM SAMPLE").getStm();
			assertTrue(query.executeQuery().next());
			assertEquals(42, query.getResultSet().getInt(1));
		}
	}
}
