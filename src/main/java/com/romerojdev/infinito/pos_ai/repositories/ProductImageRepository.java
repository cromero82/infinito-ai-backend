package com.romerojdev.infinito.pos_ai.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.romerojdev.infinito.pos_ai.models.ProductImage;

public interface ProductImageRepository extends MongoRepository<ProductImage, String> {
    // Use default findById
}
