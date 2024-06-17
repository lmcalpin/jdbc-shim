package com.metatrope.jdbc.shim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.metatrope.jdbc.common.model.Parameter;
import com.metatrope.jdbc.common.model.SqlRequest;
import com.metatrope.jdbc.common.model.SqlResponse;
import com.metatrope.jdbc.common.model.Type;
import com.metatrope.jdbc.shim.server.Application;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.com.google.common.collect.Lists;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SqlControllerTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private ServletWebServerApplicationContext webServerAppCtxt;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private String baseUrl;
    private boolean testDataCreated;
    private RestClient restClient = RestClient.create();

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.username", postgres::getUsername);
    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @BeforeEach
    void before() {
        baseUrl = "http://localhost:" + webServerAppCtxt.getWebServer().getPort();
        if (testDataCreated)
            return;
        testDataCreated = true;
        try {
            jdbcTemplate.execute("CREATE DATABASE unittest");
            jdbcTemplate.execute("""
                    CREATE TABLE employees (
                                         id SERIAL PRIMARY KEY,
                                         name VARCHAR(50),
                                         birthdate DATE,
                                         salary NUMERIC CHECK(salary > 0)
                    );
                    """);
        } catch (Exception e) {
            // ignore, they probably already exist
        }
        jdbcTemplate.execute("INSERT INTO employees(name, birthdate, salary) VALUES('John Doe', '06-06-1973', 100000);");
        jdbcTemplate.execute("INSERT INTO employees(name, birthdate, salary) VALUES('Bob Robertson', '06-06-1923', 200000);");
        jdbcTemplate.execute("INSERT INTO employees(name, birthdate, salary) VALUES('Jane Doe', '06-06-1983', 180000);");
        jdbcTemplate.execute("INSERT INTO employees(name, birthdate, salary) VALUES('Jim Smith', '01-01-1985', 120000);");
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Test
    void testRootIsEmpty() {
        String result = restClient.get().uri(baseUrl).retrieve().body(String.class);
        assertTrue(result == null);
    }

    @Test
    void testInvalidQuery() {
        ResponseEntity<Void> result = restClient.post().uri(baseUrl + "/statement").contentType(MediaType.APPLICATION_JSON).body("xxxxx").retrieve().onStatus(status -> status.value() == 400, (request, response) -> {
        }).toBodilessEntity();
        assertTrue(result.getStatusCode() == HttpStatusCode.valueOf(400));
    }

    @Test
    void testExecuteSelectLiteral() {
        SqlResponse result = executeSql("select 12345;");
        assertEquals(1, result.getResults().size());
        List<?> row = ((List<?>) result.getResults().get(0));
        assertEquals(12345, row.get(0));
    }

    @Test
    void testExecuteSelectQuery() {
        SqlResponse result = executeSql("select max(salary) from employees;");
        assertEquals(1, result.getResults().size());
        List<?> row = ((List<?>) result.getResults().get(0));
        assertEquals(200000, row.get(0));
    }

    @Test
    void testExecuteSelectMultipleRows() {
        SqlResponse result = executeSql("select count(name) from employees;");
        List<?> row = ((List<?>) result.getResults().get(0));
        int numRows = (int) row.get(0);
        result = restClient.post().uri(baseUrl + "/statement").contentType(MediaType.APPLICATION_JSON).body("{\"sql\": \"select * from employees;\" }").retrieve().body(SqlResponse.class);
        assertEquals(numRows, result.getResults().size());
    }

    @Test
    void testExecutePreparedStatement() {
        SqlResponse result = executeSql("select * from employees where name = ?", Lists.newArrayList(new Parameter("Jane Doe", Type.STRING)));
        assertEquals(1, result.getResults().size());
        List<?> row = ((List<?>) result.getResults().get(0));
        assertEquals(200000, row.get(0));
    }

    private SqlResponse executeSql(String sql) {
        return executeSql(sql, null);
    }
    
    private SqlResponse executeSql(String sql, List<Parameter> parameters) {
        SqlRequest request = new SqlRequest(sql, parameters);
        SqlResponse result = restClient.post().uri(baseUrl + "/statement").contentType(MediaType.APPLICATION_JSON).body(request).retrieve().body(SqlResponse.class);
        return result;
    }
}
