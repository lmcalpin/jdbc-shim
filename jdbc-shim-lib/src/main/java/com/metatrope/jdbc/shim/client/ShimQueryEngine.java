package com.metatrope.jdbc.shim.client;

import com.metatrope.jdbc.common.JdbcUrl;
import com.metatrope.jdbc.common.QueryEngine;
import com.metatrope.jdbc.common.model.SqlRequest;
import com.metatrope.jdbc.common.model.SqlResponse;

import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.Duration;

public class ShimQueryEngine implements QueryEngine {
    private final JdbcUrl jdbcUrl;

    public ShimQueryEngine(JdbcUrl jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    @Override
    public SqlResponse executeQuery(SqlRequest sqlRequest) throws SQLException {
        HttpRequest httpRequest;
        try {
            httpRequest = HttpRequest.newBuilder().uri(new URI(jdbcUrl.getServiceRoot())).timeout(Duration.ofMinutes(2)).header("Content-Type", "application/json").POST(BodyPublishers.ofFile(Paths.get("file.json"))).build();
            HttpClient client = HttpClient.newBuilder().version(Version.HTTP_1_1).followRedirects(Redirect.NORMAL).connectTimeout(Duration.ofSeconds(20)).proxy(ProxySelector.of(new InetSocketAddress("proxy.example.com", 80)))
                    .authenticator(Authenticator.getDefault()).build();
            HttpResponse<String> response = client.send(httpRequest, BodyHandlers.ofString());
            System.out.println(response);
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw new SQLException(e);
        }
        return null;
    }

}
