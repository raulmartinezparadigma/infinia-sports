package com.infinia.sports.repository.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class CartRepositoryImpl implements CartRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void deleteByUserIdOrSessionId(String userId, String sessionId) {
        Criteria criteria = new Criteria().orOperator(
            Criteria.where("userId").is(userId),
            Criteria.where("sessionId").is(sessionId)
        );
        Query query = new Query(criteria);
        mongoTemplate.remove(query, "carts");
    }
}
