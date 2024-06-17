package com.metatrope.jdbc.common.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

public class SqlRequestTest {
    @Test
    public void testSqlRequestIsSerializable() throws JsonProcessingException {
        SqlRequest req = new SqlRequest("SELECT * FROM account;");
        String json = new ObjectMapper().writeValueAsString(req);
        assertEquals("{\"sql\":\"SELECT * FROM account;\",\"parameters\":[]}", json);
    }
}
