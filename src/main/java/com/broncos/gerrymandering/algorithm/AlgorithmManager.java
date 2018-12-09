package com.broncos.gerrymandering.algorithm;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AlgorithmManager {

    private Map<UUID, Algorithm> algorithmBySessionId;
    private static AlgorithmManager manager;

    private AlgorithmManager() {
        algorithmBySessionId = new HashMap<>();
    }

    public static AlgorithmManager getInstance() {
        if (manager == null) {
            manager = new AlgorithmManager();
        }
        return manager;
    }

    public Algorithm getAlgorithm(UUID sessionId) {
        return algorithmBySessionId.get(sessionId);
    }

    public void addAlgorithm(UUID sessionId, Algorithm algorithm) {
        algorithmBySessionId.put(sessionId, algorithm);
    }
}
