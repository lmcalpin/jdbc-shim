package com.metatrope.jdbc.common;

public class JdbcUrl {
    private final String expectedPrefix;
    private final String serviceRoot;

    public JdbcUrl(String url, String expectedPrefix) {
        this.expectedPrefix = expectedPrefix;
        String cleanURI = url.substring(expectedPrefix.length());
        this.serviceRoot = cleanURI;
    }

    @Override
    public String toString() {
        return expectedPrefix + serviceRoot;
    }
    
    public String getServiceRoot() {
        return serviceRoot;
    }
}
