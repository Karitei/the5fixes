package com.the5fixes;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class The5fixesApplicationTests {

    @Test
    void contextLoads() {
        // Fails fast if any bean, property binding or JPA mapping is broken.
    }


}
