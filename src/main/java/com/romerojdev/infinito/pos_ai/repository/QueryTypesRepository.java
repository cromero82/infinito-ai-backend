package com.romerojdev.infinito.pos_ai.repository;

import com.romerojdev.infinito.pos_ai.model.Type;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QueryTypesRepository extends MongoRepository<Type, Integer> {
}
