package com.metatrope.jdbc.shim;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.metatrope.jdbc.shim.server.Application;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(classes=Application.class)
@TestPropertySource(locations = "classpath:test.properties")
public class EndToEndTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Test
    void test() {
        System.out.println(postgres.getJdbcUrl());
        System.out.println(postgres.getUsername());
        System.out.println(postgres.getPassword());
        
        assertTrue(true);
    }

}
