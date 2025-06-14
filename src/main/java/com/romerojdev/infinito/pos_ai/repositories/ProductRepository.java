package com.romerojdev.infinito.pos_ai.repositories;

import com.romerojdev.infinito.pos_ai.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    Page<Product> findByBarcodeContaining(String barcode, Pageable pageable);
}

