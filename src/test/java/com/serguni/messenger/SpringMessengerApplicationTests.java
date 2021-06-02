package com.serguni.messenger;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class SpringMessengerApplicationTests {

    @Test
    void main() {
        assertDoesNotThrow(() -> SpringMessengerApplication.main(new String[0]));
    }

}
