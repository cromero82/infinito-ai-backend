package com.romerojdev.infinito.pos_ai.services;

import com.romerojdev.infinito.pos_ai.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    Page<Product> getAll(String barcodeOrName, Pageable pageable);
}
