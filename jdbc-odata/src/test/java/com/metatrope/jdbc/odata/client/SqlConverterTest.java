package com.metatrope.jdbc.odata.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.metatrope.jdbc.common.model.SqlRequest;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.sql.SQLException;

import org.apache.olingo.client.core.ODataClientFactory;
import org.junit.jupiter.api.Test;

public class SqlConverterTest {
    private static final String SERVICE_ROOT = "http://example.odata.com/test";
    SqlConverter converter = new SqlConverter(ODataClientFactory.getClient());
    
    @Test
    public void testConvertSelect() throws SQLException {
        String sql = "SELECT x, y FROM foo";
        URI uri = converter.convert(SERVICE_ROOT, new SqlRequest(sql));
        assertODataQueryIs("/foo?$select=x,y", uri);
    }

    private void assertODataQueryIs(String expected, URI actual) {
        assertEquals(SERVICE_ROOT + expected, URLDecoder.decode(actual.toString(), Charset.defaultCharset()));
    }
}
