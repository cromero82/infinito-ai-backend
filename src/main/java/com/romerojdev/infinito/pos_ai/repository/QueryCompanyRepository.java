package com.romerojdev.infinito.pos_ai.repository;

import com.romerojdev.infinito.pos_ai.model.Company;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface QueryCompanyRepository extends MongoRepository<Company, String> {
    Optional<Company> findById(Long id);

    Optional<Company> findByNameIgnoreCase(String name);
}
