package com.hoffnungland.db.corner.pgdbconn;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class AppTest {

    @Test
    void mainPrintsHelloWorld() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(outBuffer, true, StandardCharsets.UTF_8));
            App.main(new String[0]);
        } finally {
            System.setOut(originalOut);
        }

        assertEquals("Hello World!" + System.lineSeparator(), outBuffer.toString(StandardCharsets.UTF_8));
    }
}
