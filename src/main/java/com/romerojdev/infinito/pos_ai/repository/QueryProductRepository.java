package com.romerojdev.infinito.pos_ai.repository;

import com.romerojdev.infinito.pos_ai.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface QueryProductRepository extends MongoRepository<Product, String> {
    // Find products where any token matches any of the input tokens
    @Query("{'tokens': {$in: ?0}}")
    List<Product> findByTokensIn(List<String> tokens);

    // Find products where all tokens match the input tokens
    @Query("{'tokens': {$all: ?0}}")
    List<Product> findByTokensAll(List<String> tokens);

    // Find products where any token matches any of the input tokens with pagination
    @Query("{'tokens': {$in: ?0}}")
    Page<Product> findByTokensIn(List<String> tokens, Pageable pageable);
}
