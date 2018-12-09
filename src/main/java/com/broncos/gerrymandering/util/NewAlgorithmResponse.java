package com.broncos.gerrymandering.util;

import java.util.UUID;

public class NewAlgorithmResponse {

    private UUID sessionId;

    public NewAlgorithmResponse(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public UUID getSessionId() {
        return sessionId;
    }
}

