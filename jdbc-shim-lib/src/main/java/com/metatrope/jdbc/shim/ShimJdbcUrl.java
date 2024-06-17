package com.metatrope.jdbc.shim;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class ShimJdbcUrl {
    private static final String JDBC_URL_PREFIX = "jdbc:";

    private final String scheme;
    private final String host;
    private final int port;
    private final String path;
    private final String user;
    private final String uriAsString;
    private final Map<String, Object> properties;

    public ShimJdbcUrl(String url) {
        String cleanURI = url.substring(JDBC_URL_PREFIX.length());
        URI uri = URI.create(cleanURI);
        this.scheme = uri.getScheme();
        this.host = uri.getHost();
        this.port = uri.getPort();
        this.path = uri.getPath();
        this.user = uri.getUserInfo();
        this.properties = parseQuery(uri.getQuery());
        this.uriAsString = "http://" + host + ":" + port + "/";
    }

    public String getScheme() {
        return scheme;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return uriAsString;
    }

    public URI toHttpUri() throws URISyntaxException {
        return new URI(uriAsString);
    }

    private Map<String, Object> parseQuery(String queryString) {
        Map<String, Object> properties = new HashMap<>();
        if (queryString != null) {
            String[] parameters = queryString.split("&");
            for (String parameter : parameters) {
                String[] argAndValue = parameter.split("=");
                properties.put(argAndValue[0], argAndValue[1]);
            }
        }
        return properties;
    }
}
