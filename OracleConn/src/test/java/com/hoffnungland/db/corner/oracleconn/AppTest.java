package com.hoffnungland.db.corner.oracleconn;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {

	@Test
	void mainPrintsGreeting() {
		PrintStream originalOut = System.out;
		ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
		try {
			System.setOut(new PrintStream(outBuffer, true, StandardCharsets.UTF_8));
			App.main(new String[0]);
		} finally {
			System.setOut(originalOut);
		}

		assertEquals("Hello world" + System.lineSeparator(), outBuffer.toString(StandardCharsets.UTF_8));
	}
}
