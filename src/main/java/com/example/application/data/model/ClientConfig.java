package com.example.application.data.model;

public class ClientConfig {

    private String dbApiClientSecret;
    private String gitHubClientSecret;

    public String getDbApiClientSecret() {
        return dbApiClientSecret;
    }

    public void setDbApiClientSecret(String dbApiClientSecret) {
        this.dbApiClientSecret = dbApiClientSecret;
    }

    public String getGitHubClientSecret() {
        return gitHubClientSecret;
    }

    public void setGitHubClientSecret(String gitHubClientSecret) {
        this.gitHubClientSecret = gitHubClientSecret;
    }
}
