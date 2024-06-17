package com.metatrope.jdbc.shim.server;

import com.metatrope.jdbc.common.model.SqlRequest;
import com.metatrope.jdbc.common.model.SqlResponse;
import com.metatrope.jdbc.shim.ShimDriver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SqlController {
    private static final Logger logger = Logger.getLogger(ShimDriver.class.getName());

    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/")
    public String index() {
        return "";
    }

    private ResultSetExtractor<SqlResponse> resultSetExtractor = new ResultSetExtractor<>() {
        @Override
        public SqlResponse extractData(ResultSet rs) {
            SqlResponse res = new SqlResponse();
            try {
                int columnCount = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Object[] row = new Object[columnCount];
                    for (int i = 0; i < columnCount; i++) {
                        Object val = rs.getObject(i + 1);
                        row[i] = val;
                    }
                    res.getResults().add(Arrays.asList(row));
                }
            } catch (SQLException e) {
                res.setError(e.getMessage());
            }
            return res;
        }
    };

    @PostMapping("/statement")
    public SqlResponse execute(@RequestBody SqlRequest request) {
        SqlResponse response;
        try {
            response = jdbcTemplate.query(request.getSql(), resultSetExtractor);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception handling request", e);
            response = new SqlResponse(e);
        }
        return response;
    }

}