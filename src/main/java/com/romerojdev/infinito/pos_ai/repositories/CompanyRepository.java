package com.romerojdev.infinito.pos_ai.repositories;

import com.romerojdev.infinito.pos_ai.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
}

