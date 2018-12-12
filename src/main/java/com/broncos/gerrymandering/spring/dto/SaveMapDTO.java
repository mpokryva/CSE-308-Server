package com.broncos.gerrymandering.spring.dto;

public class SaveMapDTO {

    private String username;
    private String sessionId;

    public SaveMapDTO() { }
    public SaveMapDTO(String username, String sessionId) {
        this.username = username;
        this.sessionId = sessionId;
    }

    public String getUsername() {
        return username;
    }

    public String getSessionId() {
        return sessionId;
    }
}
