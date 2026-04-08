package com.hoffnungland.db.corner.oracleswap;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class AppTest {

    @Test
    void mainHandlesMissingArgumentsWithoutThrowing() {
        assertDoesNotThrow(() -> App.main(new String[0]));
    }
}
