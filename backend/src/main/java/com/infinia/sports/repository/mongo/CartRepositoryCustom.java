package com.infinia.sports.repository.mongo;

public interface CartRepositoryCustom {
    void deleteByUserIdOrSessionId(String userId, String sessionId);
}
