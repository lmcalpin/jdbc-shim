package com.metatrope.jdbc.common;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class JdbcUrl {
    private final String expectedPrefix;
    private final String serviceRoot;
    private final Map<String, String> properties;

    public JdbcUrl(String expectedPrefix, String url) {
        this.expectedPrefix = expectedPrefix;
        String cleanURI = url.substring(expectedPrefix.length());
        URI uri = URI.create(cleanURI);
        this.properties = parseQuery(uri.getQuery());
        this.serviceRoot = cleanURI;
    }

    @Override
    public String toString() {
        return expectedPrefix + serviceRoot;
    }
    
    public String getServiceRoot() {
        return serviceRoot;
    }
    
    public Map<String, String> getProperties() {
        return properties;
    }

    public URI toHttpUri() throws URISyntaxException {
        return new URI(serviceRoot);
    }

    private Map<String, String> parseQuery(String queryString) {
        Map<String, String> properties = new HashMap<>();
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
