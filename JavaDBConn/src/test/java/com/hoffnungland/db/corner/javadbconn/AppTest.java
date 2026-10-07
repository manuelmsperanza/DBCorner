package com.hoffnungland.db.corner.javadbconn;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Properties;

import org.junit.jupiter.api.Test;

class AppTest {

	@Test
	void getNextValReturnsSuccessiveDerbySequenceValues() throws Exception {
		String url = "jdbc:derby:memory:sequence-test;create=true";
		try (JdbcLocalManager manager = new JdbcLocalManager()) {
			manager.connect(url, new Properties());
			manager.getPreparedStm("CREATE SEQUENCE TEST_SEQUENCE START WITH 1 INCREMENT BY 1").execute();
			manager.commit();

			assertEquals(1L, manager.getNextVal("TEST_SEQUENCE"));
			assertEquals(2L, manager.getNextVal("TEST_SEQUENCE"));
		}
	}
}
