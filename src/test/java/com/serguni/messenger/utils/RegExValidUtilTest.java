package com.serguni.messenger.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegExValidUtilTest {

    @Test
    void checkStandard() {
        assertTrue(RegExValidUtil.checkStandard("serguni"));
        assertFalse(RegExValidUtil.checkStandard("serguni,-;"));
    }

    @Test
    void checkEmail() {
        assertTrue(RegExValidUtil.checkEmail("serguni@mail.ru"));
        assertFalse(RegExValidUtil.checkEmail("serguni.ru"));
    }
}