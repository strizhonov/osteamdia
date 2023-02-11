package com.strizhonovapps.skinsearcher.osteamdia.context;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "app.secure.steam=any")
class AppContextTest {

    @Test
    void contextLoads(@Autowired ApplicationContext context) {
        assertNotNull(context);
    }

}